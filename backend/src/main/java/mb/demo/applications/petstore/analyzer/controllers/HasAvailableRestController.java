package mb.demo.applications.petstore.analyzer.controllers;

import mb.demo.applications.petstore.analyzer.routes.RouteBuilderConstants;
import mb.demo.applications.petstore.analyzer.webapi.api.AvailableApi;
import mb.demo.applications.petstore.analyzer.webapi.api.RatiosApi;
import mb.demo.applications.petstore.analyzer.webapi.model.AvailabilityRatioResponse;
import mb.demo.applications.petstore.analyzer.webapi.model.HasAvailableResponse;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HasAvailableRestController extends BaseRestController implements AvailableApi {

    public HasAvailableRestController(final ProducerTemplate producerTemplate) {
        super(producerTemplate);
    }

    @Override
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/api/available/rats",
            produces = {"application/json"}
    )
    public ResponseEntity<HasAvailableResponse> getHasAvailableRats() {
        final HasAvailableResponse hasAvailableResponse = producerTemplate.requestBody(RouteBuilderConstants.DIRECT_ROUTE_GET_HAS_AVAILABLE_RATS, null, HasAvailableResponse.class);
        return ResponseEntity.ok(hasAvailableResponse);
    }
}
