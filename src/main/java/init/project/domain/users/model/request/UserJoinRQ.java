package init.project.domain.users.model.request;

import lombok.Getter;

@Getter
public class UserJoinRQ {

    private String email;
    private String password;
    private String username;

}