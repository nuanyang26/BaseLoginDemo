package top.nuanyang26.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Component
public class JwtUtils {

    @Value("${spring.security.jwt.key}")
    String key;

    @Value("${spring.security.jwt.expire}")
    int expire;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    //    springboot的装配顺序  fixed
//    @Resource
//    @Lazy
//    Algorithm algorithm = Algorithm.HMAC256(key);

    //    boolean 判断是否成功让令牌失效
    public boolean invalidateJwt(String headerToken) {
        String token = this.convertToken(headerToken);
        if (token == null) return false;
//        接下来去验证
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = jwtVerifier.verify(token);
            String id = jwt.getId();
            return deleteToken(id, jwt.getExpiresAt());
        } catch (JWTVerificationException e) {
            return false;
        }

    }

    private boolean deleteToken(String uuid, Date date) {
        if (this.isInvalidToken(uuid)) {
            return false;
        }

        Date now = new Date();
        long expire = Math.max(date.getTime() - now.getTime(), 0);
        stringRedisTemplate.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "", expire, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean isInvalidToken(String uuid) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.JWT_BLACK_LIST + uuid));
    }


    public String createJwt(UserDetails details, int id, String username) {
//        塞用户信息
        Date expire = this.expireTime();
        Algorithm algorithm = Algorithm.HMAC256(key);
        return JWT.create()
                //每个令牌都有自己的uuid，拉黑时只需要将uuid拉黑
                .withJWTId(UUID.randomUUID().toString())
//                但UserDetails 里面没有用户id，需要后续查数据库
                .withClaim("id", id)
                .withClaim("name", username)
//                拿到Authorities 是个数组 再通过Authority拿到String的权限
                .withClaim("authorities", details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expire)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }


    public Date expireTime() {
        Calendar calendar = Calendar.getInstance();
//      几小时后过期
        calendar.add(Calendar.HOUR, expire);
        return calendar.getTime();
    }

    public DecodedJWT resolveJwt(String headerToken) {
        String token = this.convertToken(headerToken);
        if (token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();

        try {
//            检查用户是否篡改token，且该异常为RunTime 运行时异常，需要主动捕获
            DecodedJWT verify = jwtVerifier.verify(token);
            //判断token是否在黑名单
            if (this.isInvalidToken(verify.getId())) {
                return null;
            }
            //        token没问题， 加下来检查是否过期
            Date expiresAt = verify.getExpiresAt();
            return new Date().after(expiresAt) ? null : verify;
        } catch (JWTVerificationException e) {
            return null;
        }


    }

    private String convertToken(String headerToken) {
        if (headerToken == null || !headerToken.startsWith("Bearer "))
            return null;
        return headerToken.substring(7);
    }


    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("******")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();

    }

    public Integer toId(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }


}
