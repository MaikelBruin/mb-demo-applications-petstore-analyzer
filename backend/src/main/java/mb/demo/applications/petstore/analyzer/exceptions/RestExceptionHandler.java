package mb.demo.applications.petstore.analyzer.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps exceptions thrown by the service implementations to the HTTP status they define.
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ConsumerDownException.class)
    public ProblemDetail handleConsumerDown(final ConsumerDownException e) {
        log.warn("Petstore analyzer consumer is unavailable: {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler(SupplierDownException.class)
    public ProblemDetail handleSupplierDown(final SupplierDownException e) {
        log.warn("Petstore supplier is unavailable: {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }
}
