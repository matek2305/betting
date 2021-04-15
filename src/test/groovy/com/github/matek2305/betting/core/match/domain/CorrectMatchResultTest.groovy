package com.github.matek2305.betting.core.match.domain

import com.github.matek2305.betting.commons.DateProvider
import com.github.matek2305.betting.commons.DomainSpecification
import com.github.matek2305.betting.core.match.RandomMatchFixtures
import com.github.matek2305.betting.core.match.infrastructure.InMemoryMatchRepository
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent
import spock.lang.Subject

import java.time.ZonedDateTime

class CorrectMatchResultTest extends DomainSpecification implements RandomMatchFixtures {

    def dateProviderMock = Mock(DateProvider)

    def matches = withEventsPublisher({new InMemoryMatchRepository(it, dateProviderMock) })

    @Subject
    def correctMatchResult = new CorrectMatchResult(matches, matches)

    def "should correct finished match result"() {
        given:
            def rewards = defaultMatchRewards()
            def matchId = createRandomFinishedMatch(rewards)

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
            def matchId = createRandomIncomingMatch()

        when:
            correctMatchResult(matchId)

        then:
            def matchNotFound = thrown(MatchNotFoundException)
            matchNotFound.message.contains(matchId.toString())
    }

    private MatchId createRandomIncomingMatch() {
        var incomingMatch = randomIncomingMatch(ZonedDateTime.now().minusHours(2), dateProviderMock)
        matches.publish(new AddIncomingMatchEvent.IncomingMatchAdded(incomingMatch))
        return incomingMatch.matchId()
    }

    private MatchId createRandomFinishedMatch(MatchRewards rewards) {
        var matchId = createRandomIncomingMatch()
        matches.publish(new MatchEvent.MatchFinished(matchId, randomMatchScore(), rewards))
        return matchId
    }

    private MatchScore correctMatchResult(MatchId matchId) {
        def command = new CorrectMatchResultCommand(matchId, randomMatchScore())
        correctMatchResult.correctResult(command)
        return command.result()
    }
}
