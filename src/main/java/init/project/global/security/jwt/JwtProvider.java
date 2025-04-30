package init.project.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperty jwtProperty;

    public String generateAccessToken(Long userId, String userRole) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + jwtProperty.getAccessToken().getExpiredTimeMs());
        String secretKey = jwtProperty.getAccessToken().getSecretKey();

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("userRole", userRole)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(getSigningKey(secretKey), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + jwtProperty.getRefreshToken().getExpiredTimeMs());
        String secretKey = jwtProperty.getRefreshToken().getSecretKey();

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(getSigningKey(secretKey), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean checkJwtValidation(String token, boolean isAccessToken) {
        String atSecretKey = jwtProperty.getAccessToken().getSecretKey();
        String rtSecretKey = jwtProperty.getRefreshToken().getSecretKey();

        try {
            SecretKey key = isAccessToken ? getSigningKey(atSecretKey) : getSigningKey(rtSecretKey);
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
        String atSecretKey = jwtProperty.getAccessToken().getSecretKey();
        String rtSecretKey = jwtProperty.getRefreshToken().getSecretKey();

        SecretKey key = isAccessToken ? getSigningKey(atSecretKey) : getSigningKey(rtSecretKey);
        return getClaims(token, key).getSubject();
    }

    public String getUserRoleFromJwt(String token, boolean isAccessToken) {
        String atSecretKey = jwtProperty.getAccessToken().getSecretKey();
        String rtSecretKey = jwtProperty.getRefreshToken().getSecretKey();

        SecretKey key = isAccessToken ? getSigningKey(atSecretKey) : getSigningKey(rtSecretKey);
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