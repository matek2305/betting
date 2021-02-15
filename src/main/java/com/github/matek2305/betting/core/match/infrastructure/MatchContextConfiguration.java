package com.github.matek2305.betting.core.match.infrastructure;

import com.github.matek2305.betting.commons.EventsPublisher;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatch;
import com.github.matek2305.betting.core.room.domain.BettingRoomRepository;
import com.github.matek2305.betting.core.match.domain.MatchRepository;
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
    public AddIncomingMatch createIncomingMatch(
            BettingRoomRepository bettingRoomRepository, MatchRepository repository) {
        return new AddIncomingMatch(bettingRoomRepository, repository);
    }
}
