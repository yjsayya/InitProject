package init.project.domain.users.service;

import init.project.domain.users.model.UserAccount;
import init.project.domain.users.model.request.UserJoinRQ;
import init.project.domain.users.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    public void join(UserJoinRQ request) {
        String email = request.getEmail();
        userAccountRepository.findByEmail(email).ifPresent(userAccount -> {
            throw new IllegalArgumentException("이미 존재하는 계정입니다");
        });

        UserAccount userAccount = UserAccount.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .build();

        int updatedCnt = userAccountRepository.save(userAccount);
        if (updatedCnt < 1) { throw new IllegalArgumentException("회원가입실패"); }
    }

}