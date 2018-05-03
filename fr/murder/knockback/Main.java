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
	private double horizontal;
	private double vertical;
	private double sprint;
	private double air;
	private double enchantment;
	
	@Override
	public void onEnable() {
		instance = this;
		
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		
		this.craftBukkitVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		this.horizontal = getConfig().getDouble("multiplier.horizontal");
		this.vertical = getConfig().getDouble("multiplier.vertical");
		this.sprint = getConfig().getDouble("multiplier.sprint");
		this.air = getConfig().getDouble("multiplier.air");
		this.enchantment = getConfig().getDouble("multiplier.enchantment");
		
		Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
		
		getCommand("setknockback").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("knockback.set")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		
		if (args.length < 5){
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <horizontal> <vertical> <sprint> <air> <enchantment>.");
			return true;
		}
		
		double horizontal = NumberUtils.toDouble(args[0], -1D);
		double vertical = NumberUtils.toDouble(args[1], -1D);
		double sprint = NumberUtils.toDouble(args[2], -0.3D);
		double air = NumberUtils.toDouble(args[3], -0.5D);
		double enchantment = NumberUtils.toDouble(args[4], -0.1D);
		
		if (horizontal < 0D || vertical < 0D || sprint < 0D || air < 0D || enchantment < 0D) {
			sender.sendMessage(ChatColor.RED + "Invalid multipliers!");
			return true;
		}
		
		this.horizontal = horizontal;
		this.vertical = vertical;
		this.sprint = sprint;
		this.air = air;
		this.enchantment = enchantment;
		
		getConfig().set("multiplier.horizontal", horizontal);
		getConfig().set("multiplier.vertical", vertical);
		getConfig().set("multiplier.sprint", sprint);
		getConfig().set("multiplier.air", air);
		getConfig().set("multiplier.enchantment", enchantment);
		saveConfig();
		
		sender.sendMessage(ChatColor.GREEN + "Successfully updated multipliers!");
		return true;
	}
	
	public static Main getInstance() {
		return instance;
	}

	public String getCraftBukkitVersion() {
		return craftBukkitVersion;
	}
	
	public double getHorizontal() {
		return horizontal;
	}

	public void setHorizontal(double horizontal) {
		this.horizontal = horizontal;
	}

	public double getVertical() {
		return vertical;
	}

	public void setVertical(double vertical) {
		this.vertical = vertical;
	}
	
	public double getSprint() {
		return sprint;
	}

	public void setSprint(double sprint) {
		this.sprint = sprint;
	}
	
	public double getAir() {
		return air;
	}

	public void setAir(double air) {
		this.air = air;
	}
	
	public double getEnchantment() {
		return enchantment;
	}

	public void setEnchantment(double enchantment) {
		this.enchantment = enchantment;
	}
	
}
