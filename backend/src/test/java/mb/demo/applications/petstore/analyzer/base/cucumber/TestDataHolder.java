package mb.demo.applications.petstore.analyzer.base.cucumber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mb.demo.applications.petstore.analyzer.webapi.model.HasAvailableResponse;

@NoArgsConstructor
@Getter
@Setter
public class TestDataHolder {

    private HasAvailableResponse hasAvailableResponse;
    private Exception exception;
}
