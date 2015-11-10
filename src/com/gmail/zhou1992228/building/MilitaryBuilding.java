package com.gmail.zhou1992228.building;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MilitaryBuilding extends BuildingEntity {

	public MilitaryBuilding(ConfigurationSection config) {
		super(config);
	}

	public MilitaryBuilding(String owner, Location pos, String type, String name) {
		super(owner, pos, type, name);
	}

	public MilitaryBuilding(String owner, Location pos, String type) {
		super(owner, pos, type);
	}
	
	@Override
	public void onCollect(Player p, int count) {
		// TODO
	}
	@Override
	public void onDamage(Entity entity) {
		// TODO
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
	}
}
