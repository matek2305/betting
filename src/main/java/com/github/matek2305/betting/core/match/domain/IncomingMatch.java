package com.github.matek2305.betting.core.match.domain;

import com.github.matek2305.betting.core.match.domain.MatchEvent.IncomingMatchCreated;
import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchFinished;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public final class IncomingMatch implements Match {

    @Getter
    private final MatchInformation matchInformation;
    private final MatchBettingPolicy bettingPolicy;
    private final MatchRewardingPolicy rewardingPolicy;

    public boolean isBettingAllowed() {
        return bettingPolicy.playerCanBet(this);
    }

    public MatchFinished finish(FinishMatchCommand command) {
        return new MatchFinished(
                command.matchId(),
                command.result(),
                new MatchRewards(
                        rewardingPolicy.getPointsForExactResultHit(),
                        rewardingPolicy.getPointsForWinningTeamHit(),
                        rewardingPolicy.getPointsForDraw(),
                        rewardingPolicy.getPointsForMissingBet()));
    }

    public FinishedMatch handle(MatchFinished matchFinished) {
        return new FinishedMatch(matchInformation, matchFinished.result());
    }

    static IncomingMatchCreated create(CreateIncomingMatchCommand command) {
        return new IncomingMatchCreated(
                MatchId.of(UUID.randomUUID()),
                command.startDateTime(),
                command.homeTeam(),
                command.awayTeam());
    }
}
