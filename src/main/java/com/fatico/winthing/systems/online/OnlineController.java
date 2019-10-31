package com.fatico.winthing.systems.online;

import com.fatico.winthing.common.BaseController;
import com.fatico.winthing.messaging.QualityOfService;
import com.fatico.winthing.messaging.Registry;

import com.google.gson.JsonPrimitive;
import com.google.inject.Inject;

public class OnlineController extends BaseController {

    @Inject
    public OnlineController(final Registry registry) {
        // TODO: Move from "system" to "" (breaking change!) on v2.0
        super("system");
        registry.queueInitialMessage(
            makeMessage(
                "online",
                new JsonPrimitive(true),
                QualityOfService.AT_LEAST_ONCE,
                true
            )
        );
        registry.setWill(
            makeMessage(
                "online",
                new JsonPrimitive(false),
                QualityOfService.AT_LEAST_ONCE,
                true
            )
        );
    }
}
