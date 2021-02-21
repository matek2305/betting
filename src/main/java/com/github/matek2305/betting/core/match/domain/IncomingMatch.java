package com.github.matek2305.betting.core.match.domain;

import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchFinishRejected;
import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchFinished;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class IncomingMatch implements Match {

    @Getter
    private final MatchInformation matchInformation;
    private final MatchBettingPolicy bettingPolicy;
    private final MatchRewardingPolicy rewardingPolicy;
    private final FinishMatchPolicy finishMatchPolicy;

    public boolean isBettingAllowed() {
        return bettingPolicy.playerCanBet(this);
    }

    public MatchEvent finish(FinishMatchCommand command) {
        if (finishMatchPolicy.check(this)) {
            return new MatchFinished(
                    matchId(),
                    command.result(),
                    new MatchRewards(
                            rewardingPolicy.getPointsForExactResultHit(),
                            rewardingPolicy.getPointsForWinningTeamHit(),
                            rewardingPolicy.getPointsForDraw(),
                            rewardingPolicy.getPointsForMissingBet()));
        } else {
            return new MatchFinishRejected(
                    matchId(),
                    finishMatchPolicy.getRuleDescription());
        }
    }

    public FinishedMatch handle(MatchFinished matchFinished) {
        return new FinishedMatch(
                matchInformation,
                matchFinished.result(),
                rewardingPolicy);
    }
}
