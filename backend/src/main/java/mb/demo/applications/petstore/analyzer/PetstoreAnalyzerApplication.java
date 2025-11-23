package mb.demo.applications.petstore.analyzer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info=@Info(title="Petstore Analyzer API", description = "API that analyzes data from the OpenAPI Petstore"))
@SpringBootApplication
public class PetstoreAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetstoreAnalyzerApplication.class, args);
	}

}
