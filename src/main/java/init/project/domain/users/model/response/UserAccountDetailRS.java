package init.project.domain.users.model.response;

import init.project.domain.users.model.UserAccount;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserAccountDetailRS {

    private String email;
    private String username;
    private LocalDateTime regDt;

    public static UserAccountDetailRS from(UserAccount userAccount) {
        return UserAccountDetailRS.builder()
                .email(userAccount.getEmail())
                .username(userAccount.getUsername())
                .regDt(userAccount.getRegDt())
                .build();
    }

}