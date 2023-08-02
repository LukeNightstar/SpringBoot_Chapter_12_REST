package com.springboot.rest.service;

import com.springboot.rest.data.dto.MemberDto;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.apache.hc.client5.http.impl.classic.HttpClients.custom;

@Service
public class RestTemplateServiceTest {

    // GET 형식 RestTemplate
    // This method passes values without using PathVariable or parameters.
    public String getName() {
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8080")
                .path("/api/v1/crud-api")
                .encode() // Prevention of broken display language
                .build()  // 빌더생성 종료
                .toUri(); // URI 타입으로(URI 객체가 아니면 toUriString()로 대체)

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

        return responseEntity.getBody();
    }

    public String getNameWithPathVariable() {
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8080")
                .path("/api/v1/crud-api/{name}")
                .encode()
                .build()
                .expand("Flature") // 복수의 값을 넣어야 할 경우 , 를 추가하여 구분
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

        return responseEntity.getBody();
    }

    // This method passes values as parameters.
    public String getNameWithParameter() {
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8080")
                .path("/api/v1/crud-api/param")
                .queryParam("name", "Flature")
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

        return responseEntity.getBody();
    }

    // Post 형식 RestTemplate
    public ResponseEntity<MemberDto> postWithParamAndBody() {
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8080")
                .path("/api/v1/crud-api")
                .queryParam("name", "Flature")
                .queryParam("email", "flature@wikibooks.co.kr")
                .queryParam("organization", "Wikibooks")
                .encode()
                .build()
                .toUri();

        MemberDto memberDto = new MemberDto();
        memberDto.setName("flature!!");
        memberDto.setEmail("flature@gmail.com");
        memberDto.setOrganization("Around Hub Studio");

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(uri, memberDto, MemberDto.class);
    }

    public ResponseEntity<MemberDto> postWithHeader() {
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8080")
                .path("/api/v1/crud-api/add-header")
                .encode()
                .build()
                .toUri();

        MemberDto memberDto = new MemberDto();
        memberDto.setName("flature!!");
        memberDto.setEmail("flature@gmail.com");
        memberDto.setOrganization("Around Hub Studio");

        RequestEntity<MemberDto> requestEntity = RequestEntity
                .post(uri)
                .header("my-header", "Wikibooks API")
                .body(memberDto);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(requestEntity, MemberDto.class);
    }

    // HttpClient API
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();

        ConnectionConfig connConfig = ConnectionConfig.custom()
                .setSocketTimeout()
                .build();


        CloseableHttpClient httpClient = custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(
                        SSLContexts.createSystemDefault(),
                        new String[] { "TLSv1.2" },
                        null,
                        SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
                .setConnectionTimeToLive(1, TimeUnit.MINUTES)
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(60000)
                        .build())
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(60000)
                        .setSocketTimeout(60000)
                        .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                        .build())
                .build();

        factory.setHttpClient(httpClient);
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(5000);

        manager.setSocketTimeout(5000);



        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }

}
