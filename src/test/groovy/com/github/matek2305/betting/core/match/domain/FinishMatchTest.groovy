package com.github.matek2305.betting.core.match.domain

import com.github.matek2305.betting.commons.DomainSpecification
import com.github.matek2305.betting.date.DateProvider
import com.github.matek2305.betting.core.match.infrastructure.InMemoryMatchRepository
import org.apache.commons.lang3.RandomUtils
import spock.lang.Subject

import java.time.ZonedDateTime

import static java.time.ZonedDateTime.parse

class FinishMatchTest extends DomainSpecification {
    
    def dateProviderMock = Mock(DateProvider)
    
    def matches = withEventsPublisher({
        new InMemoryMatchRepository(new MatchBettingPolicies(dateProviderMock), it)
    })
    
    @Subject
    def finishMatch = new FinishMatch(matches, matches)
    
    def "should finish incoming match"() {
        given:
            def matchId = randomIncomingMatch(parse('2021-02-06T13:30Z'))
        
        when:
            def result = finishMatch(matchId)
        
        then:
            def event = findPublishedEvent(MatchEvent.MatchFinished)
            event.matchId() == matchId
            event.result() == result
        
        and:
            def defaultRewards = MatchRewardingPolicy.defaultRewards()
            event.rewards().pointsForExactResultHit() == defaultRewards.pointsForExactResultHit
            event.rewards().pointsForWinningTeamHit() == defaultRewards.pointsForWinningTeamHit
            event.rewards().pointsForDrawHit() == defaultRewards.pointsForDraw
            event.rewards().pointsForMissingBet() == defaultRewards.pointsForMissingBet
    }
    
    private MatchScore finishMatch(MatchId matchId) {
        def command = new FinishMatchCommand(matchId, randomScore())
        finishMatch.finishMatch(command)
        return command.result()
    }
    
    private MatchId randomIncomingMatch(ZonedDateTime startDateTime) {
        var matchId = randomMatchId()
        matches.publish(new MatchEvent.NewMatchAdded(matchId, startDateTime))
        return matchId
    }
    
    private static MatchId randomMatchId() {
        return new MatchId(UUID.randomUUID())
    }
    
    private static MatchScore randomScore() {
        return new MatchScore(RandomUtils.nextInt(0, 3), RandomUtils.nextInt(0, 3))
    }
}
