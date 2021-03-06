package com.gmail.zhou1992228.building;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import com.gmail.zhou1992228.building.task.TaskHighlightBlock;
import com.gmail.zhou1992228.building.util.Util;
import com.google.common.base.Joiner;

public class BuildingTemplate {
	public static HashMap<String, BuildingTemplate> building_templates = new HashMap<String, BuildingTemplate>();
	public static HashSet<Integer> whitelist_id = new HashSet<Integer>();

	public static String buildingList() {
		StringBuffer sb = new StringBuffer();
		sb.append("建筑列表:\n");
		for (BuildingTemplate bt : building_templates.values()) {
			sb.append(bt.getName() + "\n");
		}
		return sb.toString();
	}

	public static void AddBuildingTemplate(String name,
			ConfigurationSection config) {
		building_templates.put(name, new BuildingTemplate(config));
	}

	public String getTemplateInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("最大生命值 : %d\n", max_health));
		sb.append(String.format("占地面积 : %d * %d * %d\n", x_size, y_size, z_size));
		sb.append(String.format("建筑面积 : %d * %d * %d\n", template_width, template_height, template_width));
		sb.append(String.format("产出 : %s / %d 分钟\n", this.reward_message, this.interval));
		sb.append(String.format("原料 : %s\n", this.input_string.isEmpty() ? "无" : Joiner.on(" 或 ").join(input_string)));
		sb.append(String.format("容量 : %d\n", this.storage_cap));
		sb.append(String.format("比例(每份原料可生产多少份产出) : %d\n", this.output_per_resource));
		sb.append(String.format("造价 : %s\n", this.otherRequireMessage));
		if (!this.destory_reward_message.isEmpty()) {
			sb.append(String.format("摧毁奖励 : %s\n", this.destory_reward_message));
		}
		return sb.toString();
	}
	
	private int defence_cooldown;
	private String defence_reward;
	private String defence_reward_message;
	
	public int getDefence_cooldown() {
		return defence_cooldown;
	}

	public String getDefence_reward() {
		return defence_reward;
	}

	public String getDefence_reward_message() {
		return defence_reward_message;
	}

	public BuildingTemplate(ConfigurationSection config) {
		template_width = config.getInt("width");
		type = config.getString("type", "");
		max_health = config.getInt("max_health", 1000);
		interval = config.getInt("interval");
		x_size = config.getInt("x_size");
		y_size = config.getInt("y_size");
		z_size = config.getInt("z_size");
		input = config.getStringList("input");
		input_string = config.getStringList("input_message");
		output_per_resource = config.getInt("output_per_resource", 1);
		output = config.getString("output", "");
		storage_cap = config.getInt("storage");
		reward_message = config.getString("reward_message", "");
		other_require = config.getString("other_require", "");
		otherRequireMessage = config.getString("other_require_message", "无");
		rob_pos = config.getInt("rob_pos");
		entity_message = config.getStringList("entity_message");
		destory_cooldown = config.getInt("destory_cooldown");
		destory_reward = config.getString("destory_reward");
		destory_reward_message = config.getString("destory_reward_message", "");
		defence_cooldown = config.getInt("defence_cooldown");
		defence_reward = config.getString("defence_reward");
		defence_reward_message = config.getString("defence_reward_message", "");
		recovery_cooldown = config.getInt("recovery_cooldown", 20);

		attack_type = config.getString("attack_type");
		attack_x_range = config.getInt("attack_x_range");
		attack_y_range = config.getInt("attack_y_range");
		attack_z_range = config.getInt("attack_z_range");
		attack_rob_pos = config.getInt("attack_rob_pos");
		max_target = config.getInt("max_target", 1);
		attack = config.getInt("attack");

		entity = config.getStringList("entity");
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
					template_ids[0][i][height][width] = ids.get(template.get(l)
							.charAt(i) + "");
				}
			}
		}
		for (int y = 0; y < template_height; ++y) {
			for (int x = 0; x < template_width; ++x)
				for (int z = 0; z < template_width; ++z) {
					template_ids[4][x][y][z] = template_ids[0][template_width
							- x - 1][y][z];
				}
		}
		for (int i = 1; i < 4; ++i)
			for (int y = 0; y < template_height; ++y) {
				for (int x = 0; x < template_width; ++x)
					for (int z = 0; z < template_width; ++z) {
						template_ids[i][x][y][z] = template_ids[i - 1][template_width
								- z - 1][y][x];
						template_ids[i + 4][x][y][z] = template_ids[i + 3][template_width
								- z - 1][y][x];
					}
			}
		/*
		 * for (int i = 0; i < 8; ++i) { for (int y = 0; y < template_height;
		 * ++y) { String output = "\n"; for (int x = 0; x < template_width; ++x)
		 * { for (int z = 0; z < template_width; ++z) { output +=
		 * template_ids[i][x][y][z] + " "; } output += "\n"; }
		 * Building.LOG(output); } }
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

	public List<String> getInput() {
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

	private boolean MatchEntity(Location loc) {
		Location l1 = loc.clone().add(template_width / 2 + 1,
				template_height / 2 + 1, template_width / 2 + 1);
		Location l2 = loc.clone().subtract(template_width / 2 + 1,
				template_height / 2 + 1, template_width / 2 + 1);
		try {
			for (String e : getEntity()) {
				String entityName = e.split(":")[0];
				int entityCount = Integer.parseInt(e.split(":")[1]);
				if (!MatchEntity(l1, l2,
						Class.forName("org.bukkit.entity." + entityName),
						entityCount)) {
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean MatchEntity(Location l1, Location l2, Class entityClass,
			int count) {
		try {
			List<Entity> entities = (List<Entity>) l1.getWorld()
					.getEntitiesByClass(entityClass);
			for (Entity e : entities) {
				if (Util.InsidePos(e.getLocation(), l1, l2)) {
					--count;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return count <= 0;
	}

	public boolean WeakMatch(Location loc, int dy) {
		return true;
	}
	
	public Location Match(Location loc) {
		if (!MatchEntity(loc))
			return null;
		int orix = (int) (loc.getX() - template_width / 2);
		int oriy = (int) (loc.getY() - 2);
		int oriz = (int) (loc.getZ() - template_width / 2);

		for (int type = 0; type < 8; ++type) {
			if (MatchType(type, orix, oriy, oriz, loc.getWorld(), false, false) == -1) {
				return new Location(loc.getWorld(), orix + template_width / 2,
					oriy + 2, oriz + template_width / 2);
			}
		}
		int mr = 0;
		int showx = 0, showy = 0, showz = 0, showtype = 0;
		for (int dx = -3 ; dx < 4; ++dx)
			for (int dy = -3; dy < 4; ++dy)
				for (int dz = -3; dz < 4; ++dz) {
					int match_x = orix + dx;
					int match_y = oriy + dy;
					int match_z = oriz + dz;
					for (int type = 0; type < 8; ++type) {
						int result = MatchType(type, match_x, match_y, match_z,
								loc.getWorld(), false, false);
						if (result == -1) {
							return new Location(loc.getWorld(),
									match_x	+ template_width / 2,
									match_y	+ 2,
									match_z	+ template_width / 2);
						} else {
							if (result >= mr) {
								mr = result;
								showx = match_x;
								showy = match_y;
								showz = match_z;
								showtype = type;
							}
						}
					}
				}
		MatchType(showtype, showx, showy, showz, loc.getWorld(), false, true);
		return null;
	}

	public boolean TestMatchType(int type, Location l) {
		return MatchType(type, l.getBlockX(), l.getBlockY(), l.getBlockZ(),
				l.getWorld(), true, true) == -1;
	}

	private int MatchType(int type, int ox, int oy, int oz, World world,
			boolean debug, boolean show_effect) {
		int match_count = 0;
		int show_count = show_effect ? 8 : 0;
		for (int j = 0; j < template_height; ++j) {
			boolean should_return = false;
			for (int i = 0; i < template_width; ++i)
			for (int k = 0; k < template_width; ++k) {
				if (debug) {
					Building.LOG("template_id: "
							+ template_ids[type][i][j][k]
							+ "\nBlock:"
							+ world.getBlockAt(ox + i, oy + j, oz + k)
									.toString());
					if (!template_ids[type][i][j][k].equals("*")) {
						Match(world.getBlockAt(ox + i, oy + j, oz + k),
								template_ids[type][i][j][k], true);
					}
				}
				if (!template_ids[type][i][j][k].equals("*")
						&& !Match(world.getBlockAt(ox + i, oy + j, oz + k),
								template_ids[type][i][j][k], false)) {
					if (show_count > 0) {
						TaskHighlightBlock.New(
							new Location(world, ox + i + 0.5, oy + j + 0.5, oz + k + 0.5),
							5);
						--show_count;
					}
					should_return = true;
				} else {
					++match_count;
				}
			}
			if (should_return) {
				return match_count;
			}
		}
		return -1;
	}

	private boolean Match(Block block, String type, boolean debug) {
		for (String singleType : type.split("---")) {
			if (MatchSingle(block, singleType, debug)) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	private boolean MatchSingle(Block block, String type, boolean debug) {
		String s = "" + block.getTypeId();
		int id = Integer.parseInt(type.split(":")[0]);
		try {
			if (debug)
				Building.LOG("try add data");
			if (block.getData() != 0 && !whitelist_id.contains(id)) {
				if (debug)
					Building.LOG("data: " + block.getData());
				s += ":" + block.getData();
			}
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
		}
		if (debug) {
			Building.LOG("output_str: " + s + " match type: " + type);
		}
		if (whitelist_id.contains(id)) {
			type = type.split(":")[0];
		}
		return s.equals(type);
	}

	@SuppressWarnings("deprecation")
	private void SetBlock(Block block, String type) {
		type = type.split("---")[0];
		if (type.equals("*")) return;
		String x[] = type.split(":");
		if (x.length == 1) {
			block.setTypeId(Integer.parseInt(x[0]));
		} else {
			block.setTypeId(Integer.parseInt(x[0]));
			block.setData((byte) Integer.parseInt(x[1]));
		}
	}

	public void BuildAt(Location loc) {
		for (int i = 0; i < template_width; ++i)
			for (int j = 0; j < template_height; ++j)
				for (int k = 0; k < template_width; ++k) {
					SetBlock(
							loc.getWorld().getBlockAt(loc.getBlockX() + i,
									loc.getBlockY() + j, loc.getBlockZ() + k),
							template_ids[0][i][j][k]);
				}
	}

	private String name;
	private int x_size;
	private int y_size;
	private int z_size;
	private int max_health;
	private int template_width;

	public int getTemplate_width() {
		return template_width;
	}

	public int getTemplate_height() {
		return template_height;
	}

	private int template_height;
	private int interval;
	private String output;
	private List<String> input;
	private int storage_cap;
	private String reward_message;
	private String destory_reward;
	private String destory_reward_message;

	public String getDestory_reward() {
		return destory_reward;
	}

	public String getDestory_reward_message() {
		return destory_reward_message;
	}

	private String other_require;
	private String type;
	private String attack_type;
	private int rob_pos;
	private int attack_rob_pos;
	private int recovery_cooldown;

	public int getRecovery_cooldown() {
		return recovery_cooldown;
	}

	private int output_per_resource;
	private int max_target;
	private int destory_cooldown;
	private List<String> input_string;
	private List<String> entity;
	private List<String> entity_message;

	public int getDestory_cooldown() {
		return destory_cooldown;
	}

	public List<String> getEntity() {
		return entity;
	}

	public List<String> getEntity_message() {
		return entity_message;
	}

	public List<String> getInput_string() {
		return input_string;
	}

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
	private String otherRequireMessage;
	public String getOtherRequireMessage() {
		return otherRequireMessage;
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
