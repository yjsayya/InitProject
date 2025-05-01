package init.project.global.security.logout;

import init.project.global.security.model.UserDetailsImpl;
import init.project.global.security.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication == null || authentication.getPrincipal() == null) {
            log.warn("로그아웃 요청이 있었지만 인증 정보가 없습니다.");
            // 인증 정보가 없을 경우 401 Unauthorized로 처리
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\": 401, \"message\": \"Unauthorized: No authentication information found.\"}");
            return;
        }

        if (!(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            log.warn("알 수 없는 인증 주체로부터 로그아웃 요청: {}", authentication.getPrincipal());
            // 인증 주체가 예상과 다를 경우 403 Forbidden으로 처리
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
            response.getWriter().write("{\"code\": 403, \"message\": \"Forbidden: Unknown authentication principal.\"}");
            return;
        }

        Long userId = SecurityUtil.getUserInfo().getUserId();
        log.info("[LOGOUT] ({}) Process End.", userId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"code\": 200, \"message\": \"success\"}");
    }

}