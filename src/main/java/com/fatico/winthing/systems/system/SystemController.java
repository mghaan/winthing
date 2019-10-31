package com.fatico.winthing.systems.system;

import com.fatico.winthing.common.BaseController;
import com.fatico.winthing.messaging.Message;
import com.fatico.winthing.messaging.QualityOfService;
import com.fatico.winthing.messaging.Registry;
import com.fatico.winthing.windows.SystemException;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.inject.Inject;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.Objects;

public class SystemController extends BaseController {

    private final SystemService systemService;
    private final SystemCommander systemCommander;

    @Inject
    public SystemController(final Registry registry, final SystemService systemService,
            final SystemCommander systemCommander) throws SystemException {
        super("system");
        this.systemService = Objects.requireNonNull(systemService);
        this.systemCommander = Objects.requireNonNull(systemCommander);

        registry.subscribe(prefix + "commands/shutdown", this::shutdown);
        registry.subscribe(prefix + "commands/suspend", this::suspend);
        registry.subscribe(prefix + "commands/hibernate", this::hibernate);
        registry.subscribe(prefix + "commands/reboot", this::reboot);
        registry.subscribe(prefix + "commands/open", this::open);
        registry.subscribe(prefix + "commands/run", this::run);

        systemCommander.parseConfig();
    }

    public void shutdown(final Message message) {
        systemService.shutdown();
    }

    void reboot(final Message message) {
        systemService.reboot();
    }

    public void suspend(final Message message) {
        systemService.suspend();
    }

    public void hibernate(final Message message) {
        systemService.hibernate();
    }

    public void run(final Message message) {
        String command;
        String parameters;
        String workingDirectory;

        try {
            final JsonArray arguments = message.getPayload().get().getAsJsonArray();
            command = arguments.get(0).getAsString();
            parameters = (arguments.size() > 1 ? arguments.get(1).getAsString() : "");
            workingDirectory = (arguments.size() > 2 ? arguments.get(2).getAsString() : null);
        } catch (final NoSuchElementException | IllegalStateException exception) {
            throw new IllegalArgumentException("Invalid arguments.");
        }

        if (systemCommander.isEnabled()) {
            String commander = systemCommander.getCommand(command);
            if (commander == null) {
                throw new SystemException("Invalid command.");
            }

            File fp = new File(commander);
            if (!fp.exists()) {
                throw new SystemException("File not found.");
            }

            command = commander;
        }

        systemService.run(command, parameters, workingDirectory);
    }

    public void open(final Message message) {
        final String uri;
        try {
            uri = message.getPayload().get().getAsString();
        } catch (final NoSuchElementException | IllegalStateException exception) {
            throw new IllegalArgumentException("Invalid arguments.");
        }
        systemService.open(uri);
    }

}
