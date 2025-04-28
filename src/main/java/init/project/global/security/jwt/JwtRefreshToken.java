package init.project.global.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtRefreshToken {

    private Long id;
    private Long userId;
    private String refreshToken;

    private String delYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}