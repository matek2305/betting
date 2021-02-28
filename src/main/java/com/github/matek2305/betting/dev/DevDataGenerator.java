package com.github.matek2305.betting.dev;

import com.github.matek2305.betting.commons.DateProvider;
import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.MatchEvent;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchInformation;
import com.github.matek2305.betting.core.match.domain.MatchRepository;
import com.github.matek2305.betting.core.match.domain.MatchRewardingPolicy;
import com.github.matek2305.betting.core.match.domain.MatchRewards;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import com.github.matek2305.betting.core.match.domain.Team;
import com.github.matek2305.betting.core.room.domain.IncomingMatches;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.arc.properties.IfBuildProperty;
import io.quarkus.runtime.Startup;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.matek2305.betting.core.match.domain.FinishMatchPolicy.afterMatchStarted;
import static com.github.matek2305.betting.core.match.domain.MatchBettingPolicy.bettingAllowedBeforeMatchStartOnly;

@Startup
@ApplicationScoped
@IfBuildProfile("dev")
@IfBuildProperty(name = "betting.api-football.enabled", stringValue = "false")
@RequiredArgsConstructor
class DevDataGenerator {

    private static final String TEST_TEAM_NAMES[] = new String[]{
            "Chelsea",
            "Manchester United",
            "Arsenal",
            "Liverpool",
            "Manchester City",
            "Juventus",
            "Milan",
            "Napoli",
            "FC Barcelona",
            "Real Madrit",
            "Atletico Madrit",
            "Bayern Monachium",
            "Borussia Dortmund"
    };

    private final IncomingMatches incomingMatches;
    private final MatchRepository matchRepository;
    private final DateProvider dateProvider;

    @PostConstruct
    void load() {
        generate(this::publishRandomIncomingMatch, 10);
        generate(this::publishRandomStartedMatch, 3);
        generate(this::publishRandomFinishedMatch, 2);
    }

    private void generate(Runnable what, int howMany) {
        IntStream.range(0, howMany).forEach(i -> what.run());
    }

    private void publishRandomFinishedMatch() {
        var matchId = publishRandomIncomingMatch(ZonedDateTime.now().minusMinutes(RandomUtils.nextInt(110, 2880)));
        matchRepository.publish(new MatchEvent.MatchFinished(
                matchId,
                randomScore(),
                defaultRewards()));
    }

    private void publishRandomStartedMatch() {
        publishRandomIncomingMatch(ZonedDateTime.now().minusMinutes(RandomUtils.nextInt(0, 110)));
    }

    private void publishRandomIncomingMatch() {
        publishRandomIncomingMatch(ZonedDateTime.now().plusHours(1 + RandomUtils.nextInt(0, 48)));
    }

    private MatchId publishRandomIncomingMatch(ZonedDateTime startDateTime) {
        var matchId = MatchId.of(UUID.randomUUID());
        var teams = generateTeams();

        incomingMatches.save(new IncomingMatch(
                new MatchInformation(
                        matchId,
                        startDateTime,
                        teams._1,
                        teams._2
                ),
                bettingAllowedBeforeMatchStartOnly(dateProvider),
                MatchRewardingPolicy.defaultRewards(),
                afterMatchStarted(dateProvider)
        ));

        return matchId;
    }

    private Tuple2<Team, Team> generateTeams() {
        var availableTeamsIndexes = IntStream.range(0, TEST_TEAM_NAMES.length)
                .boxed()
                .collect(Collectors.toList());

        var firstTeamIndex = availableTeamsIndexes
                .remove(RandomUtils.nextInt(0, availableTeamsIndexes.size()));

        var secondTeamIndex = availableTeamsIndexes
                .remove(RandomUtils.nextInt(0, availableTeamsIndexes.size()));

        return Tuple.of(
                Team.of(TEST_TEAM_NAMES[firstTeamIndex]),
                Team.of(TEST_TEAM_NAMES[secondTeamIndex]));
    }

    private MatchScore randomScore() {
        return MatchScore.of(RandomUtils.nextInt(0, 5), RandomUtils.nextInt(0, 5));
    }

    private MatchRewards defaultRewards() {
        var rewards = MatchRewardingPolicy.defaultRewards();
        return new MatchRewards(
                rewards.getPointsForExactResultHit(),
                rewards.getPointsForWinningTeamHit(),
                rewards.getPointsForDraw(),
                rewards.getPointsForMissingBet());
    }

}
