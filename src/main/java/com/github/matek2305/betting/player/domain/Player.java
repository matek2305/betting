package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.match.domain.IncomingMatch;
import com.github.matek2305.betting.match.domain.MatchId;
import com.github.matek2305.betting.match.domain.MatchScore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Player {

    private final PlayerId playerId;
    private final PlayerBets bets;

    public Player(PlayerId playerId) {
        this(playerId, new PlayerBets());
    }

    public PlayerEvent placeBet(IncomingMatch match, MatchScore bet) {
        if (match.isBettingAllowed()) {
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
