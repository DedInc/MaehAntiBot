package com.github.dedinc.maehantibot.command;

import com.github.dedinc.maehantibot.command.commands.ReloadCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager {

    public static void load(JavaPlugin plugin) {
        plugin.getCommand("mab").setExecutor(new ReloadCommand(plugin));
    }
}
