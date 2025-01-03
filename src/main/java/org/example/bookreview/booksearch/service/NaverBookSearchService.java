package org.example.bookreview.booksearch.service;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookreview.common.error.ErrorType;
import org.example.bookreview.common.exception.BusinessException;
import org.example.bookreview.config.NaverApiConfig;
import org.example.bookreview.booksearch.dto.NaverBookSearchResponse;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverBookSearchService {

    private final RestTemplate restTemplate;
    private final NaverApiConfig naverApiConfig;

    public NaverBookSearchResponse searchBooks(String query, int display, int start, String sort) {
        URI uri = UriComponentsBuilder
            .fromUriString(naverApiConfig.getUrl())
            .queryParam("query", query)
            .queryParam("display", display)
            .queryParam("start", start)
            .queryParam("sort", sort)
            .encode()
            .build()
            .toUri();

        RequestEntity<Void> req = RequestEntity
            .get(uri)
            .header("X-Naver-Client-Id", naverApiConfig.getClientId())
            .header("X-Naver-Client-Secret", naverApiConfig.getClientSecret())
            .build();

        try {
            ResponseEntity<String> rawResponse = restTemplate.exchange(req, String.class);
            log.debug("Raw Response: {}", rawResponse.getBody());

            ResponseEntity<NaverBookSearchResponse> response = restTemplate.exchange(
                req, NaverBookSearchResponse.class);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("API 호출 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorType.EXTERNAL_API_ERROR);
        }
    }
}