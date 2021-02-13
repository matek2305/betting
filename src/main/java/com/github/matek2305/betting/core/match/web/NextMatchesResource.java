package com.github.matek2305.betting.core.match.web;

import com.github.matek2305.betting.core.match.domain.CreateIncomingMatch;
import com.github.matek2305.betting.core.match.domain.CreateIncomingMatchCommand;
import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.IncomingMatches;
import com.github.matek2305.betting.core.match.domain.Team;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("next_matches")
@RequiredArgsConstructor
public class NextMatchesResource {

    private static final String DEFAULT_NEXT_MATCHES_LIMIT = "10";

    private final CreateIncomingMatch createIncomingMatch;
    private final IncomingMatches incomingMatches;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(AddNewMatchRequest request) {
        createIncomingMatch.create(toCreateCommand(request));
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<MatchResponse> get(@QueryParam("limit") @DefaultValue(DEFAULT_NEXT_MATCHES_LIMIT) int howMany) {
        return incomingMatches.findNext(howMany)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private CreateIncomingMatchCommand toCreateCommand(AddNewMatchRequest request) {
        return new CreateIncomingMatchCommand(
                request.startDateTime(),
                Team.of(request.homeTeamName()),
                Team.of(request.awayTeamName()));
    }

    private MatchResponse toResponse(IncomingMatch match) {
        return new MatchResponse(
                match.matchId().id(),
                match.homeTeam().name(),
                match.awayTeam().name(),
                match.startDateTime());
    }
}
