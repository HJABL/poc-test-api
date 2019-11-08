package com.europcar;

import com.europcar.error.BookApiUnavailableException;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static java.util.Collections.singletonList;

@Component
public class BookClient {
    private final String uriGet = "http://localhost:8080/books";

    Book[] getBooks() {
        try {
            HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            httpRequestFactory.setReadTimeout(500);
            RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<Book[]> result = restTemplate.exchange(uriGet, HttpMethod.GET, entity, Book[].class);

            return result.getBody();

        } catch (RestClientException e) {
            throw new BookApiUnavailableException();
        }
    }
}
