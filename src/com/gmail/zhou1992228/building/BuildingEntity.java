package com.gmail.zhou1992228.building;

import org.bukkit.Location;

public class BuildingEntity {
	public BuildingEntity(String owner, Location pos, String type) {
		pos_ = pos;
		owner_ = owner;
		building_type_ = type;
	}
	public Location getPos() {
		return pos_;
	}
	public String getBuilding_type() {
		return building_type_;
	}
	public String getOwner() {
		return owner_;
	}
	public void onUpdate() {
		// TODO
	}
	private Location pos_;
	private String building_type_;
	private int value_count_;
	private String owner_;
}
