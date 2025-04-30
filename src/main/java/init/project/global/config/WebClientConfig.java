package init.project.global.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000) // 커넥션 연결 시간 (3초)
                .responseTimeout(Duration.ofSeconds(3))              // 응답 대기 시간 (3초)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(3, TimeUnit.SECONDS))   // 읽기 타임아웃
                                .addHandlerLast(new WriteTimeoutHandler(3, TimeUnit.SECONDS)) // 쓰기 타임아웃
                );
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}