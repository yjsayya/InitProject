package init.project.global.response.errorHandler;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ExceptionCode exceptionCode;
    private String message;

    public CustomException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public CustomException(ExceptionCode exceptionCode, String errorMessage) {
        this.message = errorMessage;
        this.exceptionCode = exceptionCode;
    }

    @Override
    public String getMessage() {
        if (message == null) {
            return this.exceptionCode.getMessage();
        }
        return this.message;
    }

}