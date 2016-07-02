package me.MC_Elmo;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class ArrowHit extends JavaPlugin implements Listener
{
	
		private FileConfiguration config;
		private Logger logger;

		public void onEnable()
		{
			config = getConfig();
			logger = getLogger();
			logger.info("Simple plugin created by MC_Elmo");
			if(!(config.getBoolean("enabled")))
			{
				this.getServer().getPluginManager().disablePlugin(this); // If enabled is set to false in config.yml. Disable the plugin.
			}
			this.getServer().getPluginManager().registerEvents(this, this); //register ProjectileHitEvent
		}
		
		@EventHandler
		public void onHit(ProjectileHitEvent e) //called when a projectile hits an object.
		{
			Projectile p = e.getEntity(); // get the projectile for the event;
			if(p instanceof Arrow) // check if it's an arrow
			{
				Arrow arrow = (Arrow) e.getEntity();
				arrow.getWorld().playSound(arrow.getLocation(), Sound.ORB_PICKUP, 10.0F, 5.0F);
				arrow.remove();
			}
			return;
		}
		private Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException
		{
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		String name = "org.bukkit.craftbukkit." + version + "entity." + nmsClassString;
		Class<?> nmsClass = Class.forName(name);
		return nmsClass;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onDamage(EntityDamageByEntityEvent e)
		{
			Entity ent = e.getDamager();

			if(ent instanceof Arrow)
			{
				if(e.getEntity() instanceof Player)
				{
					final Player player = (Player) e.getEntity();
					Bukkit.getScheduler().runTask(this, new BukkitRunnable()
					{
						public void run()
						{
							try
							{
								Class<?> craftPlayerClass = getNMSClass("CraftPlayer");
								Object handle = craftPlayerClass.getMethod("getHandle").invoke(player);
								Object watch = handle.getClass().getMethod("getDataWatcher").invoke(handle);
								Class<?>[] paramTypes = {int.class, Object.class};
								Method setWatch = watch.getClass().getMethod("watch", paramTypes);
								setWatch.invoke(watch, 9, (byte) 0);
								//reflection ^^ to make the plugin work on multiple 1.8 versions.
								//((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
							} catch (IllegalAccessException e1)
							{
								e1.printStackTrace();
							} catch (InvocationTargetException e1)
							{
								e1.printStackTrace();
							} catch (NoSuchMethodException e)
							{
								e.printStackTrace();
							}catch (ClassNotFoundException e)
							{
								e.printStackTrace();
							}

						}
					});
				}
			}
		}


}
