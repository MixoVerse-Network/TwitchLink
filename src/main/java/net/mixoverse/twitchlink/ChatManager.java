package net.mixoverse.twitchlink;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.TwitchChatBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class ChatManager {

    private OAuth2Credential credential;
    private TwitchChat client;
    private Configuration config;

    private HashMap<String, ArrayList<UUID>> subscriptions = new HashMap<>();
    private HashMap<UUID, String> subscriptionsByPlayer = new HashMap<>();

    public ChatManager(Main main) {
        this.config = main.getConfig();
        this.credential = new OAuth2Credential("twitch", config.getString("api-token"));
        this.client = TwitchChatBuilder.builder()
                .withChatAccount(credential)
                .build();

        this.client.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            String channel = event.getChannel().getName();
            if (this.subscriptions.get(channel) == null) {
                return;
            }

            ArrayList<UUID> players = this.subscriptions.get(channel);

            players.forEach(uuid -> {
                ProxiedPlayer player = main.getProxy().getPlayer(uuid);

                if (player == null) {
                    return;
                }

                player.sendMessage(
                        new ComponentBuilder(
                                ChatColor.translateAlternateColorCodes('&',
                                        config.getString("messages.message")
                                                .replaceAll("%name%", event.getUser().getName())
                                                .replaceAll("%msg%", event.getMessage())
                                )).create());

                
            });

        });

    }
    
    // 0: success
    public int subscribe(String channel, ProxiedPlayer player) {

        if (this.subscriptionsByPlayer.containsKey(player.getUniqueId())) {
            unsubscribe(player.getUniqueId());
        }

        if (this.subscriptions.get(channel) == null) {
            this.subscriptions.put(channel, new ArrayList<UUID>());
            client.joinChannel(channel);
        }

        if (!this.subscriptions.get(channel).contains(player.getUniqueId())) {
            this.subscriptions.get(channel).add(player.getUniqueId());
        }

        this.subscriptionsByPlayer.put(player.getUniqueId(), channel);

        return 0;

    }
    
    // 0: success
    // 1: not subscribed
    public int unsubscribe(UUID uuid) {
        if (this.subscriptionsByPlayer.get(uuid) == null) {
            return 1;
        }
        String channel = this.subscriptionsByPlayer.get(uuid);
        this.subscriptions.get(channel).remove(uuid);
        this.subscriptionsByPlayer.remove(uuid);

        if (this.subscriptions.get(channel).size() < 1) {
            client.leaveChannel(channel);
            this.subscriptions.remove(channel);
        }

        return 0;
    }
    
}
