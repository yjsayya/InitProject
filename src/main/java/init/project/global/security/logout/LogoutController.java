package init.project.global.security.logout;

import init.project.global.response.ApiResponse;
import init.project.global.security.jwt.JwtRefreshTokenService;
import init.project.global.security.util.SecurityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logout")
public class LogoutController {

    private final JwtRefreshTokenService jwtRefreshTokenService;

    @PostMapping
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Long userId = SecurityUtil.getUserInfo().getUserId();

        jwtRefreshTokenService.unactiveRefreshToken(userId);

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        return ApiResponse.success();
    }

}