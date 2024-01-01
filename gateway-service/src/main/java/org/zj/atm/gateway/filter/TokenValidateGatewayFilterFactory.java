package org.zj.atm.gateway.filter;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.zj.atm.gateway.common.core.UserConstant;
import org.zj.atm.gateway.common.core.UserInfoDTO;
import org.zj.atm.gateway.common.toolkit.JWTUtil;
import org.zj.atm.gateway.config.Config;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * SpringCloud Gateway Token 拦截器
 * 网关拦截器 用户上下文 以及 UserTransmitFilter 的关系是这样的
 * 所有请求都要经过网关拦截器,如果请求前缀在黑名单上,则要尝试将 Token 中的信息放入请求头 header 中
 * UserTranmitFilter 拦截请求,如果 header 中有 USER_ID_KEY,则将请求头中的信息解析进【用户上下文】
 * 用户上下文 提供 setter 方法便于取得信息
 * 此外，如果请求前缀在黑名单上，并且从请求头中拿不到 token，则判定为恶意请求，直接打回请求
 */
@Component
public class TokenValidateGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {

    public TokenValidateGatewayFilterFactory() {
        super(Config.class);
    }

    /**
     * 注销用户时需要传递 Token
     */
    public static final String DELETION_PATH = "/api/user-service/deletion";

    @Override
    public GatewayFilter apply(Config config) {
        // ServerWebExchange（exchange）是一个包含了 HTTP 请求和响应的上下文对象。它代表了网关处理单个客户端请求的所有信息，包括 HTTP 请求头、请求体、响应头、响应体等
        // GatewayFilterChain（chain）是一个过滤器链。它包含了当前请求要经过的所有过滤器
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String requestPath = request.getPath().toString();
            // 判断请求路径是否在 BlackPathPre 中，如果在则进入下面的 if 块
            if (isPathInBlackPreList(requestPath, config.getBlackPathPre())) {
                String token = request.getHeaders().getFirst("Authorization");
                // TODO 需要验证 Token 是否有效，有可能用户注销了账户，但是 Token 有效期还未过
                UserInfoDTO userInfo = JWTUtil.parseJwtToken(token);
                if (!validateToken(userInfo)) {
                    // 如果 userInfo 为空，则响应 401 表示身份验证失败
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }

                //在 Spring Cloud Gateway 或 Spring WebFlux 应用程序中，由于请求和响应对象是不可变的，
                // 因此无法直接修改它们的属性。相反，你可以使用 mutate() 方法创建一个新的 ServerWebExchange.Builder，
                // 通过该构建器修改请求或响应的属性，然后构建一个新的 ServerWebExchange 对象。
                // 所以如果我们要操作这里的请求和响应对象，就得用链式调用使用 mutate 来获取一个状态一致的对象副本
                // 要注意的是，mutate 方法返回的是一个新的、不可变（immutable）的对象，这个对象是当前对象的一个副本，并且允许对副本进行修改，而不会改变原始对象的状态
                // 所以 mutate 返回的实例副本是可修改的，并且这种修改不会影响到原来的实例
                ServerHttpRequest.Builder builder = exchange.getRequest().mutate().headers(httpHeaders -> {
                    // 将 Token 中的
                    httpHeaders.set(UserConstant.USER_ID_KEY, String.valueOf(userInfo.getUserId()));
                    httpHeaders.set(UserConstant.CARD_ID_KEY, String.valueOf(userInfo.getCardId()));
                    httpHeaders.set(UserConstant.REAL_NAME_KEY, URLEncoder.encode(userInfo.getRealName(), StandardCharsets.UTF_8));
                    httpHeaders.set(UserConstant.USER_TOKEN_KEY, token);
//                    if (Objects.equals(requestPath, DELETION_PATH)) {
//                        httpHeaders.set(UserConstant.USER_TOKEN_KEY, token);
//                    }
                });
                // 将上面创建的 ServerHttpRequest 实例 builder 设置给一个全新的 request 的副本，而这个副本其它状态和原来的实例是一模一样的
                return chain.filter(exchange.mutate().request(builder.build()).build());
            }
            return chain.filter(exchange);
        };
    }

    private boolean isPathInBlackPreList(String requestPath, List<String> blackPathPre) {
        // 如果请求路径不在黑名单中，返回 false
        if (CollectionUtils.isEmpty(blackPathPre)) {
            return false;
        }
        // 如果请求路径匹配黑名单中的某个前缀，返回 true
        return blackPathPre.stream().anyMatch(requestPath::startsWith);
    }

    private boolean validateToken(UserInfoDTO userInfo) {
        return userInfo != null;
    }
}
