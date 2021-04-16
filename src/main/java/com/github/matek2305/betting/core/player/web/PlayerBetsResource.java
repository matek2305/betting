package com.github.matek2305.betting.core.player.web;

import com.github.matek2305.betting.commons.CommandResult;
import com.github.matek2305.betting.commons.LoggedUser;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import com.github.matek2305.betting.core.player.domain.Betting;
import com.github.matek2305.betting.core.player.domain.MakeBetCommand;
import com.github.matek2305.betting.core.player.domain.PlayerId;
import com.github.matek2305.betting.core.player.web.AddPlayerBetsRejectionsResponse.BetRejection;
import com.github.matek2305.betting.core.player.web.AddPlayerBetsRequest.SingleMatchBetRequest;
import io.quarkus.security.Authenticated;
import io.vavr.API;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.HashSet;
import java.util.Set;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.run;
import static io.vavr.Predicates.instanceOf;

@Authenticated
@Path("player_bets")
@RequiredArgsConstructor
public class PlayerBetsResource {

    private final Betting betting;
    private final LoggedUser loggedUser;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(AddPlayerBetsRequest request) {
        Set<BetRejection> rejections = new HashSet<>();

        request.bets().forEach(betRequest ->
                API.Match(betting.makeBet(toMakeBetCommand(betRequest))).option(

                        Case($(instanceOf(CommandResult.Rejected.class)), rejected ->
                                run(() -> rejections.add(rejection(betRequest, rejected))))

                )
        );

        return Response.ok(new AddPlayerBetsRejectionsResponse(rejections)).build();
    }

    private MakeBetCommand toMakeBetCommand(SingleMatchBetRequest request) {
        return new MakeBetCommand(
                PlayerId.of(loggedUser.getName()),
                MatchId.of(request.matchId()),
                MatchScore.of(request.homeTeamScore(), request.awayTeamScore()));
    }

    private BetRejection rejection(SingleMatchBetRequest betRequest, CommandResult.Rejected rejected) {
        return new BetRejection(
                betRequest.matchId(),
                rejected.rejectionReason()
        );
    }
}
