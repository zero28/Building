package com.gmail.zhou1992228.building.friend;

import java.util.ArrayList;
import java.util.Calendar;
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
	public void Backup() {
		FileConfiguration config = Util.getConfigWithName("friends.yml");
		Util.SaveConfigToName(config,
				"backup",
				"friends-" + Calendar.getInstance().getTimeInMillis() + ".yml.backup");
	}
	public void Save() {
		FileConfiguration config = Util.getConfigWithName("empty.yml");
		if (relations.size() == 0) { return; }
		for (String name : relations.keySet()) {
			Set<String> friends = relations.get(name);
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(friends);
			config.set(name, list);
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
	private HashMap<String, HashSet<String>> relations = new HashMap<String, HashSet<String>>();
	public String getList(String name) {
		if (relations.get(name.toLowerCase()) == null) { return ""; }
		StringBuffer s = new StringBuffer();
		for (String ss : relations.get(name.toLowerCase())) {
			s.append("\n");
			s.append(ss);
		}
		return s.toString();
	}
}
