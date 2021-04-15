package com.github.matek2305.betting.core.room.domain;

import com.github.matek2305.betting.commons.DateProvider;
import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchInformation;
import com.github.matek2305.betting.core.match.domain.external.ExternalMatch;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent.AddIncomingMatchRejected;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent.IncomingMatchAdded;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static com.github.matek2305.betting.core.match.domain.FinishMatchPolicy.afterMatchStarted;
import static com.github.matek2305.betting.core.match.domain.MatchBettingPolicy.bettingAllowedBeforeMatchStartOnly;
import static com.github.matek2305.betting.core.match.domain.MatchRewardingPolicy.defaultRewards;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

@RequiredArgsConstructor
public class BettingRoom {

    private final BettingRoomPolicy bettingRoomPolicy;
    private final DateProvider dateProvider;

    Either<AddIncomingMatchRejected, IncomingMatchAdded> add(AddExternalMatchCommand command) {
        return create(command)
                .map(match -> new ExternalMatch(match, command.origin(), command.externalId()))
                .map(IncomingMatchAdded::new);
    }

    Either<AddIncomingMatchRejected, IncomingMatchAdded> add(AddIncomingMatchCommand command) {
        return create(command).map(IncomingMatchAdded::new);
    }

    private Either<AddIncomingMatchRejected, IncomingMatch> create(AddExternalMatchCommand command) {
        return create(new AddIncomingMatchCommand(
                command.startDateTime(),
                command.homeTeam(),
                command.awayTeam()
        ));
    }

    private Either<AddIncomingMatchRejected, IncomingMatch> create(AddIncomingMatchCommand command) {
        if (!bettingRoomPolicy.check(command)) {
            return left(new AddIncomingMatchRejected(bettingRoomPolicy.getRuleDescription()));
        }

        return right(new IncomingMatch(
                new MatchInformation(
                        MatchId.of(UUID.randomUUID()),
                        command.startDateTime(),
                        command.homeTeam(),
                        command.awayTeam()
                ),
                bettingAllowedBeforeMatchStartOnly(dateProvider),
                defaultRewards(),
                afterMatchStarted(dateProvider)
        ));
    }
}
