package mb.demo.applications.petstore.analyzer.controllers;

import mb.demo.applications.petstore.analyzer.routes.RouteBuilderConstants;
import mb.demo.applications.petstore.analyzer.webapi.api.TotalsApi;
import mb.demo.applications.petstore.analyzer.webapi.model.TotalResponse;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TotalsRestController extends BaseRestController implements TotalsApi {

    public TotalsRestController(final ProducerTemplate producerTemplate) {
        super(producerTemplate);
    }

    @Override
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/api/totals/cats",
            produces = {"application/json"}
    )
    public ResponseEntity<TotalResponse> getTotalNumberOfCats() {
        final TotalResponse totalResponse = producerTemplate.requestBody(RouteBuilderConstants.DIRECT_ROUTE_GET_TOTAL_CATS, null, TotalResponse.class);
        return ResponseEntity.ok(totalResponse);
    }

    @Override
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/api/totals/dogs",
            produces = {"application/json"}
    )
    public ResponseEntity<TotalResponse> getTotalNumberOfDogs() {
        final TotalResponse totalResponse = producerTemplate.requestBody(RouteBuilderConstants.DIRECT_ROUTE_GET_TOTAL_DOGS, null, TotalResponse.class);
        return ResponseEntity.ok(totalResponse);
    }
}
