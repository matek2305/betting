package com.github.matek2305.betting.core.room.app;

import com.github.matek2305.betting.core.match.domain.Team;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatch;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchCommand;
import com.github.matek2305.betting.livescore.IncomingFixtureLoaded;
import io.quarkus.vertx.ConsumeEvent;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
class IncomingFixturesHandler {

    private final AddIncomingMatch addIncomingMatch;

    @ConsumeEvent(value = "incoming_fixtures", blocking = true)
    public void handle(IncomingFixtureLoaded fixtureLoaded) {
        addIncomingMatch.add(toCreateCommand(fixtureLoaded));
    }

    private AddIncomingMatchCommand toCreateCommand(IncomingFixtureLoaded fixtureLoaded) {
        return new AddIncomingMatchCommand(
                fixtureLoaded.startDateTime(),
                Team.of(fixtureLoaded.homeTeamName()),
                Team.of(fixtureLoaded.awayTeamName()));
    }
}
