package init.project.global.security.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRQ {

    @NotBlank(message = "이메일은 필수 값입니다.")
    @Size(min=1, max = 50, message="이메일은 50자 이하로 입력해주세요")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "올바른 이메일 형식이 아닙니다."
    )
    private String email;

    @NotBlank(message = "비밀번호는 필수 값입니다.")
//    @Size(min = 8, max = 20)
//    @Pattern(
//            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$",
//            message = "비밀번호는 영문자와 숫자를 포함한 8~20자리여야 합니다."
//    )
    private String password;

}