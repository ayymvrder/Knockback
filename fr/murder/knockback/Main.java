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
	private double enchantmentNerf = 1;
	
	@Override
	public void onEnable() {
		instance = this;
		
		getConfig().options().copyDefaults(true);
		getConfig().addDefault("multiplier.horizontal", 1D);
		getConfig().addDefault("multiplier.vertical", 1D);
		getConfig().addDefault("multiplier.enchantment-nerf", 1);
		saveConfig();
		
		this.craftBukkitVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		this.horizontalMultiplier = getConfig().getDouble("multiplier.horizontal");
		this.verticalMultiplier = getConfig().getDouble("multiplier.vertical");
		this.enchantmentNerf = getConfig().getDouble("multiplier.enchantment-nerf");
		
		Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
		
		getCommand("setknockback").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("knockback.set")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		
		if (args.length < 3){
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <horizontal multiplier> <vertical multiplier> <enchantment nerf>.");
			return true;
		}
		
		double horizontalMultiplier = NumberUtils.toDouble(args[0], -1D);
		double verticalMultiplier = NumberUtils.toDouble(args[1], -1D);
		double enchantmentNerf = NumberUtils.toDouble(args[2]);
		
		if (horizontalMultiplier < 0D || verticalMultiplier < 0D || enchantmentNerf <= 0) {
			sender.sendMessage(ChatColor.RED + "Invalid multipliers!");
			return true;
		}
		
		this.horizontalMultiplier = horizontalMultiplier;
		this.verticalMultiplier = verticalMultiplier;
		this.enchantmentNerf = enchantmentNerf;
		
		getConfig().set("multiplier.horizontal", horizontalMultiplier);
		getConfig().set("multiplier.vertical", verticalMultiplier);
		getConfig().set("multiplier.vertical", enchantmentNerf);
		saveConfig();
		
		sender.sendMessage(ChatColor.GREEN + "Successfully updated the multipliers!");
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
	
	public double getEnchantmentNerf() {
		return enchantmentNerf;
	}

	public void setEnchantmentNerf(double nerf) {
		this.enchantmentNerf = nerf;
	}
	
}
