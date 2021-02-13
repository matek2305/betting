package com.github.matek2305.betting.core.match.infrastructure;

import com.github.matek2305.betting.commons.EventsPublisher;
import com.github.matek2305.betting.core.match.domain.CreateIncomingMatch;
import com.github.matek2305.betting.core.match.domain.CreateIncomingMatchPolicy;
import com.github.matek2305.betting.core.match.domain.MatchBettingPolicy;
import com.github.matek2305.betting.core.match.domain.MatchRepository;
import com.github.matek2305.betting.date.DateProvider;
import io.vertx.core.eventbus.EventBus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Slf4j
@Dependent
@RequiredArgsConstructor
class MatchContextConfiguration {

    private final EventBus eventBus;

    @Produces
    public EventsPublisher eventsPublisher() {
        return EventsPublisher.explicitPublish(event -> {
            log.debug("Publishing match event: {}", event);
            eventBus.publish("matches", event);
        });
    }

    @Produces
    public CreateIncomingMatch createIncomingMatch(
            CreateIncomingMatchPolicy createIncomingMatchPolicy, MatchRepository repository) {
        return new CreateIncomingMatch(createIncomingMatchPolicy, repository);
    }

    @Produces
    public MatchBettingPolicy matchBettingPolicies(DateProvider dateProvider) {
        return MatchBettingPolicy.bettingAllowedBeforeMatchStartOnly(dateProvider);
    }

    @Produces
    public CreateIncomingMatchPolicy createIncomingMatchPolicy(DateProvider dateProvider) {
        return CreateIncomingMatchPolicy.atLeastOneHourBeforeMatchStart(dateProvider);
    }
}
