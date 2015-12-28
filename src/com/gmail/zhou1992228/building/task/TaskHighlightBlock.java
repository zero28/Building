package com.gmail.zhou1992228.building.task;

import org.bukkit.Location;
import org.bukkit.World;

import com.gmail.zhou1992228.building.Building;
import com.gmail.zhou1992228.building.util.ParticleEffect;

public class TaskHighlightBlock implements Runnable {
	private Location loc;
	private int timer;
	@SuppressWarnings("deprecation")
	public static void New(Location loc, int time) {
		Building.ins.getServer().getScheduler().scheduleAsyncDelayedTask(
				Building.ins, new TaskHighlightBlock(loc, time), 20);
	}
	public TaskHighlightBlock(Location loc, int time) {
		this.loc = loc.clone();
		timer = time;
	}
	@Override
	public void run() {
		this.HighlightBlock(loc);
		if (timer > 0) {
			Building.ins.getServer().getScheduler().scheduleSyncDelayedTask(
				Building.ins, new TaskHighlightBlock(loc, timer - 1), 20);
		}
	}
	private void HighlightBlock(Location loc) {
		double x1 = loc.getX() - 0.6;
		double y1 = loc.getY() - 0.6;
		double z1 = loc.getZ() - 0.6;
		double x2 = loc.getX() + 0.6;
		double y2 = loc.getY() + 0.6;
		double z2 = loc.getZ() + 0.6;
		World w = loc.getWorld();
		for (int i = 0; i < 4; ++i) {
			double tx = x1 + i * 0.3;
			double ty = y1 + i * 0.3;
			double tz = z1 + i * 0.3;
			ParticleEffect.sendToLocation(
					ParticleEffect.FIRE,
					new Location(w, x1, y1, tz),
					0, 0, 0, 0, 2);
			ParticleEffect.sendToLocation(
					ParticleEffect.FIRE,
					new Location(w, x1, y2, tz),
					0, 0, 0, 0, 2);
			ParticleEffect.sendToLocation(
					ParticleEffect.FIRE,
					new Location(w, x2, y1, tz),
					0, 0, 0, 0, 2);
			ParticleEffect.sendToLocation(
					ParticleEffect.FIRE,
					new Location(w, x2, y2, tz),
					0, 0, 0, 0, 2);
			ParticleEffect.sendToLocation(
					ParticleEffect.FIRE,
					new Location(w, x1, ty, z1),
					0, 0, 0, 0, 2);
			ParticleEffect.sendToLocation(
					ParticleEffect.FIRE,
					new Location(w, x2, ty, z1),
					0, 0, 0, 0, 2);
			ParticleEffect.sendToLocation(
					ParticleEffect.FIRE,
					new Location(w, x1, ty, z2),
					0, 0, 0, 0, 2);
			ParticleEffect.sendToLocation(
					ParticleEffect.FIRE,
					new Location(w, x2, ty, z2),
					0, 0, 0, 0, 2);
			ParticleEffect.sendToLocation(
					ParticleEffect.FIRE,
					new Location(w, tx, y1, z1),
					0, 0, 0, 0, 2);
			ParticleEffect.sendToLocation(
					ParticleEffect.FIRE,
					new Location(w, tx, y1, z2),
					0, 0, 0, 0, 2);
			ParticleEffect.sendToLocation(
					ParticleEffect.FIRE,
					new Location(w, tx, y2, z1),
					0, 0, 0, 0, 2);
			ParticleEffect.sendToLocation(
					ParticleEffect.FIRE,
					new Location(w, tx, y2, z2),
					0, 0, 0, 0, 2);
		}
	}
}
