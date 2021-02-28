package com.github.matek2305.betting.core.match.infrastructure.persistence;

import com.github.matek2305.betting.commons.DateProvider;
import com.github.matek2305.betting.core.match.domain.FinishedMatch;
import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.Match;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchInformation;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import com.github.matek2305.betting.core.match.domain.Team;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;

import static com.github.matek2305.betting.core.match.domain.FinishMatchPolicy.afterMatchStarted;
import static com.github.matek2305.betting.core.match.domain.MatchBettingPolicy.bettingAllowedBeforeMatchStartOnly;
import static com.github.matek2305.betting.core.match.domain.MatchRewardingPolicy.defaultRewards;

@ApplicationScoped
@RequiredArgsConstructor
class DomainMatchFactory {

    private final DateProvider dateProvider;

    Match create(MatchEntity entity) {
        return entity.finished ? createFinishedMatch(entity) : toIncomingMatch(entity);
    }

    FinishedMatch createFinishedMatch(MatchEntity entity) {
        return new FinishedMatch(
                toMatchInformation(entity),
                MatchScore.of(entity.homeTeamScore, entity.awayTeamScore),
                defaultRewards()
        );
    }

    private IncomingMatch toIncomingMatch(MatchEntity entity) {
        return new IncomingMatch(
                toMatchInformation(entity),
                bettingAllowedBeforeMatchStartOnly(dateProvider),
                defaultRewards(),
                afterMatchStarted(dateProvider)
        );
    }

    private MatchInformation toMatchInformation(MatchEntity entity) {
        return new MatchInformation(
                MatchId.of(entity.uuid),
                entity.startDateTime,
                Team.of(entity.homeTeamName),
                Team.of(entity.awayTeamName)
        );
    }
}
