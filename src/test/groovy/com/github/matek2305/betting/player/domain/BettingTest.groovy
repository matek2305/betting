package com.github.matek2305.betting.player.domain

import com.github.matek2305.betting.date.DateProvider
import com.github.matek2305.betting.match.domain.IncomingMatch
import com.github.matek2305.betting.match.domain.Match
import com.github.matek2305.betting.match.domain.MatchBettingPolicy
import com.github.matek2305.betting.match.domain.MatchId
import com.github.matek2305.betting.match.domain.MatchInformation
import com.github.matek2305.betting.match.domain.MatchRivals
import com.github.matek2305.betting.match.domain.MatchScore
import com.github.matek2305.betting.match.infrastructure.InMemoryMatchRepository
import com.github.matek2305.betting.player.infrastructure.InMemoryPlayers
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomUtils
import spock.lang.Specification
import spock.lang.Subject

import java.time.ZonedDateTime

class BettingTest extends Specification {
    
    def dateProviderMock = Mock(DateProvider)
    
    def matches = new InMemoryMatchRepository()
    def players = new InMemoryPlayers()
    
    @Subject
    def betting = new Betting(matches, players)
    
    def "player should be able to bet on incoming match"() {
        given:
            def currentDateTime = ZonedDateTime.parse('2021-01-31T18:02Z')
            dateProviderMock.getCurrentDateTime() >> currentDateTime
        
        and:
            def matchStartDateTime = ZonedDateTime.parse('2021-02-02T21:00Z')
            def match = incomingMatchWithDefaultPolicy(matchStartDateTime)
            def player = randomPlayer()
        
        when:
            def bet = makeBet(player, match)
        
        then:
            def playerAfterBetting = players.findBy(player.playerId())
            playerAfterBetting.get().bets().bets()[match.matchId()] == bet
    }
    
    def "player should not be able to bet on match when it has started"() {
        given:
            def currentDateTime = ZonedDateTime.parse('2021-02-02T21:02Z')
            dateProviderMock.getCurrentDateTime() >> currentDateTime
        
        and:
            def matchStartDateTime = ZonedDateTime.parse('2021-02-02T21:00Z')
            def match = incomingMatchWithDefaultPolicy(matchStartDateTime)
            def player = randomPlayer()
        
        when:
            makeBet(player, match)
        
        then:
            def playerAfterBetting = players.findBy(player.playerId())
            playerAfterBetting.get().bets().bets().isEmpty()
    }
    
    private MatchScore makeBet(Player player, Match match) {
        def bet = randomScore()
        betting.makeBet(new MakeBetCommand(player.playerId(), match.matchId(), bet))
        return bet
    }
    
    private Player randomPlayer() {
        return players.publish(new PlayerEvent.NewPlayerCreated(randomPlayerId()))
    }
    
    private IncomingMatch incomingMatchWithDefaultPolicy(ZonedDateTime startDateTime) {
        def match = new IncomingMatch(
                new MatchInformation(
                        randomMatchId(),
                        startDateTime,
                        randomRivals()),
                new MatchBettingPolicy.Default(dateProviderMock))
        matches.save(match)
        return match
    }
    
    private static PlayerId randomPlayerId() {
        return new PlayerId(UUID.randomUUID())
    }
    
    private static MatchId randomMatchId() {
        return new MatchId(UUID.randomUUID())
    }
    
    private static MatchRivals randomRivals() {
        return new MatchRivals(
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5))
    }
    
    private static MatchScore randomScore() {
        return new MatchScore(RandomUtils.nextInt(0, 3), RandomUtils.nextInt(0, 3))
    }
}
