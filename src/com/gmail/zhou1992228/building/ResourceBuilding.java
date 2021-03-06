package com.gmail.zhou1992228.building;

import net.minecraft.util.com.google.common.base.Joiner;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.friend.Friend;
import com.gmail.zhou1992228.building.util.Util;

public class ResourceBuilding extends BuildingEntity {

	public ResourceBuilding(ConfigurationSection config) {
		super(config);
	}

	public ResourceBuilding(String owner, Location pos, String type, String name) {
		super(owner, pos, type, name);
	}

	public ResourceBuilding(String owner, Location pos, String type) {
		super(owner, pos, type);
	}
	
	@Override
	public void onCollect(Player p, int count) {
		if (!p.getName().equals(getOwner())) {
			p.sendMessage("你不是此建筑的主人！");
			return;
		}
		if (output_count_ == 0) {
			p.sendMessage("还没东西呢，不要这么急~");
			return;
		}
		int collect_count = 0;
		while (output_count_ > 0 && count > 0) {
			--output_count_;
			--count;
			++collect_count;
			Util.giveItems(p, getTemplate().getOutput());
		}
		p.sendMessage("你从 " + getName() + " 处 获得 " + getTemplate().getRewardMessage() + " * " + collect_count);
		p.updateInventory();
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
	@Override
	public boolean onDamage(MilitaryBuilding attacker) {
		if (health_ <= 0 || tot_time_ < 0) { return false; }
		if (random.nextInt(100) < attacker.getTemplate().getAttackRobPos()) {
			if (output_count_ > 0) {
				--output_count_;
				attacker.addResource(getTemplate().getOutput(), getTemplate().getRewardMessage());
			}
			if (health_ < 30) {
				while (output_count_ > 0) {
					--output_count_;
					attacker.addResource(getTemplate().getOutput(), getTemplate().getRewardMessage());
				}
			}
		}
		
		if (health_ < 100) {
			Util.NotifyIfOnline(getOwner(), "你的 " + getName() + " 正在被 " + attacker.getOwner() + " 的建筑攻击！耐久度即将耗尽！");
		} else {
			Util.NotifyIfOnline(getOwner(), "你的 " + getName() + " 正在被 " + attacker.getOwner() + " 的建筑攻击！");
		}
		
		health_ -= attacker.getAttack();
		if (health_ <= 0 && tot_time_ > getTemplate().getDestory_cooldown()) {
			attacker.addResource(getTemplate().getDestory_reward(),
								 getTemplate().getDestory_reward_message());
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
		if (output_count_ > 0) {
			String robber = "怪物";
			if (e instanceof Player) {
				Player p = (Player) e;
				Util.giveItems(p, getTemplate().getOutput());
				robber = p.getName();
				p.sendMessage(String.format("你从 %s 的 %s 中 抢走了 %s",
						getOwner(), getName(), getTemplate().getRewardMessage()));
			}
			--output_count_;
			Util.NotifyIfOnline(
					getOwner(), String.format("你的 %s 中的物品被 %s 抢走了一些",
										  getName(),
										  robber));
		}
	}

	@Override
	public void AddIfInRange(BuildingEntity entity) {}

	@Override
	public void TryAttack() {
		return;
	}

	@Override
	public String Info() {
		return String.format("建筑名称 : %s\n"
						   + "拥有者 : %s\n"
				           + "占地面积(直径) : %d * %d * %d\n"
				           + "生命值 : %d\n"
				           + "产物 : %s\n"
				           + "生产剩余时间 : %d 分钟\n"
				           + "原材料数量 : %s\n"
				           + "所需原材料 : %s\n"
				           + "容量 : %d\n"
				           + "剩余容量 : %d\n"
				           + "恢复剩余时间 : %d\n",
				           getName(),
				           getOwner(),
				           getTemplate().getX_size(),getTemplate().getY_size(),getTemplate().getZ_size(),
				           health_,
				           getTemplate().getRewardMessage(),
				           getTemplate().getInterval() - time_counter_,
				           getTemplate().getInput() == null || getTemplate().getInput().size() == 0 ? "不需要原材料" : input_count_ + "",
				           getTemplate().getInput() == null || getTemplate().getInput().size() == 0 ? "不需要原材料" :
				        	   Joiner.on(" 或 ").join(getTemplate().getInput_string()), 
				           getTemplate().getStorage_cap(),
				           getTemplate().getStorage_cap() - output_count_,
				           Math.max(-tot_time_, 0));
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
