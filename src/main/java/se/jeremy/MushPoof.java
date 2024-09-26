package se.jeremy;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class MushPoof extends JavaPlugin implements Listener {
	FileConfiguration config;
	final double STILL = -0.0784000015258789;
	boolean canJump = true;

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		config = getConfig();
		config.options().copyDefaults(true);

		saveConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player p = (Player) sender;
		int amount = args.length == 1 && isInt(args[0]) ? Integer.parseInt(args[0]) : 64;

		p.getInventory().addItem(new ItemStack(Material.RED_MUSHROOM_BLOCK, amount));

		return true;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!event.getEntityType().equals(EntityType.PLAYER))
			return;

		Player p = (Player) event.getEntity();

		if (config.getBoolean("goldenBoots") && !goldenBoots(p))
			return;

		if (event.getCause().equals(DamageCause.FALL) && goldenBoots(p))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		if (config.getBoolean("goldenBoots") && !goldenBoots(player))
			return;

		World w = player.getWorld();
		Location playerLocation = player.getLocation();
		int x = playerLocation.getBlockX(), y = playerLocation.getBlockY(), z = playerLocation.getBlockZ();
		Material blockBeneath = w.getBlockAt(x, y - 1, z).getType(), blockAbove = w.getBlockAt(x, y + 3, z).getType();

		if (isMushroomBlock(blockBeneath)) {
			player.setFallDistance(0);

			Vector dir = player.getLocation().getDirection();
			double sp = config.getDouble("sidePoof"), hp = config.getDouble("heightPoof") * 1.0D;

			if (player.isSprinting() && config.getBoolean("fasterWhenSprinting")) {
				sp *= 1.5;
				hp *= 1.2;
			}

			dir = dir.multiply(sp);
			dir.setY(hp);

			if (player.getVelocity().getY() > STILL) {
				player.setVelocity(dir);
			}

			Block landingBlockOne = w.getBlockAt(x, y - 3, z);
			Block landingBlockTwo = w.getBlockAt(x, y - 2, z);

			if (player.isSneaking() && landingBlockOne.isPassable() && landingBlockTwo.isPassable()) {
				playerLocation.setY(y - 3);
				player.teleport(playerLocation);
			}
		} else if (isMushroomBlock(blockAbove)) {
			Block landingBlockOne = w.getBlockAt(x, y + 4, z);
			Block landingBlockTwo = w.getBlockAt(x, y + 5, z);

			if (player.getVelocity().getY() > STILL && landingBlockOne.isPassable() && landingBlockTwo.isPassable()) {
				canJump = false;
				playerLocation.setY(y + 4);

				player.teleport(playerLocation);
				player.setVelocity(new Vector(0, 0, 0));

				canJump = true;
			}
		} else
			return;
	}

	private boolean isMushroomBlock(Material m) {
		return m.equals(Material.BROWN_MUSHROOM_BLOCK) || m.equals(Material.RED_MUSHROOM_BLOCK)
				|| m.equals(Material.MUSHROOM_STEM);
	}

	private boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	private boolean goldenBoots(Player p) {
		return (p.getInventory().getBoots() == null) ? false
				: p.getInventory().getBoots().getType().equals(Material.GOLDEN_BOOTS);
	}
}