package mb.demo.applications.petstore.analyzer.service;

import mb.demo.applications.petstore.analyzer.webapi.model.HasAvailableResponse;
import mb.demos.openapi.generated.api.client.petstore.client.ApiException;

public interface HasAvailableService {
    HasAvailableResponse getHasAvailableRats() throws ApiException, InterruptedException;
}
