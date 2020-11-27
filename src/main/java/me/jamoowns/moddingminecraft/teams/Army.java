package me.jamoowns.moddingminecraft.teams;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Army {
	private final String teamName;
	private final TeamColour teamColour;

	private List<UUID> teamMembers;

	public Army(String aTeamName, TeamColour aTeamColour) {
		teamName = aTeamName;
		teamColour = aTeamColour;
		teamMembers = new ArrayList<>();
	}

	public final String getTeamName() {
		return teamName;
	}

	public final TeamColour getTeamColour() {
		return teamColour;
	}

	public final void add(UUID uuid) {
		teamMembers.add(uuid);
	}

	public final boolean has(UUID uuid) {
		return teamMembers.contains(uuid);
	}
}
