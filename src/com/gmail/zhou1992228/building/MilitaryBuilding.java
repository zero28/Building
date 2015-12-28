package com.gmail.zhou1992228.building;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.util.com.google.common.base.Joiner;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.friend.Friend;
import com.gmail.zhou1992228.building.util.Util;

public class MilitaryBuilding extends BuildingEntity {

	static public Random random = new Random();
	
	public MilitaryBuilding(ConfigurationSection config) {
		super(config);
		rewards = config.getStringList("reward_list");
		rewards_message = config.getStringList("reward_messages");
	}

	public MilitaryBuilding(String owner, Location pos, String type, String name) {
		super(owner, pos, type, name);
	}

	public MilitaryBuilding(String owner, Location pos, String type) {
		super(owner, pos, type);
	}
	
	@Override
	public void Save(ConfigurationSection config) {
		super.Save(config);
		config.set("reward_list", rewards);
		config.set("reward_messages", rewards_message);
	}
	
	@Override
	public void onCollect(Player p, int count) {
		if (rewards.size() == 0) {
			p.sendMessage("并没有战利品");
			return;
		}
		Util.giveItems(p, rewards.get(0));
		p.updateInventory();
		p.sendMessage("你获得了战利品 " + rewards_message.get(0));
		rewards.remove(0);
		rewards_message.remove(0);
	}
	
	@Override
	public boolean onDamage(Entity entity) {
		if (health_ <= 0 || tot_time_ < 0) { return false; }
		if (entity instanceof Player) {
			return false;
			/*
			Player p = (Player) entity;
			if (p.getName().equals(getOwner()) || Friend.ins.isFriend(p.getName(), getOwner())) {
				return;
			}
			if (health_ < 100) {
				Util.NotifyIfOnline(getOwner(), "你的 " + getName() + " 正在被玩家 " + p.getName() + " 破坏！耐久度即将耗尽！");
			} else {
				Util.NotifyIfOnline(getOwner(), "你的 " + getName() + " 正在被玩家 " + p.getName() + " 破坏！");
			}
			health_ -= 5;
			if (random.nextInt(100) < getTemplate().getRobPos()) {
				onRob(entity);
			}
			if (health_ <= 0 && tot_time_ > getTemplate().getDestory_cooldown()) {
				Util.giveItems(p, getTemplate().getDestory_reward());
				p.sendMessage("你摧毁了 " + getName() + "，获得了" + getTemplate().getDestory_reward_message());
			}
			*/
		} else if (entity instanceof Monster) {
			if (health_ < 100) {
				Util.NotifyIfOnline(getOwner(), "你的 " + getName() + " 正在被怪物破坏！耐久度即将耗尽！");
			} else {
				Util.NotifyIfOnline(getOwner(), "你的 " + getName() + " 正在被怪物破坏！");
			}
			health_ -= 3;
			if (random.nextInt(100) < getTemplate().getRobPos()) {
				onRob(entity);
			}
		}
		return true;
	}

	private void onRobAll(Entity e) {
		String robber = "怪物";
		if (e instanceof Player) {
			Player p = (Player) e;
			robber = p.getName();
			while (output_count_ > 0) {
				Util.giveItems(p, getTemplate().getOutput());
				p.sendMessage(String.format("你从 %s 的 %s 中 抢走了 %s",
						getOwner(), getName(), getTemplate().getRewardMessage()));
				--output_count_;
			}
		}
		Util.NotifyIfOnline(
				getOwner(), String.format("你的 %s 中的物品被 %s 抢光了",
									  getName(),
									  robber));
	}

	private void onRob(Entity e) {
		if (input_count_ > getTemplate().getOutputPerResource()) {
			String robber = "怪物";
			if (e instanceof Player) {
				Player p = (Player) e;
				Util.giveItems(p, getTemplate().getInput().get(0));
				robber = p.getName();
				p.sendMessage(String.format("你从 %s 的 %s 中 抢走了 %s",
						getOwner(), getName(), getTemplate().getRewardMessage()));
			}
			input_count_ -= getTemplate().getOutputPerResource();
			Util.NotifyIfOnline(
					getOwner(), String.format("你的 %s 中的物品被 %s 抢走了一些",
										  getName(),
										  robber));
		}
	}

	@Override
	public boolean onDamage(MilitaryBuilding attacker) {
		if (health_ <= 0 || tot_time_ < 0) { return false; }
		health_ -= attacker.getAttack();
		if (random.nextInt(100) < attacker.getTemplate().getAttackRobPos()) {
			if (input_count_ > getTemplate().getOutputPerResource()) {
				if (!getTemplate().getInput().isEmpty()) {
					attacker.addResource(getTemplate().getInput().get(0), getTemplate().getInput_string().get(0));
					input_count_ -= getTemplate().getOutputPerResource();
					if (health_ < 50) {
						while (input_count_ > getTemplate().getOutputPerResource()) {
							input_count_ -= getTemplate().getOutputPerResource();
							attacker.addResource(getTemplate().getInput().get(0), getTemplate().getInput_string().get(0));
						}
					}
				}
			}
		}
		if (health_ < 100) {
			Util.NotifyIfOnline(getOwner(), "你的 " + getName() + " 正在被攻击！耐久度即将耗尽！");
		} else {
			Util.NotifyIfOnline(getOwner(), "你的 " + getName() + " 正在被攻击！");
		}
		if (health_ <= 0 && tot_time_ > getTemplate().getDestory_cooldown()) {
			attacker.addResource(getTemplate().getDestory_reward(),
								 getTemplate().getDestory_reward_message());
		}
		return true;
	}
	
	public int getAttack() {
		return getTemplate().getAttack();
	}
	
	public void addResource(String s, String reward) {
		rewards.add(s);
		rewards_message.add(reward);
	}
	
	@Override
	public void onUpdate() {
		// Do nothing.
	}
	
	private List<BuildingEntity> building_in_range = new ArrayList<BuildingEntity>();
	private List<String> rewards = new ArrayList<String>();
	private List<String> rewards_message = new ArrayList<String>();
	
	@Override
	public void AddIfInRange(BuildingEntity entity) {
		Location loc = entity.getMidPos();
		Location l1 = 
				getMidPos().clone().add(getTemplate().getAttack_x_range(),
						 getTemplate().getAttack_y_range(),
						 getTemplate().getAttack_z_range());

		Location l2 = 
				getMidPos().clone().add(-getTemplate().getAttack_x_range(),
						 -getTemplate().getAttack_y_range(),
						 -getTemplate().getAttack_z_range());
		if (Util.InsidePos(loc, l1, l2)) {
			building_in_range.add(entity);
		}
	}

	@Override
	public void TryAttack() {
		++time_counter_;
		if (time_counter_ > getTemplate().getInterval()) {
			time_counter_ -= getTemplate().getInterval();
		} else {
			return;
		}
		if (getTemplate().getAttackType().equals("entity")) {
			int left_attack = getTemplate().getMax_target();
			List<Entity> entities;
			try {
				entities = getMidPos().getWorld().getEntities();
			} catch (Exception e) {
				return;
			}
			for (Entity e : entities) {
				if (left_attack <= 0 || input_count_ <= 0) {
					break;
				}
				if (Attack(e)) {
					--left_attack;
					--input_count_;
				}
			}
		} else if (getTemplate().getAttackType().equals("building")) {
			int left_attack = getTemplate().getMax_target();
			Iterator<BuildingEntity> it = building_in_range.iterator();
			while (it.hasNext()) {
				if (left_attack <= 0 || input_count_ <= 0) {
					break;
				}
				BuildingEntity building = it.next();
				if (!building.valid) {
					it.remove();
				} else if (!getOwner().equals(building.getOwner())
						&& !Friend.ins.isFriend(getOwner(), building.getOwner())) {
					if (building.onDamage(this)) {
						--left_attack;
						--input_count_;
					}
				}
			}
		} else {
			Building.LOG("ERROR on template!");
		}
	}
	
	@SuppressWarnings("deprecation")
	private boolean Attack(Entity e) {
		Location l1 = 
				getMidPos().clone().add(getTemplate().getAttack_x_range(),
						 getTemplate().getAttack_y_range(),
						 getTemplate().getAttack_z_range());

		Location l2 = 
				getMidPos().clone().add(-getTemplate().getAttack_x_range(),
						 -getTemplate().getAttack_y_range(),
						 -getTemplate().getAttack_z_range());
		if (e instanceof Player) {
			Player p = (Player) e;
			if (p.getName().equals(getOwner()) || Friend.ins.isFriend(getOwner(), p.getName())) {
				return false;
			}
			if (Util.InsidePos(p.getLocation(), l1, l2)) {
				p.damage(getAttack());
				return true;
			} else {
				return false;
			}
		} else if (e instanceof Monster) {
			Monster m = (Monster) e;
			if (Util.InsidePos(e.getLocation(), l1, l2)) {
				m.damage(getAttack());
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public String Info() {
		return String.format(
				 "建筑名称 : %s\n"
			   + "拥有者 : %s\n"
	           + "占地面积(直径) : %d * %d * %d\n"
	           + "生命值 : %d\n"
	           + "原材料 : %s\n"
	           + "剩余攻击次数 : %d\n"
	           + "攻击范围(半径) : %d * %d * %d\n"
	           + "恢复剩余时间 : %d"
	           + "战利品 : %s\n",
	           getName(),
	           getOwner(),
	           getTemplate().getX_size(),getTemplate().getY_size(),getTemplate().getZ_size(),
	           health_,
	           getTemplate().getInput() == null || getTemplate().getInput().size() == 0 ? "不需要原材料" : getTemplate().getInput_string(),
	           input_count_,
	           getTemplate().getAttack_x_range(),getTemplate().getAttack_y_range(),getTemplate().getAttack_z_range(),
	           Math.max(-tot_time_, 0),
	           Joiner.on(", ").join(rewards_message)
	           );
	}

	@Override
	public void attackBy(Player p) {
		if (health_ <= 0 || tot_time_ < 0) { return; }
		if (p.getName().equals(getOwner()) || Friend.ins.isFriend(p.getName(), getOwner())) {
			return;
		}
		if (health_ < 100) {
			Util.NotifyIfOnline(getOwner(), "你的 " + getName() + " 正在被玩家 " + p.getName() + " 破坏！耐久度即将耗尽！");
		} else {
			Util.NotifyIfOnline(getOwner(), "你的 " + getName() + " 正在被玩家 " + p.getName() + " 破坏！");
		}
		health_ -= Util.getAttack(p);
		if (random.nextInt(100) < getTemplate().getRobPos()) {
			onRob(p);
		}
		if (health_ < 30) {
			onRobAll(p);
		}
		if (health_ <= 0 && tot_time_ > getTemplate().getDestory_cooldown()) {
			Util.giveItems(p, getTemplate().getDestory_reward());
			p.sendMessage("你摧毁了 " + getName() + "，获得了" + getTemplate().getDestory_reward_message());
		}
	}
}
