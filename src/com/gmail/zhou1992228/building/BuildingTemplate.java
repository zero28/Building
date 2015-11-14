package com.gmail.zhou1992228.building;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

public class BuildingTemplate {
	public static HashMap<String, BuildingTemplate> building_templates =
			new HashMap<String, BuildingTemplate>();
	public static HashSet<Integer> whitelist_id = new HashSet<Integer>();
	public static void AddBuildingTemplate(String name, ConfigurationSection config) {
		building_templates.put(name, new BuildingTemplate(config));
	}
	public BuildingTemplate(ConfigurationSection config) {
		template_width = config.getInt("width");
		type = config.getString("type", "");
		max_health = config.getInt("max_health", 1000);
		interval = config.getInt("interval");
		x_size = config.getInt("x_size");
		y_size = config.getInt("y_size");
		z_size = config.getInt("z_size");
		input = config.getString("input", "");
		output_per_resource = config.getInt("output_per_resource");
		output = config.getString("output", "");
		storage_cap = config.getInt("storage");
		reward_message = config.getString("reward_message", "");
		other_require = config.getString("other_require", "");
		rob_pos = config.getInt("rob_pos");
		
		attack_type = config.getString("attack_type");
		attack_x_range = config.getInt("attack_x_range");
		attack_y_range = config.getInt("attack_y_range");
		attack_z_range = config.getInt("attack_z_range");
		attack_rob_pos = config.getInt("attack_rob_pos");
		max_target = config.getInt("max_target", 1);
		attack = config.getInt("attack");
		
		List<String> template = config.getStringList("template");
		List<String> typelist = config.getStringList("typelist");
		template_height = template.size() / template_width;
		
		Map<String, String> ids = new HashMap<String, String>();
		for (int i = 0; i < typelist.size(); ++i) {
			String[] s = typelist.get(i).split(":", 2);
			ids.put(s[0], s[1]);
		}
		name = config.getString("name");
		template_ids = new String[8][template_width][template_height][template_width];
		for (int l = 0; l < template.size(); ++l) {
			int height = l / template_width;
			int width = l % template_width;
			for (int i = 0; i < template_width; ++i) {
				if (ids.get(template.get(l).charAt(i) + "") == null) {
					template_ids[0][i][height][width] = "0";
				} else {
					template_ids[0][i][height][width] = ids.get(template.get(l).charAt(i) + "");
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
	public String getOtherRequire() {
		return other_require;
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
	
	private boolean MatchType(int type, int ox, int oy, int oz, World world) {
		for (int i = 0; i < template_width; ++i)
		for (int j = 0; j < template_height; ++j)
		for (int k = 0; k < template_width; ++k) {
			if (!template_ids[type][i][j][k].equals("0") &&
				!Match(world.getBlockAt(ox + i, oy + j, oz + k),
					   template_ids[type][i][j][k])) {
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	private boolean Match(Block block, String type) {
		String s = "" + block.getTypeId();
		try {
			if (block.getData() != 0 && !whitelist_id.contains(Integer.parseInt(type))) {
				s += ":" + ((int)(block.getData()));
			}
		} catch (Exception e) {}
		return s.equals(type);
	}
	
	@SuppressWarnings("deprecation")
	private void SetBlock(Block block, String type) {
		String x[] = type.split(":");
		if (x.length == 1) {
			block.setTypeId(Integer.parseInt(x[0]));
		} else {
			block.setTypeId(Integer.parseInt(x[0]));
			block.setData((byte) Integer.parseInt(x[0]));
		}
	}
	
	public void BuildAt(Location loc) {
		for (int i = 0; i < template_width; ++i)
		for (int j = 0; j < template_height; ++j)
		for (int k = 0; k < template_width; ++k) {
			SetBlock(loc.getWorld().getBlockAt(loc.getBlockX() + i, loc.getBlockY() + j, loc.getBlockZ() + k),
					 template_ids[0][i][j][k]);
		}	
	}
	
	private String name;
	private int x_size;
	private int y_size;
	private int z_size;
	private int max_health;
	private int template_width;
	private int template_height;
	private int interval;
	private String output;
	private String input;
	private int storage_cap;
	private String reward_message;
	private String other_require;
	private String type;
	private String attack_type;
	private int rob_pos;
	private int attack_rob_pos;
	private int output_per_resource;
	private int max_target;
	private int attack_x_range, attack_y_range, attack_z_range;
	public int getAttack_x_range() {
		return attack_x_range;
	}
	public int getAttack_y_range() {
		return attack_y_range;
	}
	public int getAttack_z_range() {
		return attack_z_range;
	}
	public int getMax_target() {
		return max_target;
	}
	public int getOutputPerResource() {
		return output_per_resource;
	}
	public int getStorage_cap() {
		return storage_cap;
	}
	public int getRobPos() {
		return rob_pos;
	}
	public int getAttackRobPos() {
		return attack_rob_pos;
	}
	public int getMaxHealth() {
		return max_health;
	}
	public String getType() {
		return type;
	}
	public String getAttackType() {
		return attack_type;
	}

	private String[][][][] template_ids;
	private int attack;
	
	public int getAttack() {
		return attack;
	}
}
