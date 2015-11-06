package com.gmail.zhou1992228.building;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.util.Util;

public class BuildingManager {
	public static BuildingManager ins = new BuildingManager();
	public BuildingManager() {
		// TODO
	}
	public void Init(Building plugin) {
		buildings_ = new HashMap<String, List<BuildingEntity>>();
		FileConfiguration config = Util.getConfigWithName("template.yml");
		for (String building_name : config.getKeys(false)) {
			BuildingTemplate.AddBuildingTemplate(
				building_name,
				config.getConfigurationSection(building_name));
		}
	}
	public void TryAddBuilding(Player p, String building_name) {
		BuildingTemplate template = BuildingTemplate.building_templates.get(building_name);
		if (template != null) {
			Location loc = template.Match(p.getLocation());
			if (loc != null) {
				Bukkit.getLogger().info("True: loc: " + loc.toString());
			}
		}
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
	
	private boolean CollideWithOtherBuilding() {
		return false;
		// TODO
	}
	
	private boolean RemoveBuilding() {
		return true;
		// TODO
	}
	
	private boolean AddBuilding() {
		return true;
		// TODO
	}
	
	private HashMap<String,List<BuildingEntity>> buildings_;
}
