package com.github.matek2305.betting.core.match.domain

import com.github.matek2305.betting.commons.DomainSpecification
import com.github.matek2305.betting.core.match.infrastructure.InMemoryMatchRepository
import com.github.matek2305.betting.date.DateProvider
import spock.lang.Subject

import java.time.ZonedDateTime

class FinishMatchTest extends DomainSpecification implements MatchFixtures {
    
    def matches = withEventsPublisher({new InMemoryMatchRepository(it, new DateProvider()) })
    
    @Subject
    def finishMatch = new FinishMatch(matches, matches)
    
    def "should finish incoming match"() {
        given:
            def matchId = randomIncomingMatch(
                    ZonedDateTime.now().minusMinutes(90))
        
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

    def "should throw match not found when trying to finish already finished match"() {
        given:
            def matchId = randomIncomingMatch(
                    ZonedDateTime.now().minusMinutes(90))

        when:
            finishMatch(matchId)

        and:
            finishMatch(matchId)

        then:
            def matchNotFound = thrown(MatchNotFoundException)
            matchNotFound.message.contains(matchId.toString())
    }

    def "should not allow to finish not started match"() {
        given:
            def matchId = randomIncomingMatch(
                    ZonedDateTime.now().plusMinutes(30))

        when:
            finishMatch(matchId)

        then:
            def event = findPublishedEvent(MatchEvent.MatchFinishRejected)
            event.matchId() == matchId
            event.rejectionReason()
    }

    private MatchScore finishMatch(MatchId matchId) {
        def command = new FinishMatchCommand(matchId, randomScore())
        finishMatch.finishMatch(command)
        return command.result()
    }
    
    private MatchId randomIncomingMatch(ZonedDateTime startDateTime) {
        var matchId = randomMatchId()
        matches.publish(new MatchEvent.IncomingMatchCreated(matchId, startDateTime, randomTeam(), randomTeam()))
        return matchId
    }
}
