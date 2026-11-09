package mb.demo.applications.petstore.analyzer.config.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import mb.demo.applications.petstore.analyzer.utils.JerseyRequestFilter;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.api.EventsApi;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.client.ApiClient;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PetstoreAnalyzerConsumerClientConfiguration {

    private static final Logger log = LoggerFactory.getLogger(PetstoreAnalyzerConsumerClientConfiguration.class);

    @Value("${services.petstore-analyzer-consumer.base-url}")
    private String baseUrl;

    @Value("${services.petstore-analyzer-consumer.connection-timeout}")
    private Integer connectionTimeout;

    @Bean
    public EventsApi eventsApi(ObjectMapper objectMapper) {
        return new EventsApi(createApiClient(objectMapper));
    }

    private ApiClient createApiClient(ObjectMapper objectMapper) {
        final ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(baseUrl);
        apiClient.setConnectTimeout(connectionTimeout);
        apiClient.setHttpClient(createJerseyClient(objectMapper));
        return apiClient;
    }

    private JerseyClient createJerseyClient(ObjectMapper objectMapper) {
        final JerseyClient client = JerseyClientBuilder.createClient();
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider();
        jacksonJsonProvider.setMapper(objectMapper);
        client.register(jacksonJsonProvider, 1);
        client.property("jersey.config.client.connectTimeout", connectionTimeout);
        client.register(new JerseyRequestFilter(PetstoreAnalyzerConsumerClientConfiguration.class));
        return client;
    }

}
