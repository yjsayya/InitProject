package init.project.global.response.errorHandler;

import init.project.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> CustomException(CustomException e) {
        HttpStatus httpStatus = e.getExceptionCode().getHttpStatus();
        int code = e.getExceptionCode().getCode();
        String errorMessage = e.getMessage();

        log.info("[CustomException]: {}", errorMessage);
        return ApiResponse.error(httpStatus, code, errorMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = Objects.requireNonNull(e.getBindingResult()
                .getFieldError())
                .getDefaultMessage();
        log.warn("[MethodArgumentNotValidException] {}", errorMessage);
        return ApiResponse.error(HttpStatus.BAD_REQUEST, 400, errorMessage);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        String errorMessage = e.getMessage();
        log.error("[RuntimeException] error: {}", errorMessage, e);
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 123, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("UnknownException: {}", e.toString(), e);
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 500, "오류가 발생했습니다. 관리자에게 문의해주세요.");
    }

}