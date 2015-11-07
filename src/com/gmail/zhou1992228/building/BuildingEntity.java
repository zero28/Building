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
	public void Save() {	
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
	private int value_count_;
	private String owner_;
}
