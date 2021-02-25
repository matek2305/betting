package com.github.matek2305.betting.dev;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.security.spi.runtime.AuthorizationController;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;

@Alternative
@IfBuildProfile("dev")
@Priority(Interceptor.Priority.LIBRARY_AFTER)
@ApplicationScoped
class DevAuthorizationController extends AuthorizationController {

    @ConfigProperty(name = "betting.auth.enabled", defaultValue = "true")
    boolean authorizationEnabled;

    @Override
    public boolean isAuthorizationEnabled() {
        return authorizationEnabled;
    }
}
