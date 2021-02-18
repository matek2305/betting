package com.github.matek2305.betting.core.player.infrastructure;

import com.github.matek2305.betting.core.match.domain.IncomingMatches;
import com.github.matek2305.betting.core.player.domain.Betting;
import com.github.matek2305.betting.core.player.domain.Players;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
class PlayerContextConfiguration {

    @Produces
    public Betting betting(IncomingMatches incomingMatches, Players players) {
        return new Betting(incomingMatches, players);
    }
}
