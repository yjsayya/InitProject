package init.project.global.response.errorHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    // COMMON
    SQL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), "SQL Error"),
    COMMON_ERROR_400(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "Common Error"),
    COMMON_ERROR_404(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "Common Error"),
    COMMON_ERROR_500(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Common Error"),
    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

}