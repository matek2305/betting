package com.github.matek2305.betting.core.player.web;

import com.github.matek2305.betting.commons.LoggedUser;
import com.github.matek2305.betting.core.match.domain.Match;
import com.github.matek2305.betting.core.match.domain.MatchInformation;
import com.github.matek2305.betting.core.match.domain.MatchRepository;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import com.github.matek2305.betting.core.player.domain.Player;
import com.github.matek2305.betting.core.player.domain.PlayerBets;
import com.github.matek2305.betting.core.player.domain.PlayerId;
import com.github.matek2305.betting.core.player.domain.Players;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("players")
@RequiredArgsConstructor
public class PlayersResource {

    private final LoggedUser loggedUser;
    private final Players players;
    private final MatchRepository matchRepository;

    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    public PlayerResponse get() {
        return toResponse(players.getBy(PlayerId.of(loggedUser.getName())));
    }

    private PlayerResponse toResponse(Player player) {
        return new PlayerResponse(player.playerId().id(), toActiveBets(player.bets()));
    }

    private Set<PlayerResponse.ActiveBet> toActiveBets(PlayerBets bets) {
        var entries = bets.getAll();
        var matches = matchRepository.findBy(entries.keySet())
                .stream()
                .collect(Collectors.toMap(Match::matchId, Function.identity()));

        return entries.entrySet()
                .stream()
                .map(entry -> toActiveBet(
                        matches.get(entry.getKey()).matchInformation(),
                        entry.getValue()))
                .collect(Collectors.toSet());
    }

    private static PlayerResponse.ActiveBet toActiveBet(MatchInformation matchInformation, MatchScore bet) {
        return new PlayerResponse.ActiveBet(
                matchInformation.homeTeam().name(),
                matchInformation.awayTeam().name(),
                matchInformation.startDateTime(),
                bet.homeTeamScore(),
                bet.awayTeamScore());
    }
}
