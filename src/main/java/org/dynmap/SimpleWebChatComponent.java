package org.dynmap;

import static org.dynmap.JSONUtils.s;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.json.simple.JSONObject;

public class SimpleWebChatComponent extends Component {

    public SimpleWebChatComponent(final DynmapPlugin plugin, final ConfigurationNode configuration) {
        super(plugin, configuration);
        plugin.events.addListener("webchat", new Event.Listener<ChatEvent>() {
            @Override
            public void triggered(ChatEvent t) {
                plugin.getServer().broadcastMessage(plugin.configuration.getString("webprefix", "§2[WEB] ") + t.name + ": " + plugin.configuration.getString("websuffix", "§f") + t.message);
            }
        });
        
        plugin.events.addListener("buildclientconfiguration", new Event.Listener<JSONObject>() {
            @Override
            public void triggered(JSONObject t) {
                s(t, "allowchat", configuration.getBoolean("allowchat", false));
            }
        });
        
        if (configuration.getBoolean("allowchat", false)) {
            PlayerChatListener playerListener = new PlayerChatListener();
            PluginManager pm = plugin.getServer().getPluginManager();
            pm.registerEvent(org.bukkit.event.Event.Type.PLAYER_CHAT, playerListener, org.bukkit.event.Event.Priority.Monitor, plugin);
            pm.registerEvent(org.bukkit.event.Event.Type.PLAYER_LOGIN, playerListener, org.bukkit.event.Event.Priority.Monitor, plugin);
            pm.registerEvent(org.bukkit.event.Event.Type.PLAYER_JOIN, playerListener, org.bukkit.event.Event.Priority.Monitor, plugin);
            pm.registerEvent(org.bukkit.event.Event.Type.PLAYER_QUIT, playerListener, org.bukkit.event.Event.Priority.Monitor, plugin);
        }
    }
    
    protected class PlayerChatListener extends PlayerListener {
        @Override
        public void onPlayerChat(PlayerChatEvent event) {
            if(event.isCancelled()) return;
            plugin.mapManager.pushUpdate(new Client.ChatMessage("player", "", event.getPlayer().getDisplayName(), event.getMessage(), event.getPlayer().getName()));
        }

        @Override
        public void onPlayerJoin(PlayerJoinEvent event) {
            plugin.mapManager.pushUpdate(new Client.PlayerJoinMessage(event.getPlayer().getDisplayName(), event.getPlayer().getName()));
        }

        @Override
        public void onPlayerQuit(PlayerQuitEvent event) {
            plugin.mapManager.pushUpdate(new Client.PlayerQuitMessage(event.getPlayer().getDisplayName(), event.getPlayer().getName()));
        }
    }

}
