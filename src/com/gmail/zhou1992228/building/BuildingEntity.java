package com.gmail.zhou1992228.building;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class BuildingEntity {
	public BuildingEntity(String owner, Location pos, String type) {
		pos_ = pos;
		owner_ = owner;
		building_type_ = type;
		name_ = owner + " µÄ " + type;
	}
	public BuildingEntity(String owner, Location pos, String type, String name) {
		pos_ = pos;
		owner_ = owner;
		building_type_ = type;
		if (name.isEmpty()) {
			name_ = owner + " µÄ " + type;
		} else {
			name_ = name;
		}
	}
	public BuildingEntity(ConfigurationSection config) {
		building_type_ = config.getString("type");
		owner_ = config.getString("owner");
		pos_ = new Location(Bukkit.getWorld(config.getString("world")),
							config.getInt("x"),
							config.getInt("y"),
							config.getInt("z"));
		input_count_ = config.getInt("input_count");
		output_count_ = config.getInt("output_count");
		time_counter_ = config.getInt("time_counter");
	}
	public void Save(ConfigurationSection config) {
		config.set("type", building_type_);
		config.set("owner", owner_);
		config.set("world", pos_.getWorld().getName());
		config.set("x", pos_.getBlockX());
		config.set("y", pos_.getBlockY());
		config.set("z", pos_.getBlockZ());
		config.set("input_count", input_count_);
		config.set("output_count", output_count_);
		config.set("time_counter", time_counter_);
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
	
	public boolean Collide(Location loc1, Location loc2) {
		int Xa = Math.abs(loc1.getBlockX() - loc2.getBlockX());
		int Ya = Math.abs(loc1.getBlockY() - loc2.getBlockY());
		int Za = Math.abs(loc1.getBlockZ() - loc2.getBlockZ());
		int Xb = BuildingTemplate.building_templates.get(building_type_).getX_size();
		int Yb = BuildingTemplate.building_templates.get(building_type_).getY_size();
		int Zb = BuildingTemplate.building_templates.get(building_type_).getZ_size();
		int Xma = (loc1.getBlockX() + loc2.getBlockX()) / 2;
		int Yma = (loc1.getBlockY() + loc2.getBlockY()) / 2;
		int Zma = (loc1.getBlockZ() + loc2.getBlockZ()) / 2;
		int Xmb = pos_.getBlockX();
		int Ymb = pos_.getBlockY();
		int Zmb = pos_.getBlockZ();
		return Math.abs(Xma - Xmb) <= (Xa + Xb) / 2 &&
			   Math.abs(Yma - Ymb) <= (Ya + Yb) / 2 &&
			   Math.abs(Zma - Zmb) <= (Za + Zb) / 2;
	}
	
	public boolean inBuilding(Location loc) {
		int max_x = pos_.getBlockX()
				  + BuildingTemplate.building_templates.get(building_type_).getX_size() / 2;
		int min_x = pos_.getBlockX()
				  - BuildingTemplate.building_templates.get(building_type_).getX_size() / 2;
		int max_y = pos_.getBlockY()
				  + BuildingTemplate.building_templates.get(building_type_).getY_size() / 2;
		int min_y = pos_.getBlockY()
				  - BuildingTemplate.building_templates.get(building_type_).getY_size() / 2;
		int max_z = pos_.getBlockZ()
				  + BuildingTemplate.building_templates.get(building_type_).getZ_size() / 2;
		int min_z = pos_.getBlockZ()
				  - BuildingTemplate.building_templates.get(building_type_).getZ_size() / 2;
		return (min_x <= loc.getBlockX() && loc.getBlockX() <= max_x) &&
			   (min_y <= loc.getBlockY() && loc.getBlockY() <= max_y) &&
			   (min_z <= loc.getBlockZ() && loc.getBlockZ() <= max_z);
	}
	
	private Location pos_;
	private String building_type_;
	private int time_counter_;
	private String owner_;
	private int input_count_;
	private int output_count_;
	private String name_;
}
