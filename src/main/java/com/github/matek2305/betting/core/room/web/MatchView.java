package com.github.matek2305.betting.core.room.web;

import com.github.matek2305.betting.core.room.readmodel.MatchesReadModelEntity;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Value
public class MatchView {
    UUID matchId;
    String homeTeamName;
    String awayTeamName;
    ZonedDateTime when;
    Set<PlayerBet> bets;

    static MatchView withHiddenBets(MatchesReadModelEntity entity, String loggedUsername) {
        return new MatchView(
                entity.matchId(),
                entity.homeTeamName(),
                entity.awayTeamName(),
                entity.when(),
                toPlayerBets(entity.bets(), bet -> bet.playerId().equals(loggedUsername))
        );
    }

    static MatchView withAllBets(MatchesReadModelEntity entity) {
        return new MatchView(
                entity.matchId(),
                entity.homeTeamName(),
                entity.awayTeamName(),
                entity.when(),
                toPlayerBets(entity.bets())
        );
    }

    private static Set<PlayerBet> toPlayerBets(List<MatchesReadModelEntity.Bet> bets) {
        return toPlayerBets(bets, bet -> true);
    }

    private static Set<PlayerBet> toPlayerBets(
            List<MatchesReadModelEntity.Bet> bets,
            Predicate<MatchesReadModelEntity.Bet> filter) {
        return bets
                .stream()
                .filter(filter)
                .map(bet -> new PlayerBet(
                        bet.score().homeTeam(),
                        bet.score().awayTeam()))
                .collect(Collectors.toSet());
    }

    @Value
    public static class PlayerBet {
        int homeTeam;
        int awayTeam;
    }
}
