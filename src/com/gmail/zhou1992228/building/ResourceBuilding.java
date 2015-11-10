package com.gmail.zhou1992228.building;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class ResourceBuilding extends BuildingEntity {

	public ResourceBuilding(ConfigurationSection config) {
		super(config);
	}

	public ResourceBuilding(String owner, Location pos, String type, String name) {
		super(owner, pos, type, name);
	}

	public ResourceBuilding(String owner, Location pos, String type) {
		super(owner, pos, type);
	}
}
