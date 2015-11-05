package com.gmail.zhou1992228.building;

import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import net.minecraft.server.v1_7_R4.Position;

public class BuildingTemplate {
	public static HashMap<String, BuildingTemplate> building_templates =
			new HashMap<String, BuildingTemplate>();
	public BuildingTemplate(FileConfiguration config) {
		x_size = config.getInt("x_size");
		y_size = config.getInt("y_size");
		z_size = config.getInt("z_size");
		reward = config.getString("reward");
		interval = config.getInt("interval");
		template_width = config.getInt("width");
		template_length = config.getInt("length");
		List<String> template = config.getStringList("template");
		template_height = template.size() / template_width;
		List<String> typelist = config.getStringList("typelist");
		name = config.getString("name");
		building_templates.put(name, this);
		template_ids = new int[template_length][template_height][template_width];
		for (int l = 0; l < template.size(); ++l) {
			
		}
	}
	public int getX_size() {
		return x_size;
	}
	public int getY_size() {
		return y_size;
	}
	public int getZ_size() {
		return z_size;
	}
	public int getInterval() {
		return interval;
	}
	public String getReward() {
		return reward;
	}
	public String getName() {
		return name;
	}
	public Position Match(Position ori_pos) {
		return null;
		// TODO
	}
	private String name;
	private int x_size;
	private int y_size;
	private int z_size;
	private int template_width;
	private int template_length;
	private int template_height;
	private int interval;
	private String reward;
	@SuppressWarnings("unused")
	private int[][][] template_ids;
}
