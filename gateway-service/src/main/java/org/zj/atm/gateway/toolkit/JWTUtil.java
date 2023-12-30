package org.zj.atm.gateway.toolkit;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.zj.atm.gateway.common.constant.UserConstant.*;

/**
 * JWT 工具类
 *
 * @公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
@Slf4j
public final class JWTUtil {

    private static final long EXPIRATION = 86400L;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String ISS = "index12306";
    public static final String SECRET = "SecretKey039245678901232039487623456783092349288901402967890140939827";

    /**
     * 生成用户 Token
     *
     * @param userInfo 用户信息
     * @return 用户访问 Token
     */
    public static String generateAccessToken(UserInfoDTO userInfo) {
        Map<String, Object> customerUserMap = new HashMap<>();
        customerUserMap.put(USER_ID_KEY, userInfo.getUserId());
        customerUserMap.put(USER_NAME_KEY, userInfo.getUsername());
        customerUserMap.put(REAL_NAME_KEY, userInfo.getRealName());
        String jwtToken = Jwts.builder()
                // 指定用于签名的算法和密钥（SignatureAlgorithm.HS512 和 SECRET）
                .signWith(SignatureAlgorithm.HS512, SECRET)
                // 设置 Token 的签发时间为当前时间
                .setIssuedAt(new Date())
                // 设置签发者的信息
                .setIssuer(ISS)
                // 设置 JWT 的主题部分，这里将用户信息 Map 转换为 JSON 字符串并作为主题
                .setSubject(JSON.toJSONString(customerUserMap))
                // 设置 Token 的过期时间为当前时间加上一定的有效期
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000))
                // 将 JWT 构建为一个字符串
                .compact();
        // 对 jwt 口令拼接一个前缀并返回
        return TOKEN_PREFIX + jwtToken;
    }

    /**
     * 解析用户 Token
     *
     * @param jwtToken 用户访问 Token
     * @return 用户信息
     */
    public static UserInfoDTO parseJwtToken(String jwtToken) {
        // 检查传入的 JWT Token 字符串是否有效
        if (StringUtils.hasText(jwtToken)) {
            // 取得 jwt 口令部分
            String actualJwtToken = jwtToken.replace(TOKEN_PREFIX, "");
            try {
                // Jwts.parser 创建一个 JWT 解析器
                //setSigningKey(SECRET) 设置用于验证签名的密钥。
                //parseClaimsJws 解析传入的 JWT Token，如果解析成功则返回一个包含声明（Claims）的对象 claims。
                Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(actualJwtToken).getBody();
                // 从 claims 中获取 Token 的过期时间（expiration）。
                Date expiration = claims.getExpiration();
                // 如果 Token 的过期时间在当前时间之后（即未过期），则进行下一步；否则，抛出 ExpiredJwtException 异常
                if (expiration.after(new Date())) {
                    String subject = claims.getSubject();
                    return JSON.parseObject(subject, UserInfoDTO.class);
                }
            } catch (ExpiredJwtException ignored) {
            } catch (Exception ex) {
                log.error("JWT Token解析失败，请检查", ex);
            }
        }
        return null;
    }
}