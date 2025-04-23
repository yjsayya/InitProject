package init.project.global.security.authentication;

import init.project.global.security.model.LoginRQ;
import init.project.global.security.model.UserDetailsImpl;
import init.project.global.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> login(@Valid @RequestBody LoginRQ request, HttpServletRequest httpRequest) {
        try {
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

            Authentication authentication = authenticationManager.authenticate(token);

            UserDetailsImpl loginInfo = (UserDetailsImpl) authentication.getPrincipal();

            String accessToken = jwtUtil.generateAccessToken(loginInfo);
            String refreshToken = jwtUtil.generateRefreshToken(loginInfo);

            log.info("[LOGIN-SUCCESS] email: {}", request.getEmail());

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + accessToken)
                    .header("X-Refresh-Token", refreshToken)
                    .body(Map.of(
                            "accessToken", accessToken,
                            "refreshToken", refreshToken
                    ));
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            log.warn("[LOGIN-FAILURE] 이메일 또는 비밀번호 불일치 - {}", request.getEmail());
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "이메일 또는 비밀번호가 올바르지 않습니다."));
        } catch (Exception e) {
            log.error("[LOGIN-ERROR] 로그인 중 예외 발생", e);
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "로그인 처리 중 오류가 발생했습니다. 관리자에게 연락 바랍니다."));
        }
    }

}