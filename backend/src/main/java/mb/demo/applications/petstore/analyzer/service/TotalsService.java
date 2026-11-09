package mb.demo.applications.petstore.analyzer.service;

import mb.demo.applications.petstore.analyzer.webapi.model.TotalResponse;
import mb.demos.openapi.generated.api.client.petstore.client.ApiException;

public interface TotalsService {
    TotalResponse getTotalNumberOfDogs() throws ApiException, InterruptedException, mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.client.ApiException;

    TotalResponse getTotalNumberOfCats() throws ApiException, InterruptedException, mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.client.ApiException;

    TotalResponse getTotalNumberOfAvailablePets() throws ApiException, InterruptedException, mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.client.ApiException;

    TotalResponse findTotalNumberOfPets(String tag) throws ApiException, InterruptedException, mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.client.ApiException;
}
