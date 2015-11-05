package com.gmail.zhou1992228.building;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BuildingManager {
	public static BuildingManager ins = new BuildingManager();
	public BuildingManager() {
		// TODO
	}
	public void Init(Building plugin) {
		buildings_ = new HashMap<String, List<BuildingEntity>>();
	}
	public void TryAddBuilding(Player p, String building_name) {
		// TODO
	}
	public void ValidateBuildings() {
		// TODO
	}
	public BuildingEntity InBuilding(Entity e) {
		return null;
		// TODO
	}
	public void DamageBuilding(Entity e) {
		// TODO
	}
	public void Save() {
		// TODO
	}
	
	@SuppressWarnings("unused")
	private boolean CollideWithOtherBuilding() {
		return true;
		// TODO
	}
	
	@SuppressWarnings("unused")
	private boolean RemoveBuilding() {
		return true;
		// TODO
	}
	
	@SuppressWarnings("unused")
	private boolean AddBuilding() {
		return true;
		// TODO
	}
	
	@SuppressWarnings("unused")
	private HashMap<String,List<BuildingEntity>> buildings_;
}
