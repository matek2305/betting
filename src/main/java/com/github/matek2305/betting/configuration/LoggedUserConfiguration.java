package com.github.matek2305.betting.configuration;

import com.github.matek2305.betting.commons.LoggedUser;
import io.quarkus.arc.properties.IfBuildProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
class LoggedUserConfiguration {

    @Produces
    @IfBuildProperty(name = "betting.auth.enabled", stringValue = "true", enableIfMissing = true)
    public LoggedUser loggedUser(JsonWebToken jwt) {
        return jwt::getName;
    }

    @Produces
    @IfBuildProperty(name = "betting.auth.enabled", stringValue = "false")
    public LoggedUser testLoggedUser() {
        return () -> "testuser";
    }
}
