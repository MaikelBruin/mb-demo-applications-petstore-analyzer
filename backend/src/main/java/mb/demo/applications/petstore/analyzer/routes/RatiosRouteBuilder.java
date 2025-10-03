package mb.demo.applications.petstore.analyzer.routes;

import mb.demo.applications.petstore.analyzer.service.RatiosService;
import mb.demo.applications.petstore.analyzer.webapi.model.AvailabilityRatioResponse;
import org.apache.camel.CamelContext;
import org.springframework.stereotype.Component;

@Component
public class RatiosRouteBuilder extends BaseRouteBuilder {

    private final RatiosService ratiosService;

    public RatiosRouteBuilder(final CamelContext context, final RatiosService ratiosService) {
        super(context);
        this.ratiosService = ratiosService;
    }

    @Override
    public void configure() {
        from(RouteBuilderConstants.DIRECT_ROUTE_GET_AVAILABILITY_RATIO)
                .routeId(RouteBuilderConstants.DIRECT_ROUTE_GET_AVAILABILITY_RATIO + "Id")
                .process(exchange -> {
                    AvailabilityRatioResponse response = ratiosService.getPetAvailabilityRatio();
                    exchange.getMessage().setBody(response);
                });
    }
}
