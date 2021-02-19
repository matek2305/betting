package com.github.matek2305.betting.core.match.domain

import com.github.matek2305.betting.commons.DomainSpecification
import com.github.matek2305.betting.core.match.infrastructure.InMemoryMatchRepository
import com.github.matek2305.betting.date.DateProvider
import spock.lang.Subject

import java.time.ZonedDateTime

class CorrectMatchResultTest extends DomainSpecification implements MatchFixtures {

    def dateProviderMock = Mock(DateProvider)

    def matches = withEventsPublisher({new InMemoryMatchRepository(it, dateProviderMock) })

    @Subject
    def correctMatchResult = new CorrectMatchResult(matches, matches)

    def "should correct finished match result"() {
        given:
            def rewards = defaultRewards()
            def matchId = randomFinishedMatch(rewards)
        
        when:
            def result = correctMatchResult(matchId)
        
        then:
            def event = findPublishedEvent(MatchEvent.MatchResultCorrected)
            event.matchId() == matchId
            event.result() == result
            event.rewards() == rewards
    }

    def "should throw match not found when trying to correct result for not finished match"() {
        given:
            def matchId = randomIncomingMatch()
        
        when:
            correctMatchResult(matchId)
        
        then:
            def matchNotFound = thrown(MatchNotFoundException)
            matchNotFound.message.contains(matchId.toString())
    }
    
    private MatchId randomIncomingMatch() {
        var matchId = randomMatchId()
        matches.publish(new MatchEvent.IncomingMatchCreated(matchId, ZonedDateTime.now().minusHours(2), randomTeam(), randomTeam()))
        return matchId
    }
    
    private MatchId randomFinishedMatch(MatchRewards rewards) {
        var matchId = randomIncomingMatch()
        matches.publish(new MatchEvent.MatchFinished(matchId, randomScore(), rewards))
        return matchId
    }
    
    private MatchScore correctMatchResult(MatchId matchId) {
        def command = new CorrectMatchResultCommand(matchId, randomScore())
        correctMatchResult.correctResult(command)
        return command.result()
    }
}
