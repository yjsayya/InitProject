package init.project.global.security.authorization;

import init.project.global.response.ApiResponse;
import init.project.global.security.jwt.JwtRefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/refresh")
public class JwtRefreshTokenController {

    private final JwtRefreshTokenService jwtRefreshTokenService;

    @PostMapping
    public ResponseEntity<?> checkRefreshToken(HttpServletRequest request) {
        Cookie refreshCookie = WebUtils.getCookie(request, "refreshToken");
        if (refreshCookie == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not found.");
        }

        String refreshToken = refreshCookie.getValue();
//        String newAccessToken = jwtRefreshTokenService.checkRefreshToken(refreshToken);

        return ApiResponse.success();
    }

}