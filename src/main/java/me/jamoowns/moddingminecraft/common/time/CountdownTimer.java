package me.jamoowns.moddingminecraft.common.time;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CountdownTimer implements Runnable {

	// Main class for bukkit scheduling
	private JavaPlugin plugin;

	// Our scheduled task's assigned id, needed for canceling
	private Integer assignedTaskId;

	// Seconds and shiz
	private int seconds;
	private int secondsLeft;

	// Actions to perform while counting down, before and after
	private Consumer<CountdownTimer> everySecond;
	private Runnable beforeTimer;
	private Runnable afterTimer;

	private int delay;

	// Construct a timer, you could create multiple so for example if
	// you do not want these "actions"
	public CountdownTimer(JavaPlugin plugin, int seconds, int delay, Runnable beforeTimer, Runnable afterTimer,
			Consumer<CountdownTimer> everySecond) {
		// Initializing fields
		this.plugin = plugin;

		this.seconds = seconds;
		this.secondsLeft = seconds;
		this.delay = delay;

		this.beforeTimer = beforeTimer;
		this.afterTimer = afterTimer;
		this.everySecond = everySecond;
	}

	/**
	 * Gets the seconds left this timer should run
	 *
	 * @return Seconds left timer should run
	 */
	public int getSecondsLeft() {
		return secondsLeft;
	}

	/**
	 * Gets the total seconds this timer was set to run for
	 *
	 * @return Total seconds timer should run
	 */
	public int getTotalSeconds() {
		return seconds;
	}

	public void kill() {
		if (assignedTaskId != null)
			Bukkit.getScheduler().cancelTask(assignedTaskId);
	}

	/**
	 * Runs the timer once, decrements seconds etc... Really wish we could make it
	 * protected/private so you couldn't access it
	 */
	@Override
	public void run() {
		// Is the timer up?
		if (secondsLeft < 1) {
			// Do what was supposed to happen after the timer
			afterTimer.run();

			// Cancel timer
			if (assignedTaskId != null)
				Bukkit.getScheduler().cancelTask(assignedTaskId);
			return;
		}

		// Are we just starting?
		if (secondsLeft == seconds)
			beforeTimer.run();

		// Do what's supposed to happen every second
		everySecond.accept(this);

		secondsLeft--;
	}

	/**
	 * Schedules this instance to "run" every second
	 */
	public void scheduleTimer() {
		// Initialize our assigned task's id, for later use so we can cancel
		assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, delay * TimeConstants.ONE_SECOND,
				TimeConstants.ONE_SECOND);
	}

}