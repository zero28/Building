package com.gmail.zhou1992228.building.friend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.zhou1992228.building.Building;
import com.gmail.zhou1992228.building.util.Util;

public class Friend {
	public static Friend ins = new Friend();
	public void Init(Building plugin) {
		FileConfiguration config = Util.getConfigWithName("friends.yml");
		for (String from : config.getKeys(false)) {
			List<String> list = config.getStringList(from);
			HashSet<String> set = new HashSet<String>();
			set.addAll(list);
			relations.put(from, set);
		}
	}
	public void Save() {
		FileConfiguration config = Util.getConfigWithName("empty.yml");
		for (String name : relations.keySet()) {
			Set<String> friends = relations.get(name);
			config.set(name, friends);
		}
		Util.SaveConfigToName(config, "friends.yml");
	}
	
	public void RemoveRelation(String from, String to) {
		Set<String> friends = relations.get(from);
		if (friends != null) {
			friends.remove(to);
		}
	}
	
	public void AddRelation(String from, String to) {
		Set<String> friends = relations.get(from);
		if (friends == null) {
			HashSet<String> new_set = new HashSet<String>();
			new_set.add(to);
			relations.put(from, new_set);
		} else {
			friends.add(to);
		}
	}
	
	public boolean isFriend(String from, String to) {
		Set<String> friends = relations.get(from.toLowerCase());
		if (friends == null) { return false; }
		return friends.contains(to.toLowerCase());
	}
	private HashMap<String, HashSet<String>> relations;
}
