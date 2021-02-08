package com.github.matek2305.betting.core.player.domain

import com.github.matek2305.betting.commons.DomainSpecification
import com.github.matek2305.betting.core.match.domain.MatchId
import com.github.matek2305.betting.core.match.domain.MatchRewards
import com.github.matek2305.betting.core.match.domain.MatchScore
import com.github.matek2305.betting.core.player.infrastructure.InMemoryPlayers
import spock.lang.Subject

class RewardPlayersTest extends DomainSpecification {
    
    private static final Points POINTS_FOR_EXACT_RESULT_HIT = Points.of(5)
    private static final Points POINTS_FOR_WINNING_TEAM_HIT = Points.of(2)
    private static final Points POINTS_FOR_DRAW = Points.of(3)
    private static final Points POINTS_FOR_MISSED_BET = Points.of(0)
    
    def players = withEventsPublisher({
        new InMemoryPlayers(it)
    })
    
    @Subject
    def rewardPlayers = new RewardPlayers(players)
    
    def "should reward players with points according to their bets"() {
        given:
            def matchId = new MatchId(UUID.randomUUID())
        
        and:
            def playerWithExactResultHit = randomPlayerWithBet(matchId, new MatchScore(1, 2))
            def playerWithPickedTeamHit = randomPlayerWithBet(matchId, new MatchScore(0, 2))
            def playerWithDrawBet = randomPlayerWithBet(matchId, new MatchScore(1, 1))
            def playerWithMissingBet = randomPlayerWithBet(matchId, new MatchScore(1, 0))
        
        when:
            giveRewards(matchId, matchResult)
        
        then:
            def events = findAllPublishedEvents(PlayerEvent.PointsRewarded)
            events.size() == 4
        
        and:
            events.any { it.matchId() == matchId && it.playerId() == playerWithExactResultHit && it.points() == exactResultHitPoints }
            events.any { it.matchId() == matchId && it.playerId() == playerWithPickedTeamHit && it.points() == teamPickedBetPoints }
            events.any { it.matchId() == matchId && it.playerId() == playerWithDrawBet && it.points() == drawBetPoints }
            events.any { it.matchId() == matchId && it.playerId() == playerWithMissingBet && it.points() == missingBetPoints }
        
        where:
            matchResult          || exactResultHitPoints        || teamPickedBetPoints         || drawBetPoints         || missingBetPoints
            new MatchScore(1, 2) || POINTS_FOR_EXACT_RESULT_HIT || POINTS_FOR_WINNING_TEAM_HIT || POINTS_FOR_MISSED_BET || POINTS_FOR_MISSED_BET
            new MatchScore(3, 3) || POINTS_FOR_MISSED_BET       || POINTS_FOR_MISSED_BET       || POINTS_FOR_DRAW       || POINTS_FOR_MISSED_BET
    }
    
    def "should reward player only once"() {
        given:
            def matchId = new MatchId(UUID.randomUUID())
            def playerId = randomPlayerWithBet(matchId, new MatchScore(1, 2))
        
        when:
            giveRewards(matchId, new MatchScore(1, 2))
        
        and:
            giveRewards(matchId, new MatchScore(1, 2))
    
        then:
            def events = findAllPublishedEvents(PlayerEvent.PointsRewarded)
            events.size() == 1
        
        and:
            events[0].matchId() == matchId
            events[0].playerId() == playerId
            events[0].points() == POINTS_FOR_EXACT_RESULT_HIT
    }
    
    private void giveRewards(MatchId matchId, MatchScore result) {
        rewardPlayers.give(new RewardPlayersCommand(matchId, result, defaultRewards()))
    }
    
    private PlayerId randomPlayerWithBet(MatchId matchId, MatchScore bet) {
        def playerId = new PlayerId(UUID.randomUUID())
        players.publish(new PlayerEvent.NewPlayerCreated(playerId))
        players.publish(new PlayerEvent.PlayerBetMade(playerId, matchId, bet))
        return playerId
    }
    
    private static MatchRewards defaultRewards() {
        return new MatchRewards(
                POINTS_FOR_EXACT_RESULT_HIT,
                POINTS_FOR_WINNING_TEAM_HIT,
                POINTS_FOR_DRAW,
                POINTS_FOR_MISSED_BET)
    }
}
