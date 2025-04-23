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

    public static <T> ResponseEntity<?> success() {
        ApiResponse<T> body = new ApiResponse<>(HttpStatus.OK.value(),"SUCCESS", null);
        return ResponseEntity.ok(body);
    }

    public static <T> ResponseEntity<?> success(T result) {
        ApiResponse<T> body = new ApiResponse<>(HttpStatus.OK.value(),"SUCCESS", result);
        return ResponseEntity.ok(body);
    }

    public static ResponseEntity<?> error(int code, String message) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(code, message, null));
    }

    public static ResponseEntity<?> error(int code) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(code, null, null));
    }

}