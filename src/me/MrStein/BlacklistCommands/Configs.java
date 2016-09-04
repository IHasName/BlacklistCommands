package me.MrStein.BlacklistCommands;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Configs {
	
	static FileConfiguration blconfig;
	static File blfile;
	static FileConfiguration MainConfig;
	static File MainFile;
	
	public static FileConfiguration getBlacklist(Plugin plugin) {
		if(blconfig == null) {
			reloadBlacklist(plugin);
		}
		return blconfig;
	}
	
	public static void saveBlacklist(Plugin plugin) {
		if(blconfig == null) {
			throw new NullPointerException("Cannot save a Null File!");
		}
		try {
			blconfig.save(blfile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Couldn't save File to " + blfile.getName(), e);
		}
	}
	
	public static void reloadBlacklist(Plugin plugin) {
		blfile = new File(plugin.getDataFolder(), "blacklist.yml");
		if(!blfile.exists()) {
			plugin.saveResource("blacklist.yml", false);
		}
		blconfig = YamlConfiguration.loadConfiguration(blfile);
		InputStream defPlayerData = plugin.getResource("blacklist.yml");
		if(defPlayerData != null) {
			blconfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defPlayerData)));
		}
	}
	
	public static FileConfiguration getConfig(Plugin plugin) {
		if(MainConfig == null) {
			reloadConfig(plugin);
		}
		return MainConfig;
	}
	
	public static void saveConfig(Plugin plugin) {
		if(MainConfig == null) {
			throw new NullPointerException("Cannot save a Null File!");
		}
		YamlConfiguration config = YamlConfiguration.loadConfiguration(MainFile);
		try {
			config.save(MainFile);
		}catch (IOException e){
			plugin.getLogger().log(Level.SEVERE, "Couldn't save File to " + MainFile.getName(), e);
		}
	}
	
	public static void reloadConfig(Plugin plugin) {
		MainFile = new File(plugin.getDataFolder(), "config.yml");
		if(!MainFile.exists()) {
			plugin.saveResource("config.yml", false);
		}
		MainConfig = YamlConfiguration.loadConfiguration(MainFile);
		InputStream defMainFile = plugin.getResource("config.yml");
		if(defMainFile != null) {
			MainConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defMainFile)));
		}
	}
}
