package init.project.global.security.jwt;

import init.project.global.security.model.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private static final String atSecretKey = "awbefzl;xczl;xckvjl;awzkejfkvjl;awzkejfalw;ef;alwefawefyour-access-token-secret-32char+";
    private static final String rfSecretKey = "awbefzl;xczl;xckvjl;awzkejfkvjl;awzkejfalw;ef;alwefawefyour-access-token-secret-32char+";
    private static final long atExpiredTimeMs = 1000 * 60 * 5; // 30분
    private static final long rfExpiredTimeMs = 1000L * 60 * 60 * 24 * 7; // 7일

    public String generateAccessToken(Long userId, String userRole) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + atExpiredTimeMs);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("userRole", userRole)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(getSigningKey(atSecretKey), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + rfExpiredTimeMs);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(getSigningKey(rfSecretKey), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean checkJwtValidation(String token, boolean isAccessToken) {
        try {
            SecretKey key = isAccessToken ? getSigningKey(atSecretKey) : getSigningKey(rfSecretKey);
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("[JWT] 토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            log.error("[JWT] 지원하지 않는 토큰입니다.");
        } catch (MalformedJwtException e) {
            log.error("[JWT] 잘못된 토큰입니다.");
        } catch (SecurityException e) {
            log.error("[JWT] 서명 검증에 실패했습니다.");
        } catch (IllegalArgumentException e) {
            log.error("[JWT] 잘못된 입력입니다.");
        }
        return false;
    }

    public String getUserIdFromJwt(String token, boolean isAccessToken) {
        SecretKey key = isAccessToken ? getSigningKey(atSecretKey) : getSigningKey(rfSecretKey);
        return getClaims(token, key).getSubject();
    }

    public String getUserRoleFromJwt(String token, boolean isAccessToken) {
        SecretKey key = isAccessToken ? getSigningKey(atSecretKey) : getSigningKey(rfSecretKey);
        Object userLv = getClaims(token, key).get("userLv");
        return userLv != null ? userLv.toString() : null;
    }

    private Claims getClaims(String token, SecretKey key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSigningKey(String secretKey) {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

}