package com.github.matek2305.betting.core.match.web;

import com.github.matek2305.betting.commons.CommandResult.Allowed;
import com.github.matek2305.betting.commons.CommandResult.Rejected;
import com.github.matek2305.betting.core.match.domain.FinishMatch;
import com.github.matek2305.betting.core.match.domain.FinishMatchCommand;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import io.vavr.API;
import lombok.RequiredArgsConstructor;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Path("/finished_matches")
@RequiredArgsConstructor
public class FinishedMatchesResource {

    private final FinishMatch finishMatch;

    @POST
    @RolesAllowed("betting-app-admin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response finish(FinishMatchRequest request) {
        return API.Match(finishMatch.finishMatch(toCommand(request))).of(
                Case($(instanceOf(Allowed.class)), this::created),
                Case($(instanceOf(Rejected.class)), this::badRequest));
    }

    private FinishMatchCommand toCommand(FinishMatchRequest request) {
        return new FinishMatchCommand(
                MatchId.of(request.matchId()),
                MatchScore.of(request.homeTeamScore(), request.awayTeamScore()));
    }

    private Response created() {
        return Response.status(Response.Status.CREATED).build();
    }

    private Response badRequest(Rejected rejected) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(rejected.rejectionReason())
                .build();
    }
}
