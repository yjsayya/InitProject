package init.project.global.security.authentication;

import init.project.domain.users.model.UserAccount;
import init.project.domain.users.repository.UserAccountRepository;
import init.project.global.security.model.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount entity = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        return UserDetailsImpl.from(entity);
    }

}