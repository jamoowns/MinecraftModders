package me.jamoowns.moddingminecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Husk;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.jamoowns.moddingminecraft.extras.Spells;
import me.jamoowns.moddingminecraft.features.Feature;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;
import me.jamoowns.moddingminecraft.roominating.SimpleChunks;

public class MabListener implements IGameEventListener {

	private final ModdingMinecraft javaPlugin;

	private final Random RANDOM;
	private final SimpleChunks simpleChunks;

	boolean mabmoSet;
	Player mabmo;

	boolean whoCares = false;

	public MabListener(ModdingMinecraft aJavaPlugin) {
		RANDOM = new Random();
		javaPlugin = aJavaPlugin;
		simpleChunks = new SimpleChunks();
	}

	@Override
	public final void cleanup() {
		/* Empty. */
	}

	@EventHandler
	public final void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getType().equals(Material.GRASS_BLOCK)) {
			// simpleChunks.createChunk(1, event.getBlock().getLocation());
		}
	}

	@EventHandler
	public final void onEntitySpawnEvent(EntitySpawnEvent event) {
		// TODO: Make lantern a custom item and only remove that
		if (event.getEntity().getName().contains("Lantern")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onFallingBlockLand(final EntityChangeBlockEvent event) {
		if (whoCares && event.getEntity() instanceof FallingBlock) {
			FallingBlock fallingBlock = (FallingBlock) event.getEntity();

			if (event.getBlock().getType() == Material.AIR && fallingBlock.isOnGround()) {
				new BukkitRunnable() {
					@Override
					public void run() {
						event.getBlock().setType(Material.SAND);
					}
				}.runTaskLater(javaPlugin, 1L);
			}
		}
	}

	@EventHandler
	public final void onPlayerDeath(PlayerDeathEvent event) {
		if (javaPlugin.featureTracker().isFeatureActive(Feature.IRON_GOLEM)) {
			Player playerEnt = event.getEntity();
			Random r = new Random();
			int low = 0;
			int high = 3;
			int result = r.nextInt(high - low) + low;
			for (int i = 0; i < result; i++) {
				event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), IronGolem.class);
				if (playerEnt.getKiller() instanceof Player) {
					if (playerEnt != playerEnt.getKiller())
						event.getEntity().getLocation().getWorld().spawnEntity(playerEnt.getKiller().getLocation(),
								EntityType.FIREWORK);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public final void onPlayerInteractEvent(PlayerItemConsumeEvent e) {
		// get all the relative values for comparation
		final Player p = e.getPlayer();
		if (e.getItem().getType().equals(Material.SPIDER_EYE)) {
			// schedule a task to see if they have eaten the cookie(maybe the time could be
			// a little faster idk)
			Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
				@Override
				public void run() {
					p.setFallDistance(-20);
					p.teleport(p.getLocation().add(0, 20, 0));
				}
			}, 100L);

		}
		Location playerLoc = p.getLocation();
		Location playerLoc2 = p.getLocation();
		playerLoc2.setY(900);
		if (playerLoc.add(0, 2, 0).getBlock().getType().equals(Material.AIR)) {
			if (e.getItem().getType().equals(Material.COOKED_SALMON)) {
				Husk deadPlayer = p.getLocation().getWorld().spawn(p.getLocation(), Husk.class);
				deadPlayer.getEquipment().setArmorContents(p.getEquipment().getArmorContents());
				deadPlayer.getEquipment().setItemInMainHand(p.getEquipment().getItemInMainHand());
				deadPlayer.getEquipment().setItemInOffHand(p.getEquipment().getItemInOffHand());
				ItemStack is = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
				deadPlayer.getEquipment().setHelmet(is);

				p.teleport(playerLoc2);
				Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {

					@Override
					public void run() {
						p.setFallDistance(0);
						p.teleport(playerLoc.add(0, -2, 0));
					}

				}, 230);
			}
		}
	}

	@EventHandler
	public final void onPlayerMoveEvent(PlayerMoveEvent event) {

		if (javaPlugin.featureTracker().isFeatureActive(Feature.WINFRED)) {
			for (Entity ent : event.getPlayer().getNearbyEntities(5.0D, 4.0D, 5.0D)) {
				if (ent instanceof Witch) {
					if (ent.getName().contains("Winfred")) {
						event.getPlayer().sendMessage("'Get Back'-" + ent.getName());
						event.getPlayer().getWorld().strikeLightningEffect(event.getPlayer().getLocation());
						event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1));
						event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 180, 1));
						Random r = new Random();
						int low = 1;
						int high = 19;
						int resulty = r.nextInt(high - low) + low;

						int resultx = r.nextInt(high - low) + low;

						event.getPlayer().teleport(ent.getLocation().add(resultx - 9, 5, resulty - 9));

						if (((Witch) ent).getHealth() < ((Witch) ent).getAttribute(Attribute.GENERIC_MAX_HEALTH)
								.getDefaultValue() * .75) {
							Spells.switchAllPlayers(event.getPlayer().getWorld());
						}
					}
				}
			}
		}
		if (whoCares) {
			BlockFace facing = event.getPlayer().getFacing();
			int x = 0;
			int z = 0;
			if (facing == BlockFace.SOUTH) {
				x = 0;
				z = -1;
			} else if (facing == BlockFace.NORTH) {
				x = 0;
				z = 1;
			} else if (facing == BlockFace.EAST) {
				x = -1;
				z = 0;
			} else if (facing == BlockFace.WEST) {
				x = 1;
				z = 0;
			}
			Location belowBehindPlayer = event.getPlayer().getLocation().add(x, -1, z);

			if (!belowBehindPlayer.getBlock().isEmpty() && !belowBehindPlayer.getBlock().isLiquid()) {

				Block block = event.getPlayer().getWorld().getBlockAt(belowBehindPlayer);
				BlockData blockData = block.getBlockData();
				block.setType(Material.AIR);
				FallingBlock fb = block.getLocation().getWorld().spawnFallingBlock(block.getLocation().add(0.5, 0, 0.5),
						blockData);
				fb.setVelocity(new Vector(0, 1, 0));
				fb.setGravity(false);
				Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {

					@Override
					public void run() {

						fb.setGravity(true);
						Random r = new Random();
						int low = -50;
						int high = 50;
						double x = (r.nextInt(high - low) + low) / 10;
						double z = (r.nextInt(high - low) + low) / 10;
						fb.setVelocity(new Vector(x / 10, 0, z / 10));
					}
				}, 10);

			}
		}

	}

	@EventHandler
	public final void onThunderChange(ThunderChangeEvent event) {
		if (javaPlugin.featureTracker().isFeatureActive(Feature.STABLE_WEATHER)) {
			if (event.toThunderState()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public final void onWeatherChange(WeatherChangeEvent event) {
		if (javaPlugin.featureTracker().isFeatureActive(Feature.STABLE_WEATHER)) {
			if (event.toWeatherState()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public final void playerChatEvent(PlayerChatEvent event) {
		if (event.getMessage().contains("Winfred the Weak")) {
			Witch witch = event.getPlayer().getLocation().getWorld().spawn(event.getPlayer().getLocation(),
					Witch.class);
			witch.getWorld().strikeLightningEffect(witch.getLocation());
			witch.setCustomName("Winfred the Witch");
			witch.setFallDistance(-400);
			for (int i = 0; i < 3; i++) {
				witch.getLocation().getWorld().spawn(witch.getLocation().add(0, 1, 0), Bat.class);
			}
			for (Entity players : witch.getNearbyEntities(20.0D, 20.0D, 20.0D)) {
				if (players instanceof Player) {
					players.sendMessage("'Get away from me'-" + witch.getName());
				}
			}
		}
		if (event.getMessage().contains("Switch")) {
			Spells.switchAllPlayers(event.getPlayer().getWorld());

		}

		if (event.getMessage().contains("SwapNearMe")) {
			Spells.switchAllPlayersInAnArea(event.getPlayer().getLocation(), 20, 5, 20);
		}
		if (event.getMessage().contains("Ok let's do this")) {
			getReady(event.getPlayer().getLocation().getWorld());
		}
		if (event.getMessage().contains("Iammabmo")) {
			mabmo = event.getPlayer();
			mabmoSet = true;
		}
		if (event.getMessage().contains("Iamnotmabmo")) {
			mabmoSet = false;
		}
	}

	private void getReady(World world) {
		List<Player> PlayerArr = new ArrayList<Player>();

		for (Player players : world.getPlayers()) {
			PlayerArr.add(players);
		}
		sendMabmoMsg("" + PlayerArr.size());
		if (PlayerArr.size() > 0) {
			for (Player player : PlayerArr) {
				player.sendTitle("Get Ready", "", 40, 40, 40);
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 255));
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
			@Override
			public void run() {
				if (PlayerArr.size() > 0) {
					for (Player player : PlayerArr) {
						player.sendTitle("Get Set", "", 40, 40, 40);
					}
				}
			}
		}, 40);
		Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
			@Override
			public void run() {
				if (PlayerArr.size() > 0) {
					for (Player player : PlayerArr) {
						player.sendTitle("Go!", "", 40, 40, 40);
					}
				}
			}
		}, 80);
		Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
			@Override
			public void run() {
				if (PlayerArr.size() > 0) {
					for (Player player : PlayerArr) {
						player.sendTitle("", "", 40, 40, 40);
					}
				}
			}
		}, 120);
		Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
			@Override
			public void run() {
				if (PlayerArr.size() > 0) {

					Location loc = PlayerArr.get(0).getLocation();
					Random r = new Random();
					int low = 3000;
					int high = 5000;
					int count = 100;
					while (count > 0) {
						Location newLoc = loc;
						newLoc.add(r.nextInt(high - low) + low, 0, r.nextInt(high - low) + low);
						if (!newLoc.getBlock().getBiome().toString().contains("OCEAN")) {
							loc = newLoc;
							count = 0;
						}
						count--;
					}
					loc.add(0, 80, 0);
					for (Player player : PlayerArr) {
						player.setVelocity(new Vector(player.getVelocity().getX(), 1, player.getVelocity().getZ()));
						player.teleport(loc);
						player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 255));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 180, 1));

					}
				}
			}
		}, 120);

	}

	private void sendMabmoMsg(String str) {
		if (mabmoSet) {
			if (mabmo.isOnline()) {
				mabmo.sendMessage(str);
			}
		}
	}

}
