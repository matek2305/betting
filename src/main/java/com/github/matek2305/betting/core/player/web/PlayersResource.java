package com.github.matek2305.betting.core.player.web;

import com.github.matek2305.betting.commons.LoggedUser;
import com.github.matek2305.betting.core.match.domain.*;
import com.github.matek2305.betting.core.player.domain.*;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Authenticated
@Path("players")
@RequiredArgsConstructor
public class PlayersResource {

    private final LoggedUser loggedUser;
    private final Players players;
    private final MatchRepository matchRepository;
    private final FinishedMatches finishedMatches;

    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    public PlayerResponse get() {
        return toResponse(players.getBy(PlayerId.of(loggedUser.getName())));
    }

    private PlayerResponse toResponse(Player player) {
        return new PlayerResponse(
                player.playerId().id(),
                toActiveBets(player.bets()),
                toScoredPoints(player.points())
        );
    }

    private Set<PlayerResponse.MatchBet> toActiveBets(PlayerBets bets) {
        var entries = bets.getAll();
        var matches = matchRepository.findBy(entries.keySet())
                .stream()
                .collect(Collectors.toMap(Match::matchId, Function.identity()));

        return entries.entrySet()
                .stream()
                .map(entry -> toMatchBet(matches.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toSet());
    }

    private Set<PlayerResponse.ScoredPoints> toScoredPoints(PlayerPoints points) {
        var entries = points.getAll();
        var matches = finishedMatches.getFinishedMatchesBy(entries.keySet())
                .stream()
                .collect(Collectors.toMap(Match::matchId, Function.identity()));

        return entries.entrySet()
                .stream()
                .map(entry -> toScoredPoints(matches.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toSet());
    }

    private static PlayerResponse.MatchBet toMatchBet(Match match, MatchScore bet) {
        return new PlayerResponse.MatchBet(
                match.homeTeamName(),
                match.awayTeamName(),
                match.startDateTime(),
                toScore(bet)
        );
    }

    private static PlayerResponse.ScoredPoints toScoredPoints(FinishedMatch match, BetPoints points) {
        return new PlayerResponse.ScoredPoints(
                toMatchBet(match, points.bet()),
                toScore(match.result()),
                points.points().points()
        );
    }

    private static PlayerResponse.Score toScore(MatchScore score) {
        return new PlayerResponse.Score(score.homeTeam(), score.awayTeam());
    }
}
