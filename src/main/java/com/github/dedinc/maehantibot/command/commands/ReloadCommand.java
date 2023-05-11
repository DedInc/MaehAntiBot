package com.github.dedinc.maehantibot.command.commands;

import com.github.dedinc.maehantibot.MaehAntiBot;
import com.github.dedinc.maehantibot.Messages;
import com.github.dedinc.maehantibot.Storage;
import com.github.dedinc.maehantibot.event.EventManager;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReloadCommand implements CommandExecutor {

    private static JavaPlugin plugin;

    public ReloadCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Bukkit.getScheduler().cancelTasks(plugin);
        EventManager.unregister(plugin);
        Messages.unloadMessages();
        Storage.cleanup();
        new ConfigUtils(plugin);
        EventManager.register(plugin);
        Messages.loadMessages();
        MaehAntiBot.logFeatureStatus();
        sender.sendMessage("Reloaded!");
        return true;
    }
}