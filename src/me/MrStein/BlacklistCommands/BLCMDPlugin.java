package me.MrStein.BlacklistCommands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import com.google.common.collect.Lists;

public class BLCMDPlugin extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {

	public void onEnable() {
		loadConfigs();
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	public void onDisable() {

	}

	public void loadConfigs() {
		Configs.reloadBlacklist(this);
		Configs.reloadConfig(this);
	}

	public boolean onCommand(CommandSender s, Command cmd, String cmdlabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("blcmd")) {
			if(!s.hasPermission("blacklist.*")) {
				s.sendMessage("You don't have Permission to use this Command");
			}
			if (args.length == 0) {
				s.sendMessage("§cNot enough Arguments\n/blcmd <add, remove, reload> <[command]> <[CustomMessage]>");
				return true;
			}
			if (args[0].equalsIgnoreCase("add")) {
				if(!s.hasPermission("blacklist.add")) {
					s.sendMessage("You don't have Permission to use this Command");
					return true;
				}
				if(args.length <= 1) {
					s.sendMessage("§cNot enough Arguments\n/blcmd add <command> <[CustomMessage]>");
					return true;
				}
				String msg = "";
				if(args.length <= 2) {
					msg += Configs.getConfig(this).getString("defaultmessage");
					s.sendMessage("§7No Message provided. Using defaultmessage instead");
				}else{
					for(int i = 2; i < args.length; i++) {
						msg += args[i] + " ";
					}
				}
				if(Configs.getBlacklist(this).getConfigurationSection("blacklisted").getKeys(false).contains(args[1])) {
					s.sendMessage("§e /" + args[1] + " §7has been edited!");
					s.sendMessage("§7Before: §r" + format(Configs.getBlacklist(this).getString("blacklisted." + args[1])));
					s.sendMessage("§7After: §r" + format(msg));
				}else{
					s.sendMessage("§e /" + args[1] + " §7has been added!");
					s.sendMessage("§7Message: §r" + format(msg));
				}
				Configs.getBlacklist(this).set("blacklisted." + args[1], msg);
				Configs.saveBlacklist(this);
			}
			if(args[0].equalsIgnoreCase("remove")) {
				if(!s.hasPermission("blacklist.remove")) {
					s.sendMessage("You don't have Permission to use this Command");
					return true;
				}
				if(args.length < 1) {
					s.sendMessage("§cNot enough Arguments\n/blcmd remove <command>");
					return true;
				}
				if(Configs.getBlacklist(this).getConfigurationSection("blacklisted").getKeys(false).contains(args[1])) {
					Configs.getBlacklist(this).set("blacklisted." + args[1], null);
//					list.remove(args[1]);
					s.sendMessage("§e /" + args[1] + " §7has been removed!");
					Configs.saveBlacklist(this);
					return true;
				}
				s.sendMessage("§e /" + args[1] + " §7couldn't be found");
			}
			if (args[0].equalsIgnoreCase("get")) {
				if(!s.hasPermission("blacklist.get")) {
					s.sendMessage("You don't have Permission to use this Command");
					return true;
				}
				s.sendMessage("§cThese are the currnetly Blacklisted Commands:");
				for(String key : Configs.getBlacklist(this).getConfigurationSection("blacklisted").getKeys(false)) {
					s.sendMessage("§7- /" + key + " - §r" + format(Configs.getBlacklist(this).getString("blacklisted." + key)));
				}
			}
			if(args[0].equalsIgnoreCase("reload")) {
				if(!s.hasPermission("blacklist.reload")) {
					s.sendMessage("You don't have Permission to use this Command");
				}
				Configs.saveConfig(this);
				Configs.reloadConfig(this);
				Configs.saveBlacklist(this);
				Configs.reloadBlacklist(this);
				s.sendMessage("§7Configs reloaded!");
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void command(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		String[] args = e.getMessage().split(" ");
		for(String list : Configs.getBlacklist(this).getConfigurationSection("blacklisted").getKeys(false)) {
			if (args[0].equalsIgnoreCase("/" + list) && !p.hasPermission("blacklist.bypass")) {
				p.sendMessage(format(Configs.getBlacklist(this).getString("blacklisted." + list)));
				e.setCancelled(true);
				return;
			}
		}
	}
	
	public List<String> onTabComplete(CommandSender s, Command cmd, String cmdlabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("blcmd")) {
			ArrayList<String> a1 = new ArrayList<String>();
			List<String> alist = Lists.newArrayList();
			if(s.hasPermission("blacklist.add")) {
				a1.add("add");
			}
			if(s.hasPermission("blacklist.remove")) {
				a1.add("remove");
			}
			if(s.hasPermission("blacklist.reload")) {
				a1.add("reload");
			}
			if(s.hasPermission("blacklist.get")) {
				a1.add("get");
			}
			if(args.length == 1) {
				for(String a : a1) {
					if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
						alist.add(a);
					}
				}
			}
			return alist;
		}
		return null;
	}
	
	private static String format(String string) {
		string = ChatColor.translateAlternateColorCodes('&', string);
		string = string.replace("{newline}", "\n").replace("ae", "ä").replace("oe", "ö").replace("ue", "ü").replace("[sz]", "ẞ");
		return string;
	}

}
