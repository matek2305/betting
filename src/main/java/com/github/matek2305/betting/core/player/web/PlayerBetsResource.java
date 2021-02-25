package com.github.matek2305.betting.core.player.web;

import com.github.matek2305.betting.commons.CommandResult;
import com.github.matek2305.betting.commons.LoggedUser;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import com.github.matek2305.betting.core.player.domain.Betting;
import com.github.matek2305.betting.core.player.domain.MakeBetCommand;
import com.github.matek2305.betting.core.player.domain.PlayerId;
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

@Path("player_bets")
@RequiredArgsConstructor
public class PlayerBetsResource {

    private final Betting betting;
    private final LoggedUser loggedUser;

    @POST
    @RolesAllowed("betting-app-user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(AddPlayerBetRequest request) {
        return API.Match(betting.makeBet(toMakeBetCommand(request))).of(
                Case($(instanceOf(CommandResult.Allowed.class)), this::created),
                Case($(instanceOf(CommandResult.Rejected.class)), this::badRequest));
    }

    private MakeBetCommand toMakeBetCommand(AddPlayerBetRequest request) {
        return new MakeBetCommand(
                PlayerId.of(loggedUser.getName()),
                MatchId.of(request.matchId()),
                MatchScore.of(request.homeTeamScore(), request.awayTeamScore()));
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
