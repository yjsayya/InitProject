package init.project.global.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtRefreshTokenService {

    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

    @Transactional(readOnly = true)
    public String findByUserId(Long userId) {
        JwtRefreshToken refreshToken = jwtRefreshTokenRepository.findByUserId(userId).orElse(null);
        return refreshToken != null ? refreshToken.getRefreshToken() : null;
    }

    @Transactional
    public void saveOrUpdateRefreshToken(Long userId, String refreshToken) {
        Optional<JwtRefreshToken> existingToken = jwtRefreshTokenRepository.findByUserId(userId);
        JwtRefreshToken jwtRefreshToken = JwtRefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .build();

        if (existingToken.isPresent()) {
            jwtRefreshTokenRepository.updateRefreshToken(jwtRefreshToken);
        } else {
            jwtRefreshTokenRepository.saveRefreshToken(jwtRefreshToken);
        }
    }

}