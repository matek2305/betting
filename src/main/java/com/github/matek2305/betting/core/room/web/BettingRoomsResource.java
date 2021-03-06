package com.github.matek2305.betting.core.room.web;

import com.github.matek2305.betting.commons.CommandResult;
import com.github.matek2305.betting.commons.LoggedUser;
import com.github.matek2305.betting.core.match.domain.Team;
import com.github.matek2305.betting.core.match.domain.external.ExternalId;
import com.github.matek2305.betting.core.match.domain.external.Origin;
import com.github.matek2305.betting.core.room.domain.AddExternalMatchCommand;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatch;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchCommand;
import com.github.matek2305.betting.core.room.readmodel.MatchesReadModel;
import io.quarkus.security.Authenticated;
import io.vavr.API;
import lombok.RequiredArgsConstructor;

import javax.annotation.security.RolesAllowed;
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

import static com.github.matek2305.betting.core.room.web.MatchView.withHiddenBets;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Authenticated
@Path("betting_rooms")
@RequiredArgsConstructor
public class BettingRoomsResource {

    private static final String DEFAULT_NEXT_MATCHES_LIMIT = "10";

    private final AddIncomingMatch addIncomingMatch;
    private final MatchesReadModel matchesReadModel;
    private final LoggedUser loggedUser;

    @POST
    @Path("/global/matches")
    @RolesAllowed("betting-app-admin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(AddNewMatchRequest request) {
        return API.Match(handle(request)).of(
                Case($(instanceOf(CommandResult.Allowed.class)), this::created),
                Case($(instanceOf(CommandResult.Rejected.class)), this::badRequest));
    }

    @GET
    @Path("/global/next_matches")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MatchView> getNext(@QueryParam("limit") @DefaultValue(DEFAULT_NEXT_MATCHES_LIMIT) int howMany) {
        return matchesReadModel.findForBetting(howMany)
                .stream()
                .map(entity -> withHiddenBets(entity, loggedUser.getName()))
                .collect(Collectors.toList());
    }

    @GET
    @Path("/global/started_matches")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MatchView> getStarted(@QueryParam("limit") @DefaultValue(DEFAULT_NEXT_MATCHES_LIMIT) int howMany) {
        return matchesReadModel.findStarted(howMany)
                .stream()
                .map(MatchView::withAllBets)
                .collect(Collectors.toList());
    }

    private CommandResult handle(AddNewMatchRequest request) {
        return request.externalId() != null
                ? addIncomingMatch.add(toAddExternalMatchCommand(request))
                : addIncomingMatch.add(toAddIncomingMatchCommand(request));
    }

    private AddExternalMatchCommand toAddExternalMatchCommand(AddNewMatchRequest request) {
        return new AddExternalMatchCommand(
                request.startDateTime(),
                Team.of(request.homeTeamName()),
                Team.of(request.awayTeamName()),
                Origin.of(request.origin()),
                ExternalId.of(request.externalId()));
    }

    private AddIncomingMatchCommand toAddIncomingMatchCommand(AddNewMatchRequest request) {
        return new AddIncomingMatchCommand(
                request.startDateTime(),
                Team.of(request.homeTeamName()),
                Team.of(request.awayTeamName()));
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
