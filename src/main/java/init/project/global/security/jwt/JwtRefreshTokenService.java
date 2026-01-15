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
    private final JwtProvider jwtProvider;

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

//    @Transactional
//    public String checkRefreshToken(String refreshToken) {
//        if (!jwtProvider.checkJwtValidation(refreshToken, false)) {
//            // TODO: refreshToken이 문제가 있는 경우
//            // LoginPage로 되돌리면 될 듯
//        }
//        return jwtProvider.generateAccessToken()
//    }
    @Transactional
    public void unactiveRefreshToken(Long userId) {
        int cnt = jwtRefreshTokenRepository.deleteByUserId(JwtRefreshToken.builder()
                .userId(userId)
                .build());
        if (cnt < 1) { throw new IllegalArgumentException("SQL ERROR"); }
    }

}