package init.project.domain.restTemplate.service;

import init.project.domain.restTemplate.model.response.TestApiRS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestTemplateService {

    private final RestTemplate restTemplate = new RestTemplate();


    /**
     * [1] Request Param
     * [2] API Request
     * [3] Response 처리
     */
    public void testGetRestTemplate() {
        // [1]
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + "token 정보들");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromHttpUrl("http://localhost:3003/api/test")
                .queryParam("param1", "변수1")
                .queryParam("param2", "변수2")
                .queryParam("param3", "변수3")
                .toUriString();
        // [2]
        ResponseEntity<TestApiRS> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, TestApiRS.class);
        } catch (HttpClientErrorException e) {
            log.warn("HttpClientErrorException: {}", e.getResponseBodyAsString());
        }
        // [3]
        TestApiRS response = responseEntity != null ? responseEntity.getBody() : null;
        if (response != null) {
            throw new IllegalArgumentException("wow");
        }
    }

    /**
     * [1] Request Param
     * [2] API Request
     * [3] Response 처리
     */
    public void testPostRestTemplate() {
        // [1] Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + "token 정보들");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String,Object>> requestParam = new HttpEntity<>(Map.of(
                "param1", "변수1",
                "param2", "변수2",
                "param3", "변수3"
        ), headers);

        // [2] API Request
        String url = "http://localhost:3003/api/test";
        ResponseEntity<TestApiRS> responseEntity;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestParam, TestApiRS.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) { // 4XX, 5XX 에러를 받았을때
            log.error("Client/Server error: {}", e.getResponseBodyAsString());
            return;
        } catch (ResourceAccessException e) { // 서버와의 연결 실패 / 타임아웃 / DNS 문제
            log.error("Connection error: {}", e.getMessage());
            return;
        }

        // [3] Response 처리
        TestApiRS response = responseEntity.getBody();
        if (response == null || !"SUCCESS".equals(response.getResultCode())) {
            throw new IllegalStateException("API 응답 실패 또는 데이터 없음");
        }

        // 여기에서 이제 데이터 처리 쭉 하면 됨 ...

    }

}