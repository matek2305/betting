package com.github.matek2305.betting.core.match.domain;

import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchResultCorrected;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FinishedMatch implements Match {

    private final MatchInformation matchInformation;
    private final MatchScore result;
    private final MatchRewardingPolicy rewardingPolicy;

    MatchResultCorrected correctResult(CorrectMatchResultCommand command) {
        return new MatchResultCorrected(
                command.matchId(),
                command.result(),
                new MatchRewards(
                        rewardingPolicy.getPointsForExactResultHit(),
                        rewardingPolicy.getPointsForWinningTeamHit(),
                        rewardingPolicy.getPointsForDraw(),
                        rewardingPolicy.getPointsForMissingBet()));
    }
}
