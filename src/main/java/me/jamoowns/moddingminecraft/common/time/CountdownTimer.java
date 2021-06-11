package me.jamoowns.moddingminecraft.common.time;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CountdownTimer implements Runnable {

	private JavaPlugin plugin;

	private Integer assignedTaskId;

	private int seconds;

	private int secondsLeft;

	private Consumer<CountdownTimer> everySecond;

	private Runnable beforeTimer;

	private Runnable afterTimer;

	private double delay;

	public CountdownTimer(JavaPlugin plugin, int timerLength, double delay, Runnable beforeTimer, Runnable afterTimer,
			Consumer<CountdownTimer> everySecond) {
		this.plugin = plugin;

		this.seconds = timerLength;
		this.secondsLeft = timerLength;
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
	public final int getSecondsLeft() {
		return secondsLeft;
	}

	/**
	 * Gets the total seconds this timer was set to run for
	 *
	 * @return Total seconds timer should run
	 */
	public final int getTotalSeconds() {
		return seconds;
	}

	public final void kill() {
		if (assignedTaskId != null)
			Bukkit.getScheduler().cancelTask(assignedTaskId);
	}

	/**
	 * Runs the timer once, decrements seconds etc... Really wish we could make it
	 * protected/private so you couldn't access it
	 */
	@Override
	public void run() {
		if (secondsLeft < 1) {
			afterTimer.run();

			if (assignedTaskId != null) {
				Bukkit.getScheduler().cancelTask(assignedTaskId);
				assignedTaskId = null;
			}
			return;
		}

		if (secondsLeft == seconds) {
			beforeTimer.run();
		}

		everySecond.accept(this);
		secondsLeft--;
	}

	/**
	 * Schedules this instance to "run" every second
	 */
	public final void scheduleTimer() {
		// Initialize our assigned task's id, for later use so we can cancel
		assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this,
				(long) (delay * TimeConstants.ONE_SECOND), TimeConstants.ONE_SECOND);
	}

}