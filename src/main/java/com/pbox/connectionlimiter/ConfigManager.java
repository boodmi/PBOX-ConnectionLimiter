package com.pbox.connectionlimiter;

import org.slf4j.Logger;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.nio.file.*;

public class ConfigManager {
    private final Path path;
    private final Logger logger;

    private int maxConnections;
    private int periodSeconds;
    private String denyMessage;
    private boolean debug;

    public ConfigManager(Path dir, Logger logger) {
        this.path = dir.resolve("config.toml");
        this.logger = logger;
    }

    public void load() {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.copy(getClass().getClassLoader().getResourceAsStream("config.toml"), path);
            }
            TomlParseResult result = Toml.parse(path);
            maxConnections = result.getLong("max_connections").intValue();
            periodSeconds = result.getLong("period_seconds").intValue();
            denyMessage = result.getString("deny_message");
            debug = result.getBoolean("debug");

            logger.info("[ConnectionLimiter] Конфигурация загружена.");
        } catch (IOException e) {
            logger.error("Не удалось загрузить config.toml", e);
        }
    }

    public int maxConnections() { return maxConnections; }
    public int periodSeconds() { return periodSeconds; }
    public String denyMessage() { return denyMessage; }
    public boolean debug() { return debug; }
}
