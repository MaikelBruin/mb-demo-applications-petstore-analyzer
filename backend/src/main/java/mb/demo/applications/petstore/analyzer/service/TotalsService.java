package mb.demo.applications.petstore.analyzer.service;

import mb.demo.applications.petstore.analyzer.webapi.model.TotalResponse;
import mb.demos.openapi.generated.api.client.petstore.client.ApiException;

public interface TotalsService {
    TotalResponse getTotalNumberOfDogs() throws ApiException, InterruptedException;
    TotalResponse getTotalNumberOfCats() throws ApiException, InterruptedException;
    TotalResponse getTotalNumberOfAvailablePets() throws ApiException, InterruptedException;
}
