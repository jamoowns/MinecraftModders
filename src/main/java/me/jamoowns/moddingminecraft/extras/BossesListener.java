package me.jamoowns.moddingminecraft.extras;

import java.util.Random;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.features.Feature;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public class BossesListener implements IGameEventListener {

	private final ModdingMinecraft javaPlugin;

	public BossesListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

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
	}
}
