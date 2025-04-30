package init.project.domain.test;

import init.project.global.properties.UsersProperty;
import init.project.global.response.errorHandler.CustomException;
import lombok.Getter;
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
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

    private final UsersProperty usersProperty;
    private final WebClient webClient;

    @PostMapping
    public ResponseEntity<?> test() {
        log.info("name: {}", usersProperty.getInfo().getName());
        log.info("age: {}", usersProperty.getInfo().getAge());
        log.info("processName: {}", usersProperty.getProcessName());

        return ResponseEntity.ok().build();
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

        return ResponseEntity.ok().build();
    }

}