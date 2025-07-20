package init.project.domain.users.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {

    private Long userId;
    private String email;
    private String password;
    private String username;
    private String userRole;

    private String delYn;
    private LocalDateTime regDt;
    private LocalDateTime uptDt;

}