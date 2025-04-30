package init.project.domain.test;

import init.project.global.security.jwt.JwtProperty;
import init.project.global.properties.UsersProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

    private final UsersProperty usersProperty;
    private final JwtProperty jwtProperty;
    private final WebClient webClient;

    @PostMapping
    public ResponseEntity<?> test() {
        log.info("name: {}", usersProperty.getInfo().getName());
        log.info("age: {}", usersProperty.getInfo().getAge());
        log.info("processName: {}", usersProperty.getProcessName());

        return ResponseEntity.ok().body(Map.of("code", "200", "message", "success"));
    }

    @GetMapping
    public ResponseEntity<?> webClientTest() {
        ExApiRequest request = ExApiRequest.builder()
                .param1("param1")
                .param2("param2")
                .param3("param3")
                .build();

        String url = "http://localhost:8080/webclient/test";
        ExApiResponse response = webClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExApiResponse.class)
                .block();

        log.info("response: {}", response);

        return ResponseEntity.ok().body(Map.of("code", "200", "message", "success"));
    }

    @GetMapping("/yml")
    public ResponseEntity<?> yamlTest() {
        log.info("accessToken: {}", jwtProperty.getAccessToken().getSecretKey());
        log.info("accessToken: {}", jwtProperty.getAccessToken().getExpiredTimeMs());
        log.info("refreshToken: {}", jwtProperty.getRefreshToken().getSecretKey());
        log.info("refreshToken: {}", jwtProperty.getRefreshToken().getExpiredTimeMs());

        return ResponseEntity.ok().body(Map.of("code", "200", "message", "success"));
    }

}