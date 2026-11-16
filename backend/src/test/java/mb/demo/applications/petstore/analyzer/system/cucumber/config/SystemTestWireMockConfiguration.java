package mb.demo.applications.petstore.analyzer.system.cucumber.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistrar;

/**
 * Two WireMock servers that stand in for the downstream services: one for the public petstore and one for the
 * petstore analyzer consumer. Unlike mocking PetApi and EventsApi with Mockito, the real generated clients are used:
 * requests are really sent over HTTP, and responses really go through Jersey and Jackson. Only the other side of the
 * connection is fake.
 * <p>
 * The servers start on a random free port. The {@link DynamicPropertyRegistrar} points the base urls in
 * application.yaml at them before the client beans are created.
 */
@Configuration
@Profile("system")
public class SystemTestWireMockConfiguration {

    public static final String PETSTORE_WIREMOCK = "petstoreWireMock";
    public static final String CONSUMER_WIREMOCK = "consumerWireMock";

    @Bean(name = PETSTORE_WIREMOCK, initMethod = "start", destroyMethod = "stop")
    public WireMockServer petstoreWireMock() {
        return new WireMockServer(WireMockConfiguration.options().dynamicPort());
    }

    @Bean(name = CONSUMER_WIREMOCK, initMethod = "start", destroyMethod = "stop")
    public WireMockServer consumerWireMock() {
        return new WireMockServer(WireMockConfiguration.options().dynamicPort());
    }

    @Bean
    public DynamicPropertyRegistrar wireMockBaseUrls(@Qualifier(PETSTORE_WIREMOCK) WireMockServer petstoreWireMock,
                                                     @Qualifier(CONSUMER_WIREMOCK) WireMockServer consumerWireMock) {
        return registry -> {
            registry.add("services.petstore.base-url", petstoreWireMock::baseUrl);
            registry.add("services.petstore-analyzer-consumer.base-url", consumerWireMock::baseUrl);
        };
    }
}
