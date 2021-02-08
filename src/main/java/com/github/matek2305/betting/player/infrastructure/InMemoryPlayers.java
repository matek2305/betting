package com.github.matek2305.betting.player.infrastructure;

import com.github.matek2305.betting.commons.EventsPublisher;
import com.github.matek2305.betting.match.domain.MatchId;
import com.github.matek2305.betting.match.domain.MatchScore;
import com.github.matek2305.betting.player.domain.BetPoints;
import com.github.matek2305.betting.player.domain.Player;
import com.github.matek2305.betting.player.domain.PlayerBets;
import com.github.matek2305.betting.player.domain.PlayerEvent;
import com.github.matek2305.betting.player.domain.PlayerEvent.NewPlayerCreated;
import com.github.matek2305.betting.player.domain.PlayerEvent.PlayerBetMade;
import com.github.matek2305.betting.player.domain.PlayerEvent.PointsRewarded;
import com.github.matek2305.betting.player.domain.PlayerId;
import com.github.matek2305.betting.player.domain.PlayerPoints;
import com.github.matek2305.betting.player.domain.Players;
import com.google.common.collect.ImmutableMap;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@RequiredArgsConstructor
public class InMemoryPlayers implements Players {

    private final Map<PlayerId, Player> players = new ConcurrentHashMap<>();

    private final EventsPublisher publisher;

    @Override
    public Option<Player> findBy(PlayerId playerId) {
        return Option.of(players.get(playerId));
    }

    @Override
    public Set<Player> findByBetMatchId(MatchId matchId) {
        return players.values()
                .stream()
                .filter(player -> player.bets().bets().containsKey(matchId))
                .collect(Collectors.toSet());
    }

    @Override
    public void publish(PlayerEvent event) {
        var player = Match(event).of(
                Case($(instanceOf(NewPlayerCreated.class)), this::saveNewPlayer),
                Case($(), this::handleNextEvent));

        players.put(player.playerId(), player);
        publisher.publish(event);
    }

    private Player saveNewPlayer(NewPlayerCreated newPlayerCreated) {
        return new Player(newPlayerCreated.playerId());
    }

    private Player handleNextEvent(PlayerEvent event) {
        return Match(event).of(
                Case($(instanceOf(PlayerBetMade.class)), this::savePlayerBet),
                Case($(instanceOf(PointsRewarded.class)), this::rewardPoints),
                Case($(), () -> players.get(event.playerId())));
    }

    private Player savePlayerBet(PlayerBetMade playerBetMade) {
        var player = players.get(playerBetMade.playerId());
        var bets = new ImmutableMap.Builder<MatchId, MatchScore>()
                .putAll(player.bets().bets())
                .put(playerBetMade.matchId(), playerBetMade.bet())
                .build();

        return player.withBets(new PlayerBets(bets));
    }

    private Player rewardPoints(PointsRewarded pointsRewarded) {
        var player = players.get(pointsRewarded.playerId());
        var bet = player.bets().bets().get(pointsRewarded.matchId());

        var bets = player.bets().bets().entrySet()
                .stream()
                .filter(entry -> entry.getKey() != pointsRewarded.matchId())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        var points = new ImmutableMap.Builder<MatchId, BetPoints>()
                .putAll(player.points().points())
                .put(pointsRewarded.matchId(), new BetPoints(bet, pointsRewarded.points()))
                .build();

        return player.withBets(new PlayerBets(bets)).withPoints(new PlayerPoints(points));

    }
}
