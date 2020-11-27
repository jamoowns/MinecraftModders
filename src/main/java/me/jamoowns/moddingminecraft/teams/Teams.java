package me.jamoowns.moddingminecraft.teams;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class Teams {

	private List<TeamColour> availableTeamColours;

	private List<Army> armies;

	private final Random RANDOM;

	private final PlayerEventListener playerEventListener;

	public Teams(JavaPlugin javaPlugin) {
		armies = new ArrayList<>();
		RANDOM = new Random();

		availableTeamColours = new ArrayList<>();
		availableTeamColours.add(new TeamColour(Material.BLACK_WOOL, ChatColor.BLACK));
		availableTeamColours.add(new TeamColour(Material.BLUE_WOOL, ChatColor.BLUE));
		availableTeamColours.add(new TeamColour(Material.CYAN_WOOL, ChatColor.AQUA));
		availableTeamColours.add(new TeamColour(Material.GRAY_WOOL, ChatColor.GRAY));
		availableTeamColours.add(new TeamColour(Material.GREEN_WOOL, ChatColor.GREEN));
		availableTeamColours.add(new TeamColour(Material.ORANGE_WOOL, ChatColor.GOLD));
		availableTeamColours.add(new TeamColour(Material.PURPLE_WOOL, ChatColor.LIGHT_PURPLE));
		availableTeamColours.add(new TeamColour(Material.RED_WOOL, ChatColor.RED));
		availableTeamColours.add(new TeamColour(Material.WHITE_WOOL, ChatColor.WHITE));
		availableTeamColours.add(new TeamColour(Material.YELLOW_WOOL, ChatColor.YELLOW));

		for (Player online : Bukkit.getOnlinePlayers()) {
			register(online.getName(), online.getUniqueId());
		}
		playerEventListener = new PlayerEventListener(this);
		javaPlugin.getServer().getPluginManager().registerEvents(playerEventListener, javaPlugin);
	}

	public final void register(String teamName, UUID player) {
		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		if (!teamExists(teamName)) {
			Army army = new Army(teamName, availableTeamColours.remove(RANDOM.nextInt(availableTeamColours.size())));
			armies.add(army);

			System.out.println(board.getTeams().stream().map(t -> t.getName()).collect(Collectors.toList()));
			if (board.getTeam(teamName) == null) {
				Team team = board.registerNewTeam(army.getTeamName());
				team.setColor(army.getTeamColour().getColour());
			}
		}
		Army team = getTeam(teamName);
		team.add(player);
	}

	private final boolean teamExists(String teamName) {
		return armies.stream().anyMatch(t -> t.getTeamName().equalsIgnoreCase(teamName));
	}

	private final Army getTeam(String teamName) {
		return armies.stream().filter(t -> t.getTeamName().equalsIgnoreCase(teamName)).findFirst().get();
	}

	private final Army getTeam(UUID entity) {
		return armies.stream().filter(t -> t.has(entity)).findFirst().get();
	}

	public final String getTeamName(UUID entity) {
		return getTeam(entity).getTeamName();
	}

	public final void register(UUID owner, Mob mob) {
		Player player = Bukkit.getPlayer(owner);
		mob.setCustomName(player.getName() + "'s minion");
		mob.setCustomNameVisible(true);
		mob.setGlowing(true);
		Army army = getTeam(owner);
		Material headType = army.getTeamColour().getHead();
		mob.getEquipment().setHelmet(new ItemStack(headType));

		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team = board.getTeam(army.getTeamName());
		team.addEntry(mob.getUniqueId().toString());
	}

	private class PlayerEventListener implements Listener {

		private Teams teams;

		PlayerEventListener(Teams aTeams) {
			teams = aTeams;
		}

		@EventHandler
		public void onPlayerJoin(PlayerJoinEvent event) {
			Player player = event.getPlayer();
			teams.register(player.getName(), player.getUniqueId());
		}
	}
}
