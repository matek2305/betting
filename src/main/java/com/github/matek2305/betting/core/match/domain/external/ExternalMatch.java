package com.github.matek2305.betting.core.match.domain.external;

import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.Match;
import com.github.matek2305.betting.core.match.domain.MatchInformation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ExternalMatch implements Match {

    private final IncomingMatch match;
    private final Origin origin;
    private final ExternalId externalId;

    @Override
    public MatchInformation matchInformation() {
        return match.matchInformation();
    }
}
