package com.github.matek2305.betting.core.room.web;

import com.github.matek2305.betting.commons.CommandResult;
import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.Team;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatch;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchCommand;
import com.github.matek2305.betting.core.room.domain.IncomingMatches;
import io.vavr.API;
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

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Path("betting_rooms")
@RequiredArgsConstructor
public class BettingRoomsResource {

    private static final String DEFAULT_NEXT_MATCHES_LIMIT = "10";

    private final AddIncomingMatch addIncomingMatch;
    private final IncomingMatches incomingMatches;

    @POST
    @Path("/global/matches")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(AddNewMatchRequest request) {
        return API.Match(addIncomingMatch.add(toCommand(request))).of(
                Case($(instanceOf(CommandResult.Allowed.class)), this::created),
                Case($(instanceOf(CommandResult.Rejected.class)), this::badRequest));
    }

    @GET
    @Path("/global/next_matches")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MatchResponse> getNext(@QueryParam("limit") @DefaultValue(DEFAULT_NEXT_MATCHES_LIMIT) int howMany) {
        return incomingMatches.findNext(howMany)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/global/started_matches")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MatchResponse> getStarted(@QueryParam("limit") @DefaultValue(DEFAULT_NEXT_MATCHES_LIMIT) int howMany) {
        return incomingMatches.findStarted(howMany)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AddIncomingMatchCommand toCommand(AddNewMatchRequest request) {
        return new AddIncomingMatchCommand(
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

    private Response created() {
        return Response.status(Response.Status.CREATED).build();
    }

    private Response badRequest(CommandResult.Rejected rejected) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(rejected.rejectionReason())
                .build();
    }
}
