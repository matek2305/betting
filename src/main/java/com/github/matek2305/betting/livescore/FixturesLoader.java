package com.github.matek2305.betting.livescore;

import io.quarkus.arc.properties.IfBuildProperty;
import io.quarkus.runtime.Startup;
import io.vertx.core.eventbus.EventBus;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
@IfBuildProperty(name = "api-football.enabled", stringValue = "true")
class FixturesLoader {

    private final FootballApiClient client;
    private final EventBus eventBus;

    FixturesLoader(@RestClient FootballApiClient client, EventBus eventBus) {
        this.client = client;
        this.eventBus = eventBus;
    }

    @PostConstruct
    void load() {
        client.getIncomingFixtures(2, "2020", 5)
                .response()
                .forEach(this::publishEventAbout);
    }

    private void publishEventAbout(FootballApiFixtureListResponse.Entry entry) {
        eventBus.publish("incoming_fixtures", new IncomingFixtureLoaded(
                entry.fixture().id(),
                entry.teams().home().name(),
                entry.teams().away().name(),
                entry.fixture().date()));
    }
}
