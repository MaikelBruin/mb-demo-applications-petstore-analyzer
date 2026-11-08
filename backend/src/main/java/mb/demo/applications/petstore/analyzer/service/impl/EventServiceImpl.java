package mb.demo.applications.petstore.analyzer.service.impl;

import mb.demo.applications.petstore.analyzer.exceptions.ConsumerDownException;
import mb.demo.applications.petstore.analyzer.service.EventService;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.api.EventsApi;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.client.ApiException;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.model.EventAcceptedResponse;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.model.EventRequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class EventServiceImpl implements EventService {

    @Value("${toggles.send-to-consumer}")
    private boolean sendToConsumer;

    private EventsApi eventsApi;

    public EventServiceImpl(EventsApi eventsApi) {
        this.eventsApi = eventsApi;
    }

    @Override
    public EventAcceptedResponse processEvent(@Nonnull String eventType, @Nonnull Object payload) throws ConsumerDownException {
        if (!sendToConsumer) {
            return null;
        }

        EventRequestBody requestBody = new EventRequestBody();
        requestBody.setEventType(eventType);
        requestBody.setPayload(payload);
        EventAcceptedResponse acceptedResponse = null;
        try {
            acceptedResponse = eventsApi.processEvent(requestBody);
        } catch (ApiException e) {
            //teapot
            if (e.getCode() == 418) {
                return new EventAcceptedResponse().eventId(UUID.randomUUID()).receivedAt(Date.from(Instant.now()));
            }
            if (e.getCode() == 503) {
                throw new ConsumerDownException("Consumer is down");
            }
            throw new RuntimeException(e);
        }
        return acceptedResponse;
    }
}
