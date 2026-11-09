package mb.demo.applications.petstore.analyzer.exceptions;

import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.client.ApiException;

public class ConsumerDownException extends ApiException {

    public ConsumerDownException(String message) {
        super(503, message);
    }
}
