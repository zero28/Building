package com.gmail.zhou1992228.building;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.util.Util;

public class MilitaryBuilding extends BuildingEntity {

	public MilitaryBuilding(ConfigurationSection config) {
		super(config);
		rewards = config.getStringList("reward_list");
	}

	public MilitaryBuilding(String owner, Location pos, String type, String name) {
		super(owner, pos, type, name);
	}

	public MilitaryBuilding(String owner, Location pos, String type) {
		super(owner, pos, type);
	}
	
	@Override
	public void Save(ConfigurationSection config) {
		super.Save(config);
		config.set("reward_list", rewards);
	}
	
	@Override
	public void onCollect(Player p, int count) {
		if (rewards.size() == 0) {
			p.sendMessage("并没有战利品");
			return;
		}
		Util.giveItems(p, rewards.get(0));
		rewards.remove(0);
	}
	
	@Override
	public void onDamage(Entity entity) {
		// TODO
	}
	
	public void addResource(String s) {
		rewards.add(s);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
	}
	
	private List<String> rewards = new ArrayList<String>();
}
