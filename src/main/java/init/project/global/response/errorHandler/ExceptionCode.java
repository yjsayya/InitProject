package init.project.global.response.errorHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    SQL_ERROR(500, "SQL Error"),
    ;

    private final int code;
    private final String message;

}