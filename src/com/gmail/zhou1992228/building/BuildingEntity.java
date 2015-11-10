package com.gmail.zhou1992228.building;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.util.Util;

public class BuildingEntity {
	public static Random random = new Random();
	
	public static BuildingEntity createBuildingEntity(
			String owner, Location pos, String building_name) {
		return createBuildingEntity(owner, pos, building_name, "");
	}
	public static BuildingEntity createBuildingEntity(
			String owner, Location pos, String building_name, String name) {
		BuildingTemplate bt = BuildingTemplate.building_templates.get(building_name);
		if (bt.getType().equals("Resource")) {
			return new ResourceBuilding(owner, pos, building_name, name);
		}
		return null;
	}
	public static BuildingEntity createBuildingEntity(ConfigurationSection config) {
		if (config.getString("type", "").equals("Resource")) {
			return new ResourceBuilding(config);
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
		pos_ = new Location(Bukkit.getWorld(config.getString("world")),
							config.getInt("x"),
							config.getInt("y"),
							config.getInt("z"));
		input_count_ = config.getInt("input_count");
		output_count_ = config.getInt("output_count");
		time_counter_ = config.getInt("time_counter");
		name_ = config.getString("name");
		health_ = config.getInt("health");
		template_ = BuildingTemplate.building_templates.get(building_name);
	}
	public void Save(ConfigurationSection config) {
		config.set("type", building_name);
		config.set("owner", owner_);
		config.set("world", pos_.getWorld().getName());
		config.set("x", pos_.getBlockX());
		config.set("y", pos_.getBlockY());
		config.set("z", pos_.getBlockZ());
		config.set("input_count", input_count_);
		config.set("output_count", output_count_);
		config.set("time_counter", time_counter_);
		config.set("health", health_);
		config.set("name", name_);
	}
	
	public Location getPos() {
		return pos_;
	}
	public String getBuilding_type() {
		return building_name;
	}
	public String getOwner() {
		return owner_;
	}
	public String getName() {
		return name_;
	}
	public void onUpdate() {
		time_counter_++;
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
	
	public void getOutput(Player p, int count) {
		if (!p.getName().equals(owner_)) {
			p.sendMessage("你不是此建筑的主人！");
			return;
		}
		if (output_count_ == 0) {
			p.sendMessage("还没东西呢，不要这么急~");
			return;
		}
		while (output_count_ > 0 && count > 0) {
			--output_count_;
			--count;
			Util.giveItems(p, template_.getOutput());
			p.sendMessage("你从 " + name_ + " 处 获得 " + template_.getRewardMessage());
		}
	}
	
	public void addInput(Player p, int count) {
		if (template_.getInput().isEmpty()) {
			p.sendMessage("然而这并没有什么~用！");
			return;
		}
		while (count > 0) {
			--count;
			if (!Util.haveRequires(p, template_.getInput())) {
				p.sendMessage("你没有足够的物品！");
				break;
			} else {
				Util.takeItems(p, template_.getInput());
				++input_count_;
			}
		}
		p.sendMessage("已放入材料");
	}

	public void tryDamage(Entity entity) {
		if (entity instanceof Player) {
			Player p = (Player) entity;
			if (p.getName().equals(owner_)) {
				return;
			}
			Util.NotifyIfOnline(owner_, "你的 " + name_ + " 正在被玩家 " + p.getName() + " 破坏！");
			if (health_ < 100) {
				Util.NotifyIfOnline(owner_, "你的 " + name_ + " 正在被玩家 " + p.getName() + " 破坏！耐久度即将耗尽！");
			}
			health_ -= 5;
			if (random.nextInt() < template_.getRobPos()) {
				onRob(entity);
			}
		} else if (entity instanceof Monster) {
			Util.NotifyIfOnline(owner_, "你的 " + name_ + " 正在被怪物破坏！");
			if (health_ < 100) {
				Util.NotifyIfOnline(owner_, "你的 " + name_ + " 正在被怪物破坏！耐久度即将耗尽！");
			}
			health_ -= 3;
			if (random.nextInt() < template_.getRobPos()) {
				onRob(entity);
			}
		}
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
		if (!loc.getWorld().getName().equals(pos_.getWorld().getName())) {
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
	
	public boolean Validate() {
		Location new_loc = template_.Match(pos_);
		if (new_loc == null) {
			return false;
		}
		pos_ = new_loc;
		if (health_ <= 0) {
			return false;
		}
		return true;
	}
	
	public void putInput(Player p, int count) {
		if (template_.getInput().isEmpty()) {
			p.sendMessage("此建筑不需要添加材料");
			return;
		}
		while (count > 0) {
			if (Util.takeRequires(p, template_.getInput())) {
				--count;
				++input_count_;
			} else {
				p.sendMessage("你没有足够的材料了");
			}
		}
		p.sendMessage("材料添加成功");
	}

	private void onRob(Entity e) {
		if (output_count_ > 0) {
			String robber = "怪物";
			if (e instanceof Player) {
				Player p = (Player) e;
				Util.giveItems(p, template_.getOutput());
				robber = p.getName();
				p.sendMessage(String.format("你从 %s 的 %s 中 抢走了 %s",
						owner_, name_, template_.getRewardMessage()));
			}
			--output_count_;
			Util.NotifyIfOnline(
					owner_, String.format("你的 %s 中的物品被 %s 抢走了一些",
										  name_,
										  robber));
		}
	}
	protected int input_count_;
	protected int output_count_;
	protected int health_;
	protected int time_counter_;
	
	private Location pos_;
	private String building_name;
	private String owner_;
	private String name_;
	private BuildingTemplate template_;
}
