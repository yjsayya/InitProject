package init.project.global.security.jwt;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
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