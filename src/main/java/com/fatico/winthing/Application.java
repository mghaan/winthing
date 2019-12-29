package com.fatico.winthing;

import com.fatico.winthing.gui.WindowGui;
import com.fatico.winthing.messaging.Engine;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Application app = new Application();

    private boolean debug = false;
    private WindowGui gui;
    private Logger logger;

    private void parseArgs(String[] args) {
        for (String arg : args) {
            if (arg.equals("-debug")) {
                debug = true;
            }
        }
    }

    public static boolean debug() {
        return app.debug;
    }

    public static WindowGui getApp() {
        return app.gui;
    }

    public static void quit() {
        app.logger.info("Application terminated.");
        System.exit(0);
    }

    public static void main(final String[] args) {
        try {
            app.parseArgs(args);

            app.logger = LoggerFactory.getLogger(Application.class);

            final Injector injector = Guice.createInjector(new ApplicationModule());
            final Engine engine = injector.getInstance(Engine.class);

            app.gui = injector.getInstance(WindowGui.class);
            app.gui.initialize();

            engine.run();

        } catch (final Throwable throwable) {
            app.logger.error("Critical error.", throwable);
            System.exit(1);
        }
    }
}
