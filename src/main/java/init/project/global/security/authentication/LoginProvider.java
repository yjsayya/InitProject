package init.project.global.security.authentication;

import init.project.global.security.model.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginProvider implements AuthenticationProvider {

    private final LoginService loginService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        String email = token.getName();
        String password  = token.getCredentials().toString();

        UserDetailsImpl loginInfo = loginService.loadUserByUsername(email);

        if (!passwordEncoder.matches(password, loginInfo.getPassword())) {
            throw new BadCredentialsException("비밀번호가 올바르지 않습니다.");
        }
        return new UsernamePasswordAuthenticationToken(loginInfo, password, loginInfo.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}