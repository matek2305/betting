package com.github.matek2305.betting.core.player.domain;

import com.github.matek2305.betting.commons.CommandResult;
import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.player.domain.PlayerEvent.PlayerBetMade;
import com.github.matek2305.betting.core.player.domain.PlayerEvent.PlayerBetRejected;
import com.github.matek2305.betting.core.room.domain.IncomingMatches;
import io.vavr.API;
import lombok.RequiredArgsConstructor;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;

@RequiredArgsConstructor
public class Betting {

    private final IncomingMatches incomingMatches;
    private final Players players;

    public CommandResult makeBet(MakeBetCommand command) {
        var match = get(command.matchId());
        var player = get(command.playerId());

        return API.Match(player.placeBet(match, command.bet())).of(
                Case($Right($()), this::publish),
                Case($Left($()), this::publish)
        );
    }

    private IncomingMatch get(MatchId matchId) {
        return incomingMatches.getIncomingMatchBy(matchId);
    }

    private Player get(PlayerId playerId) {
        return players.findBy(playerId)
                .getOrElse(() -> players.createWithId(playerId));
    }

    private CommandResult publish(PlayerBetMade betMade) {
        players.publish(betMade);
        return CommandResult.allowed();
    }

    private CommandResult publish(PlayerBetRejected betRejected) {
        players.publish(betRejected);
        return CommandResult.rejected(betRejected.rejectionReason());
    }
}
