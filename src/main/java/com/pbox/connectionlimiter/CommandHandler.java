package com.pbox.connectionlimiter;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;

import java.util.List;

public class CommandHandler implements SimpleCommand {

    private final ConfigManager config;
    private final ConnectionLimiter limiter;

    public CommandHandler(ConfigManager config, ConnectionLimiter limiter) {
        this.config = config;
        this.limiter = limiter;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            source.sendMessage(Component.text("Доступные команды: reload, enable, disable"));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!source.hasPermission("connectionlimiter.reload")) {
                    source.sendMessage(Component.text("Недостаточно прав."));
                    return;
                }
                config.load();
                source.sendMessage(Component.text("Конфигурация перезагружена."));
            }
            case "enable" -> {
                if (!source.hasPermission("connectionlimiter.toggle")) {
                    source.sendMessage(Component.text("Недостаточно прав."));
                    return;
                }
                limiter.setEnabled(true);
                source.sendMessage(Component.text("PBOX-ConnectionLimiter включён."));
            }
            case "disable" -> {
                if (!source.hasPermission("connectionlimiter.toggle")) {
                    source.sendMessage(Component.text("Недостаточно прав."));
                    return;
                }
                limiter.setEnabled(false);
                source.sendMessage(Component.text("PBOX-ConnectionLimiter отключён."));
            }
            default -> source.sendMessage(Component.text("Неизвестная команда."));
        }
    }
}
