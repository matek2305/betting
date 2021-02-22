package com.github.matek2305.betting.core.room.domain;

import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchInformation;
import com.github.matek2305.betting.date.DateProvider;
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

    Either<AddIncomingMatchRejected, IncomingMatch> add(AddIncomingMatchCommand command) {
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
