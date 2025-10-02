package mb.demo.applications.petstore.analyzer.utils;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;

@Slf4j
public class JerseyRequestFilter implements ClientRequestFilter {

    private final Object context;

    public JerseyRequestFilter(Object context) {
        this.context = context;
    }

    @Override
    public void filter(ClientRequestContext clientRequestContext) {
//        clientRequestContext.getHeaders().putSingle(HttpHeaders.ACCEPT_ENCODING, "*");
//        clientRequestContext.getHeaders().putSingle(HttpHeaders.ACCEPT_CHARSET, "utf-8");
        clientRequestContext.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, List.of("application/json", "charset=utf-8"));
        log.info("Request URI: {}", clientRequestContext.getUri());
        log.info("Request Method: {}", clientRequestContext.getMethod());
        clientRequestContext.getHeaders().forEach((clientRequestHeader, headers) -> {
            if (!clientRequestHeader.equalsIgnoreCase("Authorization")) {
                log.info("Request Header: '{}': '{}'", clientRequestHeader, headers);
            }
        });
    }
}
