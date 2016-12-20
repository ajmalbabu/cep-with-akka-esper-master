package com.cep.lib.service;

import com.cep.lib.domain.Parameters;
import com.cep.lib.domain.Time;
import com.cep.lib.domain.TimeToLive;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * Helps to test actor removal functionality.
 */
public class ActorRemovalDetector extends MessageExpiryDetector implements TimeToLive {

    private Time actorTTL;

    public ActorRemovalDetector(ApplicationContext applicationContext, Parameters parameters) {
        super(applicationContext, parameters);
        actorTTL = new Time(parameters.parseLong(ACTOR_TIME_TO_LIVE_SECONDS), TimeUnit.SECONDS);
    }

    @Override
    public Time actorTtl() {
        return actorTTL;
    }
}
