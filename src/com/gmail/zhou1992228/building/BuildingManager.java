package com.gmail.zhou1992228.building;

import java.util.ArrayList;
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
			buildings_.add(BuildingEntity.createBuildingEntity(
					config.getConfigurationSection(key)));
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
					p.sendMessage("�������Ѵ��ڵĽ�����ͻ��");
				} else {
					if (Util.takeRequires(p, template.getOtherRequire())) {
						p.sendMessage(building_name + " ����ɹ���");
						buildings_.add(BuildingEntity.createBuildingEntity(
								p.getName(), loc, building_name, custom_name));
					} else {
						p.sendMessage("��û���㹻����Ʒ/��Ǯ�������������");
					}
				}
			} else {
				p.sendMessage("�ٿ������ͼ�ɣ�");
			}
		} else {
			p.sendMessage("û���������");
		}
	}
	public void ValidateBuildings() {
		Iterator<BuildingEntity> it = buildings_.iterator();
		while (it.hasNext()) {
			BuildingEntity b = it.next();
			if (!b.Validate()) {
				Util.NotifyIfOnline(b.getOwner(), "��� " + b.getName() + " �ѱ��ƻ�");
				it.remove();
			}
		}
	}
	
	public void DamageBuildings() {
		for (World w : Bukkit.getWorlds()) {
			for (Entity e : w.getEntities()) {
				BuildingEntity building = InBuilding(e);
				if (building != null) {
					building.tryDamage(e);
				}
			}
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
	
	private List<BuildingEntity> buildings_;
}
