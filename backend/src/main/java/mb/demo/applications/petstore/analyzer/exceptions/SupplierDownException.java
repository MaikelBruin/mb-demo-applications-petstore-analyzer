package mb.demo.applications.petstore.analyzer.exceptions;

import mb.demos.openapi.generated.api.client.petstore.client.ApiException;

/**
 * The petstore (our supplier of pet data) is unavailable: it responded with a 503. The counterpart of
 * {@link ConsumerDownException}, which is for the consumer of our events.
 */
public class SupplierDownException extends ApiException {

    private static final int SERVICE_UNAVAILABLE = 503;

    public SupplierDownException(String message, ApiException cause) {
        super(message, cause, SERVICE_UNAVAILABLE, cause.getResponseHeaders(), cause.getResponseBody());
    }

    /**
     * To be called first in every catch block around a petstore call: turns a 503 from the petstore into a
     * SupplierDownException, so it is not retried and not hidden behind a generic error. Does nothing for any other
     * exception.
     */
    public static void throwIfServiceUnavailable(Exception e) throws SupplierDownException {
        if (e instanceof ApiException apiException && apiException.getCode() == SERVICE_UNAVAILABLE) {
            throw new SupplierDownException("Petstore supplier is down", apiException);
        }
    }
}
