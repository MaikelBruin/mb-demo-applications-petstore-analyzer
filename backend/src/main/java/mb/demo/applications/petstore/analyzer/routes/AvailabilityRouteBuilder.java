package mb.demo.applications.petstore.analyzer.routes;

import mb.demo.applications.petstore.analyzer.service.AvailabilityService;
import mb.demo.applications.petstore.analyzer.webapi.model.AvailabilityRatioResponse;
import org.apache.camel.CamelContext;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityRouteBuilder extends BaseRouteBuilder {

    private final AvailabilityService availabilityService;

    public AvailabilityRouteBuilder(final CamelContext context, final AvailabilityService availabilityService) {
        super(context);
        this.availabilityService = availabilityService;
    }

    @Override
    public void configure() {
        from(RouteBuilderConstants.DIRECT_ROUTE_GET_AVAILABILITY_RATIO)
                .routeId(RouteBuilderConstants.DIRECT_ROUTE_GET_AVAILABILITY_RATIO + "Id")
                .process(exchange -> {
                    AvailabilityRatioResponse response = availabilityService.getPetAvailabilityRatio();
                    exchange.getMessage().setBody(response);
                });


    }
}
