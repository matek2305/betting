package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.match.domain.IncomingMatch;
import com.github.matek2305.betting.match.domain.MatchId;
import com.github.matek2305.betting.match.domain.MatchScore;
import com.github.matek2305.betting.player.domain.PlayerEvent.BetNotFound;
import com.github.matek2305.betting.player.domain.PlayerEvent.PointsRewarded;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import java.util.function.Predicate;

import static io.vavr.API.*;

@Getter
@RequiredArgsConstructor
public class Player {

    private final PlayerId playerId;

    @With
    private final PlayerBets bets;

    @With
    private final PlayerPoints points;

    public Player(PlayerId playerId) {
        this(playerId, new PlayerBets(), new PlayerPoints());
    }

    PlayerEvent placeBet(IncomingMatch match, MatchScore bet) {
        if (match.isBettingAllowed()) {
            return betSuccessful(match.matchId(), bet);
        } else {
            return betRejected(match.matchId());
        }
    }

    PlayerEvent rewardPoints(RewardPlayersCommand command) {
        if (!bets.bets().containsKey(command.matchId())) {
            return new BetNotFound(playerId, command.matchId());
        }

        int scoredPoints = Match(bets.bets().get(command.matchId())).of(
                Case($(command.result()), () -> command.rewards().pointsForExactResultHit()),
                Case($(winningTeamHit(command.result())), () -> command.rewards().pointsForWinningTeamHit()),
                Case($(draw(command.result())), () -> command.rewards().pointsForDrawHit()),
                Case($(), () -> command.rewards().pointsForMissingBet()));

        return new PointsRewarded(playerId, command.matchId(), scoredPoints);
    }

    private PlayerEvent betSuccessful(MatchId matchId, MatchScore bet) {
        return new PlayerEvent.PlayerBetMade(playerId, matchId, bet);
    }

    private PlayerEvent betRejected(MatchId matchId) {
        return new PlayerEvent.PlayerBetRejected(playerId, matchId);
    }

    private static Predicate<MatchScore> winningTeamHit(MatchScore result) {
        Predicate<MatchScore> homeTeamWin = bet -> bet.homeTeamWon() && result.homeTeamWon();
        Predicate<MatchScore> awayTeamWin = bet -> bet.awayTeamWon() && result.awayTeamWon();
        return homeTeamWin.or(awayTeamWin);
    }

    private static Predicate<MatchScore> draw(MatchScore result) {
        return bet -> bet.draw() && result.draw();
    }
}
