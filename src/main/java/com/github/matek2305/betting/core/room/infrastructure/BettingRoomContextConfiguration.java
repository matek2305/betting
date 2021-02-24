package com.github.matek2305.betting.core.room.infrastructure;

import com.github.matek2305.betting.core.room.domain.AddIncomingMatch;
import com.github.matek2305.betting.core.room.domain.BettingRoomRepository;
import com.github.matek2305.betting.core.room.domain.IncomingMatches;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
class BettingRoomContextConfiguration {

    @Produces
    public AddIncomingMatch addIncomingMatch(
            BettingRoomRepository bettingRoomRepository, IncomingMatches incomingMatches) {
        return new AddIncomingMatch(bettingRoomRepository, incomingMatches);
    }
}
