package init.project.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class WebInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();

        String fullUrl = (query == null) ? uri : uri + "?" + query;
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String user;
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            user = "ANONYMOUS";
        } else {
            user = authentication.getName();
        }

        log.info("[{}] ({}) {}", user, method, fullUrl);
        return true;
    }

}