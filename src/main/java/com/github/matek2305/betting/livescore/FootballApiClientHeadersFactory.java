package com.github.matek2305.betting.livescore;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MultivaluedMap;

@ApplicationScoped
class FootballApiClientHeadersFactory implements ClientHeadersFactory {

    private final String apiKey;

    FootballApiClientHeadersFactory(
            @ConfigProperty(name = "api-football.rapid-api-key") String rapidApiKey) {
        this.apiKey = rapidApiKey;
    }

    @Override
    public MultivaluedMap<String, String> update(
            MultivaluedMap<String, String> incomingHeaders,
            MultivaluedMap<String, String> clientOutgoingHeaders) {

        var result = new MultivaluedMapImpl<String, String>();
        result.add("x-rapidapi-key", apiKey);
        return result;
    }
}
