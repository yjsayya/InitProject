package init.project.global.security.authorization;

import init.project.global.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationFilter start");
        // 1. header에서 accessToken 조회하기
        String accessToken = getTokenFromHeader(request);
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        log.info("accessToken: {}", accessToken);
        // 2. token의 유효성 검사
        if (!jwtUtil.checkJwtValidation(accessToken, true)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 3. token에서 정보 조회
        Long userId = Long.parseLong(jwtUtil.getUserIdFromJwt(accessToken, true));
        String userRole = jwtUtil.getUserRoleFromJwt(accessToken, true);

        log.info("userId: {}, userRole: {}", userId, userRole);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userRole));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, authorities);

        // 4. SecurityContextHolder에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

}