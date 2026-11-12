package mb.demo.applications.petstore.analyzer.controllers;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class BaseRestController {

    @Autowired
    protected ProducerTemplate producerTemplate;

    public BaseRestController(final ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    /**
     * Sends the body to a camel route and returns the result, like {@link ProducerTemplate#requestBody}, but
     * rethrows the exception exactly as the service implementation threw it. {@code requestBody} would wrap it in a
     * {@code CamelExecutionException}, which hides the original type from callers and exception handlers.
     */
    @SneakyThrows
    protected <T> T requestBody(final String routeUri, final Object body, final Class<T> responseType) {
        final Exchange exchange = producerTemplate.request(routeUri, e -> e.getIn().setBody(body));
        if (exchange.getException() != null) {
            throw exchange.getException();
        }
        return exchange.getMessage().getBody(responseType);
    }
}
