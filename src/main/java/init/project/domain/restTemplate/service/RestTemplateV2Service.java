package init.project.domain.restTemplate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Slf4j
@Component
public class RestTemplateV2Service {

    private final RestTemplate restTemplate;

    public RestTemplateV2Service(@Qualifier("customRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public <T> T requestGetAPI(String url, Map<String, Object> queryParams, Class<T> responseType) {
        // [1] Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // [2] URI 구성 (쿼리 스트링 자동 인코딩)
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }
        URI uri = builder.build().encode().toUri();

        // [3] HttpEntity 구성 (GET은 보통 body가 없으므로 header만 포함)
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // [4] API Request
        ResponseEntity<T> responseEntity;
        try {
            responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, responseType);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Client/Server error: {}", e.getResponseBodyAsString());
            return null;
        } catch (ResourceAccessException e) {
            log.error("Connection error: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return null;
        }

        // [5] Response 처리
        if (responseEntity.getBody() == null) {
            log.warn("Empty response from {}", uri);
            return null;
        }

        return responseEntity.getBody();
    }

    /** POST 요청 */
    public <T, K> T requestPostApi(String url, K requestParam, Class<T> responseType) {
        return executeRequest(HttpMethod.POST, url, requestParam, responseType);
    }

    /** PUT 요청 */
    public <T, K> T requestPutApi(String url, K requestParam, Class<T> responseType) {
        return executeRequest(HttpMethod.PUT, url, requestParam, responseType);
    }

    /** PATCH 요청 */
    public <T, K> T requestPatchApi(String url, K requestParam, Class<T> responseType) {
        return executeRequest(HttpMethod.PATCH, url, requestParam, responseType);
    }

    /** DELETE 요청 */
    public <T, K> T requestDeleteApi(String url, K requestParam, Class<T> responseType) {
        return executeRequest(HttpMethod.DELETE, url, requestParam, responseType);
    }

    private <T, K> T executeRequest(HttpMethod method, String url, K requestParam, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<K> entity = new HttpEntity<>(requestParam, headers);

        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, method, entity, responseType);
            if (responseEntity.getBody() == null) {
                log.warn("[{}] Empty response from {}", method, url);
                throw new IllegalArgumentException("[IMC-API] Empty response from " + url);
            }

            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("[IMC-API] Client/Server error: {}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (ResourceAccessException e) {
            log.error("[IMC-API] Connection error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw e;
        }
    }

}