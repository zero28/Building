package com.gmail.zhou1992228.building;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.util.Util;

public class MilitaryBuilding extends BuildingEntity {

	static public Random random = new Random();
	
	public MilitaryBuilding(ConfigurationSection config) {
		super(config);
		rewards = config.getStringList("reward_list");
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
	}
	
	@Override
	public void onCollect(Player p, int count) {
		if (rewards.size() == 0) {
			p.sendMessage("并没有战利品");
			return;
		}
		Util.giveItems(p, rewards.get(0));
		rewards.remove(0);
	}
	
	@Override
	public void onDamage(Entity entity) {
		if (entity instanceof Player) {
			Player p = (Player) entity;
			if (p.getName().equals(getOwner())) {
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
	}

	private void onRob(Entity e) {
		if (input_count_ > getTemplate().getOutputPerResource()) {
			String robber = "怪物";
			if (e instanceof Player) {
				Player p = (Player) e;
				Util.giveItems(p, getTemplate().getInput());
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
	public void onDamage(MilitaryBuilding attacker) {
		health_ -= attacker.getAttack();
		if (random.nextInt(100) < attacker.getTemplate().getAttackRobPos()) {
			if (input_count_ > getTemplate().getOutputPerResource()) {
				if (!getTemplate().getInput().isEmpty()) {
					attacker.addResource(getTemplate().getInput());
					input_count_ -= getTemplate().getOutputPerResource();
					if (health_ < 50) {
						while (input_count_ > getTemplate().getOutputPerResource()) {
							input_count_ -= getTemplate().getOutputPerResource();
							attacker.addResource(getTemplate().getInput());
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
		
	}
	
	public int getAttack() {
		return getTemplate().getAttack();
	}
	
	public void addResource(String s) {
		rewards.add(s);
	}
	
	@Override
	public void onUpdate() {
		// Do nothing.
	}
	
	private List<BuildingEntity> building_in_range = new ArrayList<BuildingEntity>();
	private List<String> rewards = new ArrayList<String>();
	
	@Override
	public void AddIfInRange(BuildingEntity entity) {
		Location loc = entity.getPos();
		Location p1 = getPos().add(
				   getTemplate().getAttack_x_range(),
				   getTemplate().getAttack_y_range(),
				   getTemplate().getAttack_z_range());
		Location p2 = getPos().add(
				   -getTemplate().getAttack_x_range(),
				   -getTemplate().getAttack_y_range(),
				   -getTemplate().getAttack_z_range());
		if (Util.InsidePos(loc, p1, p2)) {
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
				entities = getPos().getWorld().getEntities();
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
				BuildingEntity building = it.next();
				if (!building.valid) {
					it.remove();
				} else {
					building.onDamage(this);
					--left_attack;
					--input_count_;
				}
				if (left_attack <= 0 || input_count_ <= 0) {
					break;
				}
			}
		} else {
			Building.LOG("ERROR on template!");
		}
	}
	
	@SuppressWarnings("deprecation")
	private boolean Attack(Entity e) {
		Location l1 = 
				getPos().clone().add(getTemplate().getAttack_x_range(),
						 getTemplate().getAttack_y_range(),
						 getTemplate().getAttack_z_range());

		Location l2 = 
				getPos().clone().add(-getTemplate().getAttack_x_range(),
						 -getTemplate().getAttack_y_range(),
						 -getTemplate().getAttack_z_range());
		if (e instanceof Player) {
			Player p = (Player) e;
			if (p.getName().equals(getOwner())) {
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
		// TODO Auto-generated method stub
		return "";
	}
}
