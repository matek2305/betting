package com.github.matek2305.betting.core.match.domain;

import com.github.matek2305.betting.commons.CommandResult;
import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchFinishRejected;
import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchFinished;
import com.github.matek2305.betting.core.room.domain.IncomingMatches;
import io.vavr.API;
import lombok.RequiredArgsConstructor;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;

@RequiredArgsConstructor
public class FinishMatch {

    private final IncomingMatches incomingMatches;
    private final MatchRepository matchRepository;

    public CommandResult finishMatch(FinishMatchCommand command) {
        var match = get(command.matchId());
        return API.Match(match.finish(command)).of(
                Case($Right($()), this::publish),
                Case($Left($()), this::publish)
        );
    }

    private IncomingMatch get(MatchId matchId) {
        return incomingMatches.getIncomingMatchBy(matchId);
    }

    private CommandResult publish(MatchFinished matchFinished) {
        matchRepository.publish(matchFinished);
        return CommandResult.allowed();
    }

    private CommandResult publish(MatchFinishRejected finishRejected) {
        matchRepository.publish(finishRejected);
        return CommandResult.rejected(finishRejected.rejectionReason());
    }
}
