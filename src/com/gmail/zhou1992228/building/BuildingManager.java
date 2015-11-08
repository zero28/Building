package com.gmail.zhou1992228.building;

import java.util.ArrayList;
import java.util.Iterator;
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
			Building.LOG("载入建筑:" + building_name);
		}
		Load();
	}
	public void Load() {
		FileConfiguration config = Util.getConfigWithName("buildings.yml");
		for (String key : config.getKeys(false)) {
			buildings_.add(new BuildingEntity(config.getConfigurationSection(key)));
		}
	}
	
	public BuildingEntity getNearBuilding(Entity e) {
		for (BuildingEntity building : buildings_) {
			if (building.inBuilding(e.getLocation())) {
				return building;
			}
		}
		return null;
	}
	
	public void onUpdate() {
		for (BuildingEntity building : buildings_) {
			building.onUpdate();
		}
	}
	public void Save() {
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
				if (CollideWithOtherBuilding(loc, template)) {
					p.sendMessage("建筑与已存在的建筑冲突！");
				} else {
					if (Util.takeRequires(p, template.getOtherRequire())) {
						p.sendMessage(building_name + " 建设成功！");
						buildings_.add(new BuildingEntity(p.getName(), loc, building_name, custom_name));
					} else {
						p.sendMessage("你没有足够的物品/金钱来建设这个建筑");
					}
				}
			} else {
				p.sendMessage("再看看设计图吧？");
			}
		} else {
			p.sendMessage("没有这个建筑");
		}
	}
	public void ValidateBuildings() {
		Iterator<BuildingEntity> it = buildings_.iterator();
		while (it.hasNext()) {
			BuildingEntity b = it.next();
			if (!b.Validate()) {
				Util.NotifyIfOnline(b.getOwner(), "你的 " + b.getName() + " 已被破坏");
				it.remove();
			}
		}
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
	
	private List<BuildingEntity> buildings_;
}
