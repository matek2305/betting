package com.github.matek2305.betting.core.player.domain;

import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.room.domain.IncomingMatches;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Betting {

    private final IncomingMatches incomingMatches;
    private final Players players;

    public void makeBet(MakeBetCommand command) {
        var match = get(command.matchId());
        var player = get(command.playerId());
        players.publish(player.placeBet(match, command.bet()));
    }

    private IncomingMatch get(MatchId matchId) {
        return incomingMatches.getIncomingMatchBy(matchId);
    }

    private Player get(PlayerId playerId) {
        return players.findBy(playerId)
                .getOrElse(() -> players.createWithId(playerId));
    }
}
