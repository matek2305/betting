package com.github.matek2305.betting.core.match.domain.external;

import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.NewMatch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Getter
@RequiredArgsConstructor
public final class ExternalMatch implements NewMatch {

    @Delegate
    private final IncomingMatch match;
    private final Origin origin;
    private final ExternalId externalId;
}
