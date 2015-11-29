package com.gmail.zhou1992228.building;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.util.Util;

public class BuildingManager {
	public static BuildingManager ins = new BuildingManager();
	public BuildingManager() { }
	public void Init(Building plugin) {
		buildings_ = new ArrayList<BuildingEntity>();
		FileConfiguration config = Util.getConfigWithName("template.yml");
		for (String building_name : config.getConfigurationSection("buildings").getKeys(false)) {
			try {
				BuildingTemplate.AddBuildingTemplate(
						building_name,
						config.getConfigurationSection("buildings." + building_name));
				Building.LOG("载入建筑:" + building_name);
			} catch (Exception e) {
				Building.LOG("ERROR: 载入建筑:" + building_name + " 时错误");
			}
		}
		List<String> whitelist_id = config.getStringList("whitelist_id");
		for (String s : whitelist_id) {
			BuildingTemplate.whitelist_id.add(Integer.parseInt(s));
		}
		Load();
	}
	public void Load() {
		FileConfiguration config = Util.getConfigWithName("buildings.yml");
		for (String key : config.getKeys(false)) {
			AddBuilding(BuildingEntity.createBuildingEntity(
					config.getConfigurationSection(key)));
		}
	}
	
	public BuildingEntity getNearBuilding(Entity e) {
		return getBuildingWithLocation(e.getLocation());
	}
	
	public BuildingEntity getBuildingWithLocation(Location loc) {
		for (BuildingEntity building : buildings_) {
			if (building.inBuilding(loc)) {
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
	public void Backup() {
		FileConfiguration config = Util.getConfigWithName("buildings.yml");
		Util.SaveConfigToName(config,
				"backup",
				"buildings-" + Calendar.getInstance().getTimeInMillis() + ".yml.backup");
	}
	public void Save() {
		FileConfiguration config = Util.getConfigWithName("empty.yml");
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
						AddBuilding(BuildingEntity.createBuildingEntity(
								p.getName(), loc, building_name, custom_name));
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
	
	public void DamageBuildings() {
		for (World w : Bukkit.getWorlds()) {
			for (Entity e : w.getEntities()) {
				BuildingEntity building = InBuilding(e);
				if (building != null) {
					building.onDamage(e);
				}
			}
		}
	}
	
	public void tryAttack() {
		for (BuildingEntity e : buildings_) {
			e.TryAttack();
		}
	}
	
	public BuildingEntity InBuilding(Entity e) {
		for (BuildingEntity b : buildings_) {
			if (b.inBuilding(e.getLocation())) {
				return b;
			}
		}
		return null;
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
	}
	
	private void AddBuilding(BuildingEntity e) {
		for (BuildingEntity ori : buildings_) {
			ori.AddIfInRange(e);
			e.AddIfInRange(ori);
		}
		buildings_.add(e);
	}
	
	private List<BuildingEntity> buildings_;
}
