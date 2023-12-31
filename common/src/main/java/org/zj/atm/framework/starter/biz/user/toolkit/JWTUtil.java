package org.zj.atm.framework.starter.biz.user.toolkit;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.zj.atm.framework.starter.biz.user.core.UserInfoDTO;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.zj.atm.framework.starter.bases.constant.UserConstant.*;

/**
 * JWT 工具类
 */
@Slf4j
public final class JWTUtil {

    private static final long EXPIRATION = 600L;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String ISS = "ATM";
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
        customerUserMap.put(CARD_ID_KEY, userInfo.getCardId());
        customerUserMap.put(REAL_NAME_KEY, userInfo.getRealName());
        String jwtToken = Jwts.builder()
                // 指定用于签名
                .signWith(SignatureAlgorithm.HS512, SECRET)
                // 设置签发时间为当前时间
                .setIssuedAt(new Date())
                // 设置签发人信息
                .setIssuer(ISS)
                // 设置 JWT 的主题为 customerUserMap
                .setSubject(JSON.toJSONString(customerUserMap))
                // 设置过期时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000))
                .compact();
        // 对 JWT 口令拼接一个前缀
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
                // 到期时间
                Date expiration = claims.getExpiration();
                // 如果没有过期
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