package mb.demo.applications.petstore.analyzer.service.impl;

import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.service.AvailabilityService;
import mb.demo.applications.petstore.analyzer.webapi.model.AvailabilityRatioResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class AvailabilityRatioServiceImpl implements AvailabilityService {
    @Override
    public AvailabilityRatioResponse getPetAvailabilityRatio() {
        //TODO: replace me with actual api calls

        AvailabilityRatioResponse response = new AvailabilityRatioResponse();
        int availablePets = 5;
        response.setAvailablePets(availablePets);
        int soldPets = 10;
        response.setSoldPets(soldPets);
        response.setRatio(BigDecimal.valueOf((float) availablePets / soldPets));
        return response;
    }
}
