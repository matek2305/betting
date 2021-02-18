package com.github.matek2305.betting.core.player.web;

import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import com.github.matek2305.betting.core.player.domain.Betting;
import com.github.matek2305.betting.core.player.domain.MakeBetCommand;
import com.github.matek2305.betting.core.player.domain.PlayerId;
import com.github.matek2305.betting.security.LoggedUser;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("player_bets")
@RequiredArgsConstructor
public class PlayerBetsResource {

    private final Betting betting;
    private final LoggedUser loggedUser;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(AddPlayerBetRequest request) {
        betting.makeBet(toMakeBetCommand(request));
        return Response.status(Response.Status.CREATED).build();
    }

    private MakeBetCommand toMakeBetCommand(AddPlayerBetRequest request) {
        return new MakeBetCommand(
                PlayerId.of(loggedUser.getName()),
                MatchId.of(request.matchId()),
                MatchScore.of(request.homeTeamScore(), request.awayTeamScore()));
    }
}
