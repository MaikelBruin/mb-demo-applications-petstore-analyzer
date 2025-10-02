package mb.demo.applications.petstore.analyzer.service;

import mb.demo.applications.petstore.analyzer.webapi.model.AvailabilityRatioResponse;

public interface AvailabilityService {
    AvailabilityRatioResponse getPetAvailabilityRatio();
}
