package com.gmail.zhou1992228.building;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.util.Util;

public class BuildingManager {
	public static BuildingManager ins = new BuildingManager();
	public BuildingManager() {
	}
	public void Init(Building plugin) {
		buildings_ = new ArrayList<BuildingEntity>();
		FileConfiguration config = Util.getConfigWithName("template.yml");
		for (String building_name : config.getKeys(false)) {
			BuildingTemplate.AddBuildingTemplate(
				building_name,
				config.getConfigurationSection(building_name));
			Building.LOG("���뽨��:" + building_name);
		}
		Load();
	}
	public void Load() {
		FileConfiguration config = Util.getConfigWithName("buildings.yml");
		for (String key : config.getKeys(false)) {
			buildings_.add(new BuildingEntity(config.getConfigurationSection(key)));
		}
	}
	public void Save() {
		Building.LOG("123");
		FileConfiguration config = Util.getConfigWithName("buildings.yml");
		for (int i = 0; i < buildings_.size(); ++i) {
			buildings_.get(i).Save(config.createSection(i + ""));
		}
		Util.SaveConfigToName(config, "buildings.yml");
	}
	
	public void TryAddBuilding(Player p, String building_name) {
		TryAddBuilding(p, building_name, "");
	}
	
	public void TryAddBuilding(Player p, String building_name, String custom_name) {
		BuildingTemplate template = BuildingTemplate.building_templates.get(building_name);
		if (template != null) {
			Location loc = template.Match(p.getLocation());
			if (loc != null) {
				Building.LOG("template loc: " + loc.toString());
				if (CollideWithOtherBuilding(loc, template)) {
					p.sendMessage("�������Ѵ��ڵĽ�����ͻ��");
				} else {
					p.sendMessage(building_name + " ����ɹ���");
					buildings_.add(new BuildingEntity(p.getName(), loc, building_name, custom_name));
				}
			} else {
				Building.LOG("NOT MATCH!");
			}
		}
		// TODO
	}
	public void ValidateBuildings() {
		// TODO
	}
	public BuildingEntity InBuilding(Entity e) {
		return null;
	}
	public void DamageBuilding(Entity e) {
		// TODO
	}
	
	private boolean CollideWithOtherBuilding(Location loc, BuildingTemplate template) {
		for (BuildingEntity building : buildings_) {
			Location loc1 = loc.add(template.getX_size() / 2,
					template.getY_size() / 2,
					template.getZ_size() / 2);
			Location loc2 = loc.add(-(template.getX_size() / 2),
					-(template.getY_size() / 2),
					-(template.getZ_size() / 2));
			Building.LOG("loc1: " + loc1.toString() + "\nloc2: " + loc2.toString());
			if (building.Collide(loc1, loc2)) {
				return true;
			}
		}
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
	
	private List<BuildingEntity> buildings_;
}
