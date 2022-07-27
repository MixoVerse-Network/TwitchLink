package net.mixoverse.twitchlink.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import net.mixoverse.twitchlink.ChatManager;
import net.mixoverse.twitchlink.Main;

public class TwitchLinkCommand extends Command {

    private ChatManager chatManager;
    private Configuration config;

    public TwitchLinkCommand(Main main) {
        super("twitchlink");
        this.chatManager = new ChatManager(main);
        this.config = main.getConfig();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!player.hasPermission("twitchlink.use")) {
            player.sendMessage(new ComponentBuilder(
                    ChatColor.translateAlternateColorCodes('&', config.getString("messages.no-permission"))).create());
            return;
        }

        if (args.length < 1) {
            player.sendMessage(new ComponentBuilder(
                    ChatColor.translateAlternateColorCodes('&', config.getString("messages.usage"))).create());
            return;
        }
        
        if (args[0].toLowerCase().equals("off")) {
            switch (chatManager.unsubscribe(player)) {
                case 0:
                    player.sendMessage(new ComponentBuilder(
                            ChatColor.translateAlternateColorCodes('&', config.getString("messages.disabled")))
                            .create());
                    break;

                case 1:
                    player.sendMessage(new ComponentBuilder(
                            ChatColor.translateAlternateColorCodes('&', config.getString("messages.not-subscribed")))
                            .create());
                    break;

                default:
                    break;
            }

            return;
        }

        switch (chatManager.subscribe(args[0], player)) {
            case 0:
                player.sendMessage(new ComponentBuilder(
                        ChatColor.translateAlternateColorCodes('&', config.getString("messages.enabled")
                            .replaceAll("%channel%", args[0])
                        )).create());
                break;
        
            default:
                break;
        }
        
    }
}
