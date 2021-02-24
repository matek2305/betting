package com.github.matek2305.betting.core;

import com.github.matek2305.betting.commons.CommandResult;
import com.github.matek2305.betting.core.match.domain.FinishMatch;
import com.github.matek2305.betting.core.match.domain.FinishMatchCommand;
import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import com.github.matek2305.betting.core.match.domain.Team;
import com.github.matek2305.betting.core.player.domain.BetPoints;
import com.github.matek2305.betting.core.player.domain.Betting;
import com.github.matek2305.betting.core.player.domain.MakeBetCommand;
import com.github.matek2305.betting.core.player.domain.PlayerId;
import com.github.matek2305.betting.core.player.domain.Players;
import com.github.matek2305.betting.core.player.domain.Points;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatch;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchCommand;
import com.github.matek2305.betting.core.room.domain.IncomingMatches;
import com.github.matek2305.betting.commons.DateProvider;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.vavr.control.Option;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.time.ZonedDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
@Tag("integration")
class BetPointsCalculationTest {

    @Inject
    AddIncomingMatch addIncomingMatch;

    @Inject
    IncomingMatches incomingMatches;

    @Inject
    Betting betting;

    @Inject
    FinishMatch finishMatch;

    @Inject
    Players players;

    @Inject
    DateProvider dateProvider;

    @BeforeAll
    static void beforeAll() {
        var dateProviderMock = mock(DateProvider.class);
        when(dateProviderMock.getCurrentDateTime()).then(inv -> ZonedDateTime.now());
        QuarkusMock.installMockForType(dateProviderMock, DateProvider.class);
    }

    @Test
    void should_calculate_players_points_after_match_finish() {
        // given: match that starts in two hours
        var incomingMatchId = randomMatchStartingInTwoHours();
        // and: players betting on this match
        var firstPlayer = makeBet(incomingMatchId, MatchScore.of(2, 2));
        var secondPlayer = makeBet(incomingMatchId, MatchScore.of(1, 2));
        var thirdPlayer = makeBet(incomingMatchId, MatchScore.of(1, 3));
        // when: finish match
        finishMatch(incomingMatchId, MatchScore.of(1, 3));
        // then: players should have calculated points
        checkReceivedPoints(firstPlayer, incomingMatchId, Points.of(0));
        checkReceivedPoints(secondPlayer, incomingMatchId, Points.of(2));
        checkReceivedPoints(thirdPlayer, incomingMatchId, Points.of(5));
    }

    private MatchId randomMatchStartingInTwoHours() {
        assertCommandSuccess(
                addIncomingMatch.add(new AddIncomingMatchCommand(
                        dateProvider.getCurrentDateTime().plusHours(2),
                        Team.of(RandomStringUtils.randomAlphabetic(10)),
                        Team.of(RandomStringUtils.randomAlphabetic(10))
                ))
        );

        return incomingMatches.findNext(1)
                .stream()
                .findFirst()
                .map(IncomingMatch::matchId)
                .orElseThrow(() -> new IllegalStateException("Added match not found"));
    }

    private PlayerId makeBet(MatchId matchId, MatchScore bet) {
        var playerId = PlayerId.of(RandomStringUtils.randomAlphabetic(5));
        assertCommandSuccess(betting.makeBet(new MakeBetCommand(playerId, matchId, bet)));
        return playerId;
    }

    private void finishMatch(MatchId matchId, MatchScore score) {
        moveInTimeAfterMatchFinish(matchId);
        assertCommandSuccess(finishMatch.finishMatch(new FinishMatchCommand(matchId, score)));
    }

    private void moveInTimeAfterMatchFinish(MatchId matchId) {
        var match = incomingMatches.getIncomingMatchBy(matchId);
        when(dateProvider.getCurrentDateTime()).thenReturn(match.startDateTime().plusHours(2));
    }

    private void assertCommandSuccess(CommandResult result) {
        assertEquals(CommandResult.Allowed.class, result.getClass());
    }

    private void checkReceivedPoints(PlayerId playerId, MatchId matchId, Points expectedPoints) {
        var betPoints = await().atMost(5, TimeUnit.SECONDS)
                .until(playerPointsForMatchCalculated(playerId, matchId), Option::isDefined)
                .get();

        assertEquals(expectedPoints, betPoints.points());
    }

    private Callable<Option<BetPoints>> playerPointsForMatchCalculated(PlayerId playerId, MatchId matchId) {
        return () -> players.getBy(playerId).points().get(matchId);
    }
}
