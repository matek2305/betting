package com.github.matek2305.betting.core.player.web;

import com.github.matek2305.betting.core.match.RandomMatchFixtures;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent.IncomingMatchAdded;
import com.github.matek2305.betting.core.room.domain.IncomingMatches;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.ZonedDateTime;

@QuarkusTest
@Tag("integration")
class PlayerBetsResourceTest implements RandomMatchFixtures {

    @Inject
    IncomingMatches incomingMatches;

    @Test
    void should_return_201_CREATED_when_bet_is_successful() {
        var matchId = randomMatchStartingInMoreThanOneHour();

        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body("{ \"matchId\": \"" + matchId.id() + "\", \"homeTeamScore\": 1, \"awayTeamScore\": 1 }")

                .when()
                    .post("/player_bets")

                .then()
                    .assertThat()
                    .statusCode(201)
                    .log()
                    .all();
    }

    @Test
    void should_return_400_BAD_REQUEST_when_bet_is_rejected() {
        var matchId = randomAlreadyStartedMatch();

        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body("{ \"matchId\": \"" + matchId.id() + "\", \"homeTeamScore\": 1, \"awayTeamScore\": 1 }")

                .when()
                    .post("/player_bets")

                .then()
                    .assertThat()
                    .statusCode(400)
                    .log()
                    .all();
    }

    @Test
    void should_return_404_NOT_FOUND_when_trying_to_bet_on_match_that_does_not_exist() {
        var matchId = randomMatchId();

        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body("{ \"matchId\": \"" + matchId.id() + "\", \"homeTeamScore\": 1, \"awayTeamScore\": 1 }")

                .when()
                    .post("/player_bets")

                .then()
                    .assertThat()
                    .statusCode(404)
                    .log()
                    .all();
    }

    private MatchId randomMatchStartingInMoreThanOneHour() {
        var incomingMatch = randomIncomingMatch(
                ZonedDateTime.now().plusHours(1).plusMinutes(RandomUtils.nextInt(5, 30)));

        incomingMatches.publish(new IncomingMatchAdded(incomingMatch));
        return incomingMatch.matchId();
    }

    private MatchId randomAlreadyStartedMatch() {
        var incomingMatch = randomIncomingMatch(
                ZonedDateTime.now().minusMinutes(RandomUtils.nextInt(5, 30)));

        incomingMatches.publish(new IncomingMatchAdded(incomingMatch));
        return incomingMatch.matchId();
    }

}