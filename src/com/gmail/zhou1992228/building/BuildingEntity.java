package com.gmail.zhou1992228.building;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.util.Util;

public abstract class BuildingEntity {
	public static Random random = new Random();
	
	public static BuildingEntity createBuildingEntity(
			String owner, Location pos, String building_name) {
		return createBuildingEntity(owner, pos, building_name, "");
	}
	public static BuildingEntity createBuildingEntity(
			String owner, Location pos, String building_name, String name) {
		BuildingTemplate bt = BuildingTemplate.building_templates.get(building_name);
		if (bt.getType().equalsIgnoreCase("Resource")) {
			return new ResourceBuilding(owner, pos, building_name, name);
		}
		if (bt.getType().equalsIgnoreCase("Military")) {
			return new MilitaryBuilding(owner, pos, building_name, name);
		}
		return null;
	}
	
	abstract public String Info();
	
	public static BuildingEntity createBuildingEntity(ConfigurationSection config) {
		BuildingTemplate bt = BuildingTemplate.building_templates.get(
				config.getString("building_name"));
		if (bt.getType().equalsIgnoreCase("Resource")) {
			return new ResourceBuilding(config);
		}
		if (bt.getType().equalsIgnoreCase("Military")) {
			return new MilitaryBuilding(config);
		}
		return null;
	}
	
	protected BuildingEntity(String owner, Location pos, String building_name) {
		pos_ = pos;
		owner_ = owner;
		this.building_name = building_name;
		name_ = owner + " 的 " + building_name;
		template_ = BuildingTemplate.building_templates.get(building_name);
		health_ = template_.getMaxHealth();
	}
	protected BuildingEntity(String owner, Location pos, String building_name, String name) {
		pos_ = pos;
		owner_ = owner;
		this.building_name = building_name;
		if (name.isEmpty()) {
			name_ = owner + " 的 " + building_name;
		} else {
			name_ = name;
		}
		template_ = BuildingTemplate.building_templates.get(building_name);
		health_ = template_.getMaxHealth();
	}
	protected BuildingEntity(ConfigurationSection config) {
		building_name = config.getString("building_name");
		owner_ = config.getString("owner");
		World world = Bukkit.getWorld(config.getString("world"));
		pos_ = new Location(world,
							config.getInt("x"),
							config.getInt("y"),
							config.getInt("z"));
		input_count_ = config.getInt("input_count");
		output_count_ = config.getInt("output_count");
		time_counter_ = config.getInt("time_counter");
		tot_time_ = config.getInt("tot_time");
		name_ = config.getString("custom_name");
		health_ = config.getInt("health");
		template_ = BuildingTemplate.building_templates.get(building_name);
	}
	public void Save(ConfigurationSection config) {
	  try {
		config.set("building_name", building_name);
		config.set("owner", owner_);
		config.set("world", pos_.getWorld().getName());
		config.set("x", pos_.getBlockX());
		config.set("y", pos_.getBlockY());
		config.set("z", pos_.getBlockZ());
		config.set("input_count", input_count_);
		config.set("output_count", output_count_);
		config.set("time_counter", time_counter_);
		config.set("tot_time", tot_time_);
		config.set("health", health_);
		config.set("custom_name", name_);
	  } catch (Exception e) {}
	}
	
	public Location getPos() {
		return pos_;
	}
	public String getBuildingName() {
		return building_name;
	}
	public String getOwner() {
		return owner_;
	}
	public String getName() {
		return name_;
	}
	public BuildingTemplate getTemplate() {
		return template_;
	}
	public void onUpdate() {
		time_counter_++;
		tot_time_++;
		if (template_ == null) {
			return;
		}
		if (time_counter_ >= template_.getInterval()) {
			if (template_.getInput().isEmpty() || input_count_ > 0) {
				if (template_.getStorage_cap() > output_count_) {
					--input_count_; ++output_count_;
				}
			}
			time_counter_ -= template_.getInterval();
		}
	}
	
	abstract public void onCollect(Player p, int count);
	abstract public void onDamage(Entity entity);
	abstract public void TryAttack();
	abstract public void AddIfInRange(BuildingEntity entity);
	abstract public void onDamage(MilitaryBuilding attacker);
	
	public boolean inTemplate(Location loc) {
		Location l1 = pos_.clone().add(
				getTemplate().getTemplate_width() / 2,
				getTemplate().getTemplate_height() / 2,
				getTemplate().getTemplate_width() / 2);
		Location l2 = pos_.clone().subtract(
				getTemplate().getTemplate_width() / 2,
				getTemplate().getTemplate_height() / 2,
				getTemplate().getTemplate_width() / 2);
		return Util.InsidePos(loc, l1, l2);
	}
	
	public boolean Collide(Location loc1, Location loc2) {
		int Xa = Math.abs(loc1.getBlockX() - loc2.getBlockX());
		int Ya = Math.abs(loc1.getBlockY() - loc2.getBlockY());
		int Za = Math.abs(loc1.getBlockZ() - loc2.getBlockZ());
		int Xb = template_.getX_size();
		int Yb = template_.getY_size();
		int Zb = template_.getZ_size();
		int Xma = (loc1.getBlockX() + loc2.getBlockX()) / 2;
		int Yma = (loc1.getBlockY() + loc2.getBlockY()) / 2;
		int Zma = (loc1.getBlockZ() + loc2.getBlockZ()) / 2;
		int Xmb = pos_.getBlockX();
		int Ymb = pos_.getBlockY();
		int Zmb = pos_.getBlockZ();
		return Math.abs(Xma - Xmb) <= (Xa + Xb) / 2 &&
			   Math.abs(Yma - Ymb) <= (Ya + Yb) / 2 &&
			   Math.abs(Zma - Zmb) <= (Za + Zb) / 2;
	}
	
	public boolean inBuilding(Location loc) {
		try {
			if (!loc.getWorld().getName().equals(pos_.getWorld().getName())) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		int max_x = pos_.getBlockX()
				  + template_.getX_size() / 2;
		int min_x = pos_.getBlockX()
				  - template_.getX_size() / 2;
		int max_y = pos_.getBlockY()
				  + template_.getY_size() / 2;
		int min_y = pos_.getBlockY()
				  - template_.getY_size() / 2;
		int max_z = pos_.getBlockZ()
				  + template_.getZ_size() / 2;
		int min_z = pos_.getBlockZ()
				  - template_.getZ_size() / 2;
		return (min_x <= loc.getBlockX() && loc.getBlockX() <= max_x) &&
			   (min_y <= loc.getBlockY() && loc.getBlockY() <= max_y) &&
			   (min_z <= loc.getBlockZ() && loc.getBlockZ() <= max_z);
	}
	
	public boolean isDestoried() {
		return health_ <= 0;
	}
	
	private int validate_error = 0;
	
	public boolean Validate() {
		try {
			if (!getPos().getChunk().isLoaded()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		if (health_ <= 0) {
			valid = false;
			return false;
		}
		Location new_loc = template_.Match(pos_);
		if (new_loc == null) {
			++validate_error;
			if (validate_error >= 10) {
				return false;
			} else {
				Util.NotifyIfOnline(getOwner(), "你的 " + this.getName() + " 不符合建筑规范，请尽快修复！", true);
				return true;
			}
		}
		pos_ = new_loc;
		validate_error = 0;
		return true;
	}
	
	public void putInput(Player p, int count) {
		if (template_.getInput().isEmpty()) {
			Util.NotifyPlayer(p, "此建筑不需要添加材料");
			return;
		}
		int ccc = 0;
		boolean goon = true;
		while (count > 0 && goon) {
			goon = false;
			for (String input : template_.getInput()) {
				if (Util.takeRequires(p, input)) {
					--count;
					input_count_ += template_.getOutputPerResource();
					++ccc;
					goon = true;
				}
			}
		}
		if (ccc != 0) {
			Util.NotifyPlayer(p, "材料添加成功");
		} else {
			Util.NotifyPlayer(p, "你没有足够的材料");
		}
	}
	
	public boolean isValid() {
		return valid;
	}

	protected boolean valid = true;
	protected int input_count_;
	protected int output_count_;
	protected int health_;
	protected int time_counter_;
	protected int tot_time_ = 0;
	
	private Location pos_;
	private String building_name;
	private String owner_;
	private String name_;
	private BuildingTemplate template_;

}
