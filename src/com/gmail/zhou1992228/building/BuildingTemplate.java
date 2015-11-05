package com.gmail.zhou1992228.building;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

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
		for (int y = 0; y < template_height; ++y) {
			for (int x = 0; x < template_width; ++x)
			for (int z = 0; z < template_width; ++z) {
				template_ids[1][x][y][z] = template_ids[0][template_width - z - 1][y][x];
				template_ids[2][x][y][z] = template_ids[1][template_width - z - 1][y][x];
				template_ids[3][x][y][z] = template_ids[2][template_width - z - 1][y][x];
				template_ids[4][x][y][z] = template_ids[0][template_width - x - 1][y][z];
				template_ids[5][x][y][z] = template_ids[5][template_width - z - 1][y][x];
				template_ids[6][x][y][z] = template_ids[6][template_width - z - 1][y][x];
				template_ids[7][x][y][z] = template_ids[7][template_width - z - 1][y][x];
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
	private String reward;
	private int[][][][] template_ids;
}
