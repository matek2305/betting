package com.github.matek2305.betting.core.match.web;

import com.github.matek2305.betting.core.match.domain.FinishMatch;
import com.github.matek2305.betting.core.match.domain.FinishMatchCommand;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("finished_matches")
@RequiredArgsConstructor
public class FinishedMatchesResource {

    private final FinishMatch finishMatch;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response finish(FinishMatchRequest request) {
        finishMatch.finishMatch(toCommand(request));
        return Response.status(Response.Status.CREATED).build();
    }

    private FinishMatchCommand toCommand(FinishMatchRequest request) {
        return new FinishMatchCommand(
                MatchId.of(request.matchId()),
                MatchScore.of(request.homeTeamScore(), request.awayTeamScore()));
    }
}
