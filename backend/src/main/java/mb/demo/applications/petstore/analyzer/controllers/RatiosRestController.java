package mb.demo.applications.petstore.analyzer.controllers;

import mb.demo.applications.petstore.analyzer.routes.RouteBuilderConstants;
import mb.demo.applications.petstore.analyzer.webapi.api.RatiosApi;
import mb.demo.applications.petstore.analyzer.webapi.model.AvailabilityRatioResponse;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RatiosRestController extends BaseRestController implements RatiosApi {

    public RatiosRestController(final ProducerTemplate producerTemplate) {
        super(producerTemplate);
    }

    @Override
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/api/ratios/availability",
            produces = {"application/json"}
    )
    public ResponseEntity<AvailabilityRatioResponse> getPetAvailabilityRatio() {
        final AvailabilityRatioResponse availabilityRatioResponse = producerTemplate.requestBody(RouteBuilderConstants.DIRECT_ROUTE_GET_AVAILABILITY_RATIO, null, AvailabilityRatioResponse.class);
        return ResponseEntity.ok(availabilityRatioResponse);
    }
}
