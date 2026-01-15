package init.project.global.security.authentication;

import init.project.global.security.jwt.JwtProvider;
import init.project.global.security.jwt.JwtRefreshTokenService;
import init.project.global.security.model.LoginRQ;
import init.project.global.security.model.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtRefreshTokenService jwtRefreshTokenService;
    private final JwtProvider jwtProvider;

    @PostMapping
    public ResponseEntity<?> login(@Valid @RequestBody LoginRQ request) {
        try {
            // 1. 검증
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

            Authentication authentication = authenticationManager.authenticate(token);

            UserDetailsImpl loginInfo = (UserDetailsImpl) authentication.getPrincipal();

            Long userId = loginInfo.getUserId();
            String userRole = loginInfo.getUserRole();

            log.info("userId: {}, userRole: {}", userId, userRole);
            // 2. jwt 발급
            String accessToken = jwtProvider.generateAccessToken(userId, userRole);
            String refreshToken = jwtProvider.generateRefreshToken(userId);

            log.info("accessToken: {}", accessToken);
            log.info("[LOGIN-SUCCESS] email: {}", request.getEmail());

            // 3. refreshToken을 저장
            jwtRefreshTokenService.saveOrUpdateRefreshToken(loginInfo.getUserId(), refreshToken);

            // 4. jwt를 쿠키에 넣고 전송
            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(15 * 60) // 15분
                    .build();

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(7 * 24 * 60 * 60) // 7일
                    .build();

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(Map.of(
                            "code", "200",
                            "message", "success"
                    ));
        } catch (AuthenticationException e) {
            log.warn("[LOGIN-FAILURE] 이메일 또는 비밀번호 불일치 - {}", request.getEmail());
            return ResponseEntity
                    .status(401)
                    .body(Map.of("message", "아이디 또는 비밀번호가 올바르지 않습니다."));
        } catch (Exception e) {
            log.error("[LOGIN-ERROR] 로그인 중 예외 발생", e);
            return ResponseEntity
                    .status(500)
                    .body(Map.of("message", "로그인 처리 중 오류가 발생했습니다. 관리자에게 연락 바랍니다."));
        }
    }

}