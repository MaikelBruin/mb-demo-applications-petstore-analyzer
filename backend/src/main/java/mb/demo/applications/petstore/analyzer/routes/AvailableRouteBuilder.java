package mb.demo.applications.petstore.analyzer.routes;

import mb.demo.applications.petstore.analyzer.service.HasAvailableService;
import mb.demo.applications.petstore.analyzer.webapi.model.AvailabilityRatioResponse;
import mb.demo.applications.petstore.analyzer.webapi.model.HasAvailableResponse;
import org.apache.camel.CamelContext;
import org.springframework.stereotype.Component;

@Component
public class AvailableRouteBuilder extends BaseRouteBuilder {

    private final HasAvailableService hasAvailableService;

    public AvailableRouteBuilder(final CamelContext context, final HasAvailableService hasAvailableService) {
        super(context);
        this.hasAvailableService = hasAvailableService;
    }

    @Override
    public void configure() {
        from(RouteBuilderConstants.DIRECT_ROUTE_GET_HAS_AVAILABLE_RATS)
                .routeId(RouteBuilderConstants.DIRECT_ROUTE_GET_HAS_AVAILABLE_RATS + "Id")
                .process(exchange -> {
                    HasAvailableResponse response = hasAvailableService.getHasAvailableRats();
                    exchange.getMessage().setBody(response);
                });
    }
}
