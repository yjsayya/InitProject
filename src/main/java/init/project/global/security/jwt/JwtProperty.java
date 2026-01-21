package init.project.global.security.jwt;

import init.project.config.YamlPropertyConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
@PropertySource(value="classpath:jwt/jwt.yml", factory = YamlPropertyConfig.class)
public class JwtProperty {

    private TokenProperties accessToken;
    private TokenProperties refreshToken;

    @Setter
    @Getter
    public static class TokenProperties {
        private String secretKey;
        private long expiredTimeMs;
    }

}