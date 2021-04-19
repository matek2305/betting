package com.github.matek2305.betting.core.match.domain;

import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchFinishRejected;
import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchFinished;
import io.vavr.control.Either;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

@Getter
@RequiredArgsConstructor
public final class IncomingMatch implements NewMatch {

    private final MatchInformation matchInformation;
    private final MatchBettingPolicy bettingPolicy;
    private final MatchRewardingPolicy rewardingPolicy;
    private final FinishMatchPolicy finishMatchPolicy;

    @Override
    public ZonedDateTime bettingAvailableUntil() {
        return bettingPolicy.calculateMarginDate(this);
    }

    public boolean isBettingAllowed() {
        return bettingPolicy.playerCanBet(this);
    }

    public Either<MatchFinishRejected, MatchFinished> finish(FinishMatchCommand command) {
        if (finishMatchPolicy.check(this)) {
            return right(new MatchFinished(
                    matchId(),
                    command.result(),
                    new MatchRewards(
                            rewardingPolicy.getPointsForExactResultHit(),
                            rewardingPolicy.getPointsForWinningTeamHit(),
                            rewardingPolicy.getPointsForDraw(),
                            rewardingPolicy.getPointsForMissingBet()
                    )
            ));
        } else {
            return left(new MatchFinishRejected(
                    matchId(),
                    finishMatchPolicy.getRuleDescription()
            ));
        }
    }
}
