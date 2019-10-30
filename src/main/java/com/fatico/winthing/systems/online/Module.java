package com.fatico.winthing.systems.online;

import com.google.inject.PrivateModule;

public class Module extends PrivateModule {

    @Override
    protected void configure() {
        bind(OnlineController.class).asEagerSingleton();
    }
}
