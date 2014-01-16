package se.enji;

import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
	Logger log=Logger.getLogger("Minecraft");
	Random random=new Random();
	final double STILL=-0.0784000015258789;
	boolean canJump=true;

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		config=getConfig();
		config.options().copyDefaults(true);
		saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		if (sender instanceof Player) {
			Player p=(Player)sender;
			if (command.equalsIgnoreCase("mush")) {
				int amount=1;
				if (args.length==1) {
					if (isInt(args[0])) amount=Integer.parseInt(args[0]);
					else return false;
				}
				p.getInventory().addItem(new ItemStack(Material.HUGE_MUSHROOM_2, amount));
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntityType()!=EntityType.PLAYER) return;
		Player p=(Player)e.getEntity();
		if (e.getCause()==DamageCause.FALL && goldenBoots(p)) e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
  		Player p=e.getPlayer();
  		if (config.getBoolean("goldenBoots")) if (!goldenBoots(p)) return;
  		World w=p.getWorld();
  		Location loc=p.getLocation();
  		int x=(int)loc.getBlockX();
  		int y=(int)loc.getBlockY();
  		int z=(int)loc.getBlockZ();
  		Material b=w.getBlockAt(x,y-1,z).getType();
  		Material c=w.getBlockAt(x,y-2,z).getType();
  		if (isMushy(b,c)) p.setFallDistance(0);
  		Location p1=e.getFrom();
  		Location p2=e.getTo();
  		int fy=(int)Math.round(p1.getY());
  		int ty=(int)Math.round(p2.getY());
  		double xb=0;
  		double zb=0;
  		double pp=getNode("sidePoof")*1.0D;
  		double pn=getNode("sidePoof")*-1.0D;
  		switch (getDirection(p)) {
  			case 360:xb=pp;break;
  			case 180:xb=pn;break;
  			case 270:zb=pp;break;
  			case 90:zb=pn;break;
  			case 315:xb=pp;zb=pp;break;
  			case 45:xb=pp;zb=pn;break;
  			case 225:xb=pn;zb=pp;break;
  			case 135:xb=pn;zb=pn;break;
  		}
  		Vector v=p.getVelocity();
  		double uppfart=v.getY();
  		if ((ty-fy==1)&&isMushy(b,c)&&(uppfart>STILL)) {
  			p.setVelocity(new Vector(xb,getNode("heightPoof")*1.0D,zb));
  			loc.setX(x);
  		}
  		Material o=w.getBlockAt(x,y+3,z).getType();
  		Material oo=w.getBlockAt(x,y+4,z).getType();
  		if (ty-fy==1&&isMush(o)) {
			int gy=y+4;
  			while (!oo.equals(Material.AIR)) gy++;
  			canJump = false;
  			loc.setY(gy);
  			p.teleport(loc);
  			loc.setX(x);
  			p.setVelocity(new Vector(0,0,0));
  			canJump = true;
  		}
  		Material k = w.getBlockAt(x,y-3,z).getType();
  		if (isMush(b)&&k.equals(Material.AIR)&&p.isSneaking()) {
  			loc.setY(y-3);
  			p.teleport(loc);
  			loc.setX(x);
  		}
	}
	
	private double getNode(String m) {
		return config.getDouble(m);
	}
	
	private boolean isMushy(Material b, Material c) {
		if ((isMush(b)&&isMush(c))||(isMush(b)||isMush(c))) return true;
		return false;
	}
	
	private boolean isMush(Material m) {
		if (m.equals(Material.HUGE_MUSHROOM_1)||m.equals(Material.HUGE_MUSHROOM_2)) return true;
		return false;
	}
	
	private boolean isInt(String s) {
		return s.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+");
	}
	
	private boolean goldenBoots(Player p) {
		ItemStack b = p.getInventory().getBoots();
		if (b==null) return false;
		else {
			if (b.getType().equals(Material.GOLD_BOOTS)) return true;
			return false;
		}
	}
	
	private int getDirection(Player p) {
		float y=p.getLocation().getYaw();
		if (y>=22.5D&&y<67.5D||y<=-292.5D&&y>-337.5D) return 225;
	    if (y>=67.5D&&y<112.5D||y<=-247.5D&&y>-292.5D) return 180;
	    if (y>=112.5D&&y<157.5D||y<=-202.5D&&y>-247.5D) return 135;
	    if (y>=157.5D&&y<202.5D||y<=-157.5D&&y>-202.5D) return 90;
	    if (y>=202.5D&&y<247.5D||y<=-112.5D&&y>-157.5D) return 45;
	    if (y>=247.5D&&y<292.5D||y<=-67.5D&&y>-112.5D) return 360;
	    if (y>=292.5D&&y<337.5D||y<=-22.5D&&y>-67.5D) return 315;
	    if (y>=337.5D||y<22.5D||y<=-337.5D||y>-22.5D) return 270;
	    return 0;
	}
}