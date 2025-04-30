package init.project.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "users", ignoreInvalidFields = true)
public class UsersProperty {

    private String processName;
    private Info info = new Info();

    @Getter
    @Setter
    public static class Info {
        private String name;
        private String age;
    }

}