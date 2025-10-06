package init.project.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    public static ResponseEntity<?> success() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(200, "success", null));
    }

    public static <T> ResponseEntity<?> success(T result) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(200, "success", result));
    }

    public static ResponseEntity<?> error(HttpStatus httpStatus, int code, String message) {
        return ResponseEntity
                .status(httpStatus)
                .body(new ApiResponse<>(code, message, null));
    }

    public static ResponseEntity<?> error(HttpStatus httpStatus, int code) {
        return ResponseEntity
                .status(httpStatus)
                .body(new ApiResponse<>(code, null, null));
    }

}