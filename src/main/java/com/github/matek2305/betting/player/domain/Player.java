package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.match.domain.IncomingMatch;
import com.github.matek2305.betting.match.domain.MatchId;
import com.github.matek2305.betting.match.domain.MatchScore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Player {

    @Getter
    private final PlayerId playerId;
    private final BettingPolicy bettingPolicy;

    public PlayerEvent placeBet(IncomingMatch match, MatchScore bet) {
        if (bettingPolicy.playerCanBet(match)) {
            return betSuccessful(match.matchId(), bet);
        } else {
            return betRejected(match.matchId());
        }
    }

    private PlayerEvent betSuccessful(MatchId matchId, MatchScore bet) {
        return new PlayerEvent.PlayerBetMade(playerId, matchId, bet);
    }

    private PlayerEvent betRejected(MatchId matchId) {
        return new PlayerEvent.PlayerBetRejected(playerId, matchId);
    }
}
