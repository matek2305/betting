package com.github.matek2305.betting.livescore;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/v3")
@RegisterRestClient(configKey = "api-football")
@RegisterClientHeaders(FootballApiClientHeadersFactory.class)
public interface FootballApiClient {

    @GET
    @Path("/fixtures")
    @Produces("application/json")
    FootballApiFixtureListResponse getIncomingFixtures(
            @QueryParam("league") int league,
            @QueryParam("season") String season,
            @QueryParam("next") int howMany);
}
