package com.gmail.zhou1992228.building;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class BuildingTemplate {
	public static HashMap<String, BuildingTemplate> building_templates =
			new HashMap<String, BuildingTemplate>();
	public static void AddBuildingTemplate(String name, ConfigurationSection config) {
		building_templates.put(name, new BuildingTemplate(config));
	}
	public BuildingTemplate(ConfigurationSection config) {
		x_size = config.getInt("x_size");
		y_size = config.getInt("y_size");
		z_size = config.getInt("z_size");
		input = config.getString("input", "");
		output = config.getString("output", "");
		interval = config.getInt("interval");
		template_width = config.getInt("width");
		storage_cap = config.getInt("storage");
		reward_message = config.getString("reward_message", "");
		List<String> template = config.getStringList("template");
		template_height = template.size() / template_width;
		List<String> typelist = config.getStringList("typelist");
		Map<String, String> ids = new HashMap<String, String>();
		for (int i = 0; i < typelist.size(); ++i) {
			String[] s = typelist.get(i).split(":");
			ids.put(s[0], s[1]);
		}
		name = config.getString("name");
		template_ids = new int[8][template_width][template_height][template_width];
		for (int l = 0; l < template.size(); ++l) {
			int height = l / template_width;
			int width = l % template_width;
			for (int i = 0; i < template_width; ++i) {
				if (ids.get(template.get(l).charAt(i) + "") == null) {
					template_ids[0][i][height][width] = 0;
				} else {
					template_ids[0][i][height][width] = Integer.parseInt(
						ids.get(template.get(l).charAt(i) + ""));
				}
			}
		}
		for (int y = 0; y < template_height; ++y) {
			for (int x = 0; x < template_width; ++x)
			for (int z = 0; z < template_width; ++z) {
				template_ids[4][x][y][z] = template_ids[0][template_width - x - 1][y][z];
			}
		}
		for (int i = 1; i < 4; ++i) 
		for (int y = 0; y < template_height; ++y) {
			for (int x = 0; x < template_width; ++x)
			for (int z = 0; z < template_width; ++z) {
				template_ids[i][x][y][z] = template_ids[i - 1][template_width - z - 1][y][x];
				template_ids[i + 4][x][y][z] = template_ids[i + 3][template_width - z - 1][y][x];
			}
		}
		/*
		for (int i = 0; i < 8; ++i) {
			for (int y = 0; y < template_height; ++y) {
				String output = "\n";
				for (int x = 0; x < template_width; ++x) {
					for (int z = 0; z < template_width; ++z) {
						output += template_ids[i][x][y][z] + " ";
					}
					output += "\n";
				}
				Building.LOG(output);
			}
		}
		*/
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
	public String getInput() {
		return input;
	}
	public String getRewardMessage() {
		return reward_message;
	}
	public String getOutput() {
		return output;
	}
	public String getName() {
		return name;
	}
	public Location Match(Location loc) {
		int orix = (int) (loc.getX() - template_width / 2);
		int oriy = (int) (loc.getY() - template_height / 2);
		int oriz = (int) (loc.getZ() - template_width / 2);
		
		for (int type = 0; type < 8; ++type) {
			if (MatchType(type, orix, oriy, oriz, loc.getWorld())) {
				return new Location(loc.getWorld(),
									orix + template_width / 2,
									oriy + template_height / 2,
									oriz + template_width / 2);
			}
		}
		
		for (int dx = -3; dx < 3; ++dx)
		for (int dy = -3; dy < 3; ++dy)
		for (int dz = -3; dz < 3; ++dz) {
			int match_x = orix + dx;
			int match_y = oriy + dy;
			int match_z = oriz + dz;
			for (int type = 0; type < 8; ++type) {
				if (MatchType(type, match_x, match_y, match_z, loc.getWorld())) {
					return new Location(loc.getWorld(),
										match_x + template_width / 2,
										match_y + template_height / 2,
										match_z + template_width / 2);
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	private boolean MatchType(int type, int ox, int oy, int oz, World world) {
		for (int i = 0; i < template_width; ++i)
		for (int j = 0; j < template_height; ++j)
		for (int k = 0; k < template_width; ++k) {
			if (template_ids[type][i][j][k] != 0 &&
				world.getBlockAt(ox + i, oy + j, oz + k).getTypeId() !=
				template_ids[type][i][j][k]) {
				return false;
			}
		}
		return true;
	}
	
	private String name;
	private int x_size;
	private int y_size;
	private int z_size;
	private int template_width;
	private int template_height;
	private int interval;
	private String output;
	private String input;
	private int storage_cap;
	private String reward_message;
	public int getStorage_cap() {
		return storage_cap;
	}

	private int[][][][] template_ids;
}
