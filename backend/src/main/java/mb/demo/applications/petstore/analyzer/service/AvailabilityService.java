package mb.demo.applications.petstore.analyzer.service;

import mb.demo.applications.petstore.analyzer.webapi.model.AvailabilityRatioResponse;
import mb.demos.openapi.generated.api.client.petstore.client.ApiException;

public interface AvailabilityService {
    AvailabilityRatioResponse getPetAvailabilityRatio() throws ApiException;
}
