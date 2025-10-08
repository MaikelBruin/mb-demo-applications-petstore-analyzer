package mb.demo.applications.petstore.analyzer.routes;

import mb.demo.applications.petstore.analyzer.service.TotalsService;
import mb.demo.applications.petstore.analyzer.webapi.model.TotalResponse;
import org.apache.camel.CamelContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TotalsRouteBuilder extends BaseRouteBuilder {

    private final TotalsService totalsService;

    public TotalsRouteBuilder(final CamelContext context, final TotalsService totalsService) {
        super(context);
        this.totalsService = totalsService;
    }

    @Override
    public void configure() {
        from(RouteBuilderConstants.DIRECT_ROUTE_GET_TOTAL_DOGS)
                .routeId(RouteBuilderConstants.DIRECT_ROUTE_GET_TOTAL_DOGS + "Id")
                .process(exchange -> {
                    TotalResponse response = totalsService.getTotalNumberOfDogs();
                    exchange.getMessage().setBody(response);
                });
        from(RouteBuilderConstants.DIRECT_ROUTE_GET_TOTAL_CATS)
                .routeId(RouteBuilderConstants.DIRECT_ROUTE_GET_TOTAL_CATS + "Id")
                .process(exchange -> {
                    TotalResponse response = totalsService.getTotalNumberOfCats();
                    exchange.getMessage().setBody(response);
                });
        from(RouteBuilderConstants.DIRECT_ROUTE_GET_TOTAL_AVAILABLE)
                .routeId(RouteBuilderConstants.DIRECT_ROUTE_GET_TOTAL_AVAILABLE + "Id")
                .process(exchange -> {
                    TotalResponse response = totalsService.getTotalNumberOfAvailablePets();
                    exchange.getMessage().setBody(response);
                });
        from(RouteBuilderConstants.DIRECT_ROUTE_FIND_TOTAL)
                .routeId(RouteBuilderConstants.DIRECT_ROUTE_FIND_TOTAL + "Id")
                .process(exchange -> {
                    final Map<String, Object> params = exchange.getIn().getBody(Map.class);
                    final String tag = (String) params.get("tag");

                    if (tag == null) {
                        throw new RuntimeException("tag is null");
                    }
                    TotalResponse response = totalsService.findTotalNumberOfPets(tag);
                    exchange.getMessage().setBody(response);
                });
    }
}
