package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.match.domain.MatchId;
import com.github.matek2305.betting.match.domain.MatchScore;
import lombok.Value;

public interface PlayerEvent {

    PlayerId playerId();

    @Value
    class PlayerBetMade implements PlayerEvent {
        PlayerId playerId;
        MatchId matchId;
        MatchScore bet;
    }

    @Value
    class PlayerBetRejected implements PlayerEvent {
        PlayerId playerId;
        MatchId matchId;
    }
}
