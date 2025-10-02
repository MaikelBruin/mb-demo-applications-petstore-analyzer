package mb.demo.applications.petstore.analyzer.controllers;

import mb.demo.applications.petstore.analyzer.routes.RouteBuilderConstants;
import mb.demo.applications.petstore.analyzer.webapi.api.AvailabilityApi;
import mb.demo.applications.petstore.analyzer.webapi.model.AvailabilityRatioResponse;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AvailabilityRatioRestController extends BaseRestController implements AvailabilityApi {

    public AvailabilityRatioRestController(final ProducerTemplate producerTemplate) {
        super(producerTemplate);
    }

    @Override
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/api/availability/ratio",
            produces = {"application/json"}
    )
    public ResponseEntity<AvailabilityRatioResponse> getPetAvailabilityRatio() {
        final AvailabilityRatioResponse availabilityRatioResponse = producerTemplate.requestBody(RouteBuilderConstants.DIRECT_ROUTE_GET_AVAILABILITY_RATIO, null, AvailabilityRatioResponse.class);
        return ResponseEntity.ok(availabilityRatioResponse);
    }
}
