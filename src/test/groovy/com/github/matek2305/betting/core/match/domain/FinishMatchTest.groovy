package com.github.matek2305.betting.core.match.domain

import com.github.matek2305.betting.commons.CommandResult
import com.github.matek2305.betting.commons.DomainSpecification
import com.github.matek2305.betting.core.match.RandomMatchFixtures
import com.github.matek2305.betting.core.match.infrastructure.InMemoryMatchRepository
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent
import spock.lang.Subject

import java.time.ZonedDateTime

class FinishMatchTest extends DomainSpecification implements RandomMatchFixtures {
    
    def matches = withEventsPublisher({new InMemoryMatchRepository(it, { ZonedDateTime.now() }) })
    
    @Subject
    def finishMatch = new FinishMatch(matches, matches)

    def "should finish incoming match"() {
        given:
            def matchId = createRandomIncomingMatch(
                    ZonedDateTime.now().minusMinutes(90))
            def score = randomMatchScore()
        
        when:
            def result = finishMatch(matchId, score)
        
        then:
            result.class == CommandResult.Allowed

        and:
            def event = findPublishedEvent(MatchEvent.MatchFinished)
            event.matchId() == matchId
            event.result() == score
        
        and:
            def defaultRewards = MatchRewardingPolicy.defaultRewards()
            event.rewards().pointsForExactResultHit() == defaultRewards.pointsForExactResultHit
            event.rewards().pointsForWinningTeamHit() == defaultRewards.pointsForWinningTeamHit
            event.rewards().pointsForDrawHit() == defaultRewards.pointsForDraw
            event.rewards().pointsForMissingBet() == defaultRewards.pointsForMissingBet
    }

    def "should throw match not found when trying to finish already finished match"() {
        given:
            def matchId = createRandomIncomingMatch(
                    ZonedDateTime.now().minusMinutes(90))

        when:
            finishMatch(matchId, randomMatchScore())

        and:
            finishMatch(matchId, randomMatchScore())

        then:
            def matchNotFound = thrown(MatchNotFoundException)
            matchNotFound.message.contains(matchId.toString())
    }

    def "should not allow to finish not started match"() {
        given:
            def matchId = createRandomIncomingMatch(
                    ZonedDateTime.now().plusMinutes(30))

        when:
            def result = finishMatch(matchId, randomMatchScore())

        then:
            result.class == CommandResult.Rejected

        and:
            def event = findPublishedEvent(MatchEvent.MatchFinishRejected)
            event.matchId() == matchId
            event.rejectionReason()
    }

    private CommandResult finishMatch(MatchId matchId, MatchScore score) {
        def command = new FinishMatchCommand(matchId, score)
        return finishMatch.finishMatch(command)
    }
    
    private MatchId createRandomIncomingMatch(ZonedDateTime startDateTime) {
        var incomingMatch = randomIncomingMatch(startDateTime)
        matches.publish(new AddIncomingMatchEvent.IncomingMatchAdded(incomingMatch))
        return incomingMatch.matchId()
    }
}
