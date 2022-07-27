package net.mixoverse.twitchlink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.mixoverse.twitchlink.commands.TwitchLinkCommand;

public class Main extends Plugin {

   private Configuration config;

   @Override
   public void onEnable() {

      try {
         makeConfig();
         config = ConfigurationProvider.getProvider(YamlConfiguration.class)
               .load(new File(getDataFolder(), "config.yml"));
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      ProxyServer.getInstance().getPluginManager().registerCommand(this, new TwitchLinkCommand(this));

      this.getLogger().info("TwitchLink Enabled");
   }
   
   public void makeConfig() throws IOException {
      // Create plugin config folder if it doesn't exist
      if (!getDataFolder().exists()) {
         getLogger().info("Created config folder: " + getDataFolder().mkdir());
      }

      File configFile = new File(getDataFolder(), "config.yml");

      // Copy default config if it doesn't exist
      if (!configFile.exists()) {
         FileOutputStream outputStream = new FileOutputStream(configFile); // Throws IOException
         InputStream in = getResourceAsStream("config.yml"); // This file must exist in the jar resources folder
         in.transferTo(outputStream); // Throws IOException
      }
   }

   public Configuration getConfig() {
      return config;
   }

}
