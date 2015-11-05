package com.gmail.zhou1992228.building;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
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
		List<String> template = config.getStringList("template");
		template_height = template.size() / template_width;
		List<String> typelist = config.getStringList("typelist");
		Map<String, String> ids = new HashMap<String, String>();
		for (int i = 0; i < typelist.size(); ++i) {
			String[] s = typelist.get(i).split(":");
			ids.put(s[0], s[1]);
		}
		name = config.getString("name");
		building_templates.put(name, this);
		template_ids = new int[8][template_width][template_height][template_width];
		for (int l = 0; l < template.size(); ++l) {
			int height = l / template_width;
			int width = l % template_width;
			for (int i = 0; i < template_width; ++i) {
				template_ids[0][i][height][width] = Integer.parseInt(
						ids.get(typelist.get(l).charAt(i) + ""));
			}
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
	@SuppressWarnings("deprecation")
	public Location Match(Location loc) {
		int orix = (int) (loc.getX() - template_width / 2);
		int oriy = (int) (loc.getY() - template_height / 2);
		int oriz = (int) (loc.getZ() - template_width / 2);
		for (int dx = -3; dx < 3; ++dx)
		for (int dy = -3; dy < 3; ++dy)
		for (int dz = -3; dz < 3; ++dz) {
			int match_x = orix + dx;
			int match_y = oriy + dy;
			int match_z = oriz + dz;
			for (int x = 0; x < template_width; ++x)
			for (int y = 0; y < template_height; ++y)
			for (int z = 0; z < template_width; ++z) {
				if (loc.getWorld().getBlockAt(match_x + x, match_y + y, match_z + z).getTypeId() ==
					template_ids[0][x][y][z]) {
					
				}
			}
		}
		return null;
		// TODO
	}
	private String name;
	private int x_size;
	private int y_size;
	private int z_size;
	private int template_width;
	private int template_height;
	private int interval;
	private String reward;
	private int[][][][] template_ids;
}
