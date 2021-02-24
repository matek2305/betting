package com.github.matek2305.betting.core.match.web;

import com.github.matek2305.betting.core.match.RandomMatchFixtures;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.room.domain.IncomingMatches;
import com.github.matek2305.betting.commons.DateProvider;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.UUID;

@QuarkusTest
@Tag("integration")
class FinishedMatchesResourceTest implements RandomMatchFixtures {

    @Inject
    IncomingMatches incomingMatches;

    @Inject
    DateProvider dateProvider;

    @Test
    void should_return_201_CREATED_for_successful_match_finish() {
        var matchId = randomMatch(ZonedDateTime.now().minusMinutes(100));

        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body("{ \"matchId\": \"" + matchId.id() + "\", \"homeTeamScore\": 1, \"awayTeamScore\": 1 }")

                .when()
                    .post("/finished_matches")

                .then()
                    .assertThat()
                    .statusCode(201)
                    .log()
                    .all();

    }

    @Test
    void should_return_400_BAD_REQUEST_when_trying_to_finish_not_started_match() {
        var matchId = randomMatch(ZonedDateTime.now().plusMinutes(10));

        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body("{ \"matchId\": \"" + matchId.id() + "\", \"homeTeamScore\": 1, \"awayTeamScore\": 1 }")

                .when()
                    .post("/finished_matches")

                .then()
                    .assertThat()
                    .statusCode(400)
                    .log()
                    .all();
    }

    @Test
    void should_return_404_NOT_FOUND_when_match_does_not_exist() {
        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body("{ \"matchId\": \"" + UUID.randomUUID() + "\", \"homeTeamScore\": 1, \"awayTeamScore\": 1 }")

                .when()
                    .post("/finished_matches")

                .then()
                    .assertThat()
                    .statusCode(404)
                    .log()
                    .all();
    }

    private MatchId randomMatch(ZonedDateTime startDateTime) {
        var incomingMatch = randomIncomingMatch(startDateTime, dateProvider);
        incomingMatches.save(incomingMatch);
        return incomingMatch.matchId();
    }
}