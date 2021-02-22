package com.github.matek2305.betting.core.match;

import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchInformation;
import com.github.matek2305.betting.core.match.domain.MatchRewards;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import com.github.matek2305.betting.core.match.domain.Team;
import com.github.matek2305.betting.date.DateProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.github.matek2305.betting.core.match.domain.FinishMatchPolicy.afterMatchStarted;
import static com.github.matek2305.betting.core.match.domain.MatchBettingPolicy.bettingAllowedBeforeMatchStartOnly;
import static com.github.matek2305.betting.core.match.domain.MatchRewardingPolicy.defaultRewards;

public interface RandomMatchFixtures {

    default IncomingMatch randomIncomingMatch(ZonedDateTime startDateTime) {
        return randomIncomingMatch(startDateTime, new DateProvider());
    }

    default IncomingMatch randomIncomingMatch(ZonedDateTime startDateTime, DateProvider dateProvider) {
        return new IncomingMatch(
                randomMatchInformation(startDateTime),
                bettingAllowedBeforeMatchStartOnly(dateProvider),
                defaultRewards(),
                afterMatchStarted(dateProvider)
        );
    }

    default MatchInformation randomMatchInformation(ZonedDateTime startDateTime) {
        return new MatchInformation(
                randomMatchId(),
                startDateTime,
                randomTeam(),
                randomTeam()
        );
    }

    default MatchId randomMatchId() {
        return MatchId.of(UUID.randomUUID());
    }

    default Team randomTeam() {
        return Team.of(RandomStringUtils.randomAlphabetic(10));
    }

    default MatchScore randomMatchScore() {
        return MatchScore.of(RandomUtils.nextInt(0, 5), RandomUtils.nextInt(0, 5));
    }

    default MatchRewards defaultMatchRewards() {
        var rewardingPolicy = defaultRewards();
        return new MatchRewards(
                rewardingPolicy.getPointsForExactResultHit(),
                rewardingPolicy.getPointsForWinningTeamHit(),
                rewardingPolicy.getPointsForDraw(),
                rewardingPolicy.getPointsForMissingBet()
        );
    }
}
