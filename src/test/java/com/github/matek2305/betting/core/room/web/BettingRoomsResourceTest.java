package com.github.matek2305.betting.core.room.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

@QuarkusTest
@Tag("integration")
class BettingRoomsResourceTest {

    @Test
    void should_return_201_CREATED_for_successfully_added_match() {
        var inTwoHours = ZonedDateTime.now().plusHours(2);

        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body("{ \"homeTeamName\": \"Chelsea\", \"awayTeamName\": \"Arsenal\", \"startDateTime\": \"" + inTwoHours + "\" }")

                .when()
                    .post("/betting_rooms/global/matches")

                .then()
                    .assertThat()
                    .statusCode(201)
                    .log()
                    .all();
    }

    @Test
    void should_return_400_BAD_REQUEST_when_add_match_is_rejected() {
        var inFiveMinutes = ZonedDateTime.now().plusMinutes(5);

        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body("{ \"homeTeamName\": \"Chelsea\", \"awayTeamName\": \"Arsenal\", \"startDateTime\": \"" + inFiveMinutes + "\" }")

                .when()
                    .post("/betting_rooms/global/matches")

                .then()
                    .assertThat()
                    .statusCode(400)
                    .log()
                    .all();
    }
}