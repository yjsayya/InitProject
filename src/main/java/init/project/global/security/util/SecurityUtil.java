package init.project.global.security.util;

import init.project.global.security.model.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static UserDetailsImpl getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("No authenticated user found");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetailsImpl)) {
            throw new IllegalArgumentException("Principal is not an instance of UserDetailsImpl");
        }
        return (UserDetailsImpl) principal;
    }

}