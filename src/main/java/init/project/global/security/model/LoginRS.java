package init.project.global.security.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRS {

    private String accessToken;
    private String refreshToken;

}