package com.pbox.connectionlimiter;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.nio.file.Path;


import javax.inject.Inject;
import java.nio.file.Path;

@Plugin(
        id = "pbox-connectionlimiter",
        name = "PBOX-ConnectionLimiter",
        version = "1.0.0",
        authors = {"ParadiseBOX"}
)
public class PBOXConnectionLimiter {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataFolder;

    private ConfigManager config;
    private ConnectionLimiter limiter;

    @Inject
    public PBOXConnectionLimiter(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataFolder;
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent e) {
        config = new ConfigManager(dataFolder, logger);
        config.load();
        limiter = new ConnectionLimiter(config);

        server.getCommandManager().register(
                server.getCommandManager().metaBuilder("connectionlimiter").build(),
                new CommandHandler(config, limiter)
        );

        logger.info("[PBOX-ConnectionLimiter] Плагин загружен.");
    }

    @Subscribe
    public void onPreLogin(PreLoginEvent event) {
        if (!limiter.isEnabled()) return;

        // Тут нельзя вызвать getPlayer(), потому что игрок ещё не авторизован
        // Также невозможно проверить permission — в PreLogin они недоступны

        if (!limiter.tryConnect()) {
            String message = limiter.getDenyMessage();
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(
                    Component.text(message)
            ));
            if (config.debug()) {
                logger.info("[ConnectionLimiter] Отклонено: " + event.getUsername());
            }
        }
    }
}
