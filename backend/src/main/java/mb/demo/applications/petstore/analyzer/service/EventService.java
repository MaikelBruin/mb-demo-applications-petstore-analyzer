package mb.demo.applications.petstore.analyzer.service;

import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.client.ApiException;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.model.EventAcceptedResponse;

public interface EventService {
    EventAcceptedResponse processEvent(String eventType, Object payload) throws ApiException;
}
