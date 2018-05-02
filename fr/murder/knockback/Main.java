package fr.murder.knockback;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private static Main instance;
	
	private String craftBukkitVersion;
	private double horizontalMultiplier = 1D;
	private double verticalMultiplier = 1D;
	
	@Override
	public void onEnable() {
		instance = this;
		
		getConfig().options().copyDefaults(true);
		getConfig().addDefault("knockback-multiplier.horizontal", 1D);
		getConfig().addDefault("knockback-multiplier.vertical", 1D);
		saveConfig();
		
		this.craftBukkitVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		this.horizontalMultiplier = getConfig().getDouble("knockback-multiplier.horizontal");
		this.verticalMultiplier = getConfig().getDouble("knockback-multiplier.vertical");
		
		Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
		
		getCommand("setknockback").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("knockbackpatch.setknockback")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		
		if (args.length < 2){
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <horizontal multiplier> <vertical multiplier>.");
			return true;
		}
		
		double horizontalMultiplier = NumberUtils.toDouble(args[0], -1D);
		double verticalMultiplier = NumberUtils.toDouble(args[1], -1D);
		
		if (horizontalMultiplier < 0D || verticalMultiplier < 0D) {
			sender.sendMessage(ChatColor.RED + "Invalid horizontal/vertical multiplier!");
			return true;
		}
		
		this.horizontalMultiplier = horizontalMultiplier;
		this.verticalMultiplier = verticalMultiplier;
		
		getConfig().set("knockback-multiplier.horizontal", horizontalMultiplier);
		getConfig().set("knockback-multiplier.vertical", verticalMultiplier);
		saveConfig();
		
		sender.sendMessage(ChatColor.GREEN + "Successfully updated the knockback multipliers!");
		return true;
	}
	
	public static Main getInstance() {
		return instance;
	}

	public String getCraftBukkitVersion() {
		return craftBukkitVersion;
	}
	
	public double getHorizontalMultiplier() {
		return horizontalMultiplier;
	}

	public void setHorizontalMultiplier(double horizontal) {
		this.horizontalMultiplier = horizontal;
	}

	public double getVerticalMultiplier() {
		return verticalMultiplier;
	}

	public void setVerticalMultiplier(double vertical) {
		this.verticalMultiplier = vertical;
	}
	
}
