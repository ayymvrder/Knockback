package fr.murder.knockback;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public class DamageListener implements Listener {

	private Field fieldPlayerConnection;
	private Method sendPacket;
	private Constructor<?> packetVelocity;

	public DamageListener() {
		try {			
			Class<?> entityPlayerClass = Class.forName("net.minecraft.server." + Main.getInstance().getCraftBukkitVersion() + ".EntityPlayer");
			Class<?> packetVelocityClass = Class.forName("net.minecraft.server." + Main.getInstance().getCraftBukkitVersion() + ".PacketPlayOutEntityVelocity");
			Class<?> playerConnectionClass = Class.forName("net.minecraft.server." + Main.getInstance().getCraftBukkitVersion() + ".PlayerConnection");

			// Get the fields here to improve performance later on			
			this.fieldPlayerConnection = entityPlayerClass.getField("playerConnection");
			this.sendPacket = playerConnectionClass.getMethod("sendPacket", packetVelocityClass.getSuperclass());
			this.packetVelocity = packetVelocityClass.getConstructor(int.class, double.class, double.class, double.class);
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerVelocity(PlayerVelocityEvent event) {
		Player player = event.getPlayer();
		EntityDamageEvent lastDamage = player.getLastDamageCause();

		if (lastDamage == null || !(lastDamage instanceof EntityDamageByEntityEvent)) {
			return;
		}

		// Cancel the vanilla knockback
		if (((EntityDamageByEntityEvent) lastDamage).getDamager() instanceof Player) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		long delay = System.currentTimeMillis();
		if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
			return;
		}

		if (event.isCancelled()) {
			return;
		}

		Player damaged = (Player) event.getEntity();
		Player damager = (Player) event.getDamager();
		
		if (damaged.getNoDamageTicks() > damaged.getMaximumNoDamageTicks() / 2D) {
			return;
		}

		double horizontal = Main.getInstance().getHorizontal();
		double vertical = Main.getInstance().getVertical();
		double sprint = damager.isSprinting() ? 0.4D + Main.getInstance().getSprint() : 0.4D;
		double enchantment = damager.getItemInHand() == null ? 0 : damager.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) * 0.1D / Main.getInstance().getEnchantment();
		double air = ((Entity) damaged).isOnGround() ? 1 : Main.getInstance().getAir();
		
		//Uses the direction instead of the vector between the two players to limit misdirected knockbacks @Murder
		//Vector knockback = damaged.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize();
		Vector knockback = damager.getLocation().getDirection().normalize();
		//Uses kbMultiplier as a factor to limit vector deformation @Murder
		knockback.setX(knockback.getX() * (sprint + enchantment) * horizontal);
		knockback.setY(0.35D * air * vertical);
		knockback.setZ(knockback.getZ() * (sprint + enchantment) * horizontal);
		
		try {
			// Send the velocity packet immediately instead of using setVelocity, which fixes the 'relog bug'
			Object entityPlayer = damaged.getClass().getMethod("getHandle").invoke(damaged);
			Object playerConnection = fieldPlayerConnection.get(entityPlayer);
			Object packet = packetVelocity.newInstance(damaged.getEntityId(), knockback.getX(), knockback.getY(), knockback.getZ());
			sendPacket.invoke(playerConnection, packet);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
			e.printStackTrace();
		}
		/*damaged.setVelocity(knockback.multiply(sprintMultiplier).setY(airMultiplier));*/

		delay = System.currentTimeMillis() - delay;
		if(damager.hasPermission("knockback.output")) {
			damager.sendMessage(ChatColor.GREEN + "[Knockback : Hit Statistics]");
			damager.sendMessage(ChatColor.GREEN + "Vector: " + ChatColor.WHITE + knockback);
			damager.sendMessage(ChatColor.GREEN + "Horizontal: " + ChatColor.WHITE + horizontal);
			damager.sendMessage(ChatColor.GREEN + "Vertical: " + ChatColor.WHITE + vertical);
			damager.sendMessage(ChatColor.GREEN + "Sprint: " + ChatColor.WHITE + sprint);
			damager.sendMessage(ChatColor.GREEN + "Enchantment: " + ChatColor.WHITE + enchantment);
			damager.sendMessage(ChatColor.GREEN + "Air: " + ChatColor.WHITE + air);
			damager.sendMessage(ChatColor.GREEN + "Execution time: " + ChatColor.WHITE + delay + "ms");
		}
	}
}
