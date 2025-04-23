package init.project.global.security.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginDTO {

    private Long userId;
    private String email;
    private String password;
    private String userRole;

}