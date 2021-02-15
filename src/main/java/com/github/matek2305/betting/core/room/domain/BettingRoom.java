package com.github.matek2305.betting.core.room.domain;

import com.github.matek2305.betting.core.match.domain.MatchEvent.IncomingMatchCreated;
import com.github.matek2305.betting.core.match.domain.MatchId;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class BettingRoom {

    private final BettingRoomPolicy bettingRoomPolicy;

    IncomingMatchCreated add(AddIncomingMatchCommand command) {
        if (!bettingRoomPolicy.check(command)) {
            throw new IllegalArgumentException(bettingRoomPolicy.getRuleDescription());
        }

        return new IncomingMatchCreated(
                MatchId.of(UUID.randomUUID()),
                command.startDateTime(),
                command.homeTeam(),
                command.awayTeam());
    }
}
