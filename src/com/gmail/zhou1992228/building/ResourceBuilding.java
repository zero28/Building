package com.gmail.zhou1992228.building;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

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
		while (output_count_ > 0 && count > 0) {
			--output_count_;
			--count;
			Util.giveItems(p, getTemplate().getOutput());
			p.sendMessage("你从 " + getName() + " 处 获得 " + getTemplate().getRewardMessage());
		}
	}
	@Override
	public void onDamage(Entity entity) {
		if (entity instanceof Player) {
			Player p = (Player) entity;
			if (p.getName().equals(getOwner())) {
				return;
			}
			Util.NotifyIfOnline(getOwner(), "你的 " + getName() + " 正在被玩家 " + p.getName() + " 破坏！");
			if (health_ < 100) {
				Util.NotifyIfOnline(getOwner(), "你的 " + getName() + " 正在被玩家 " + p.getName() + " 破坏！耐久度即将耗尽！");
			}
			health_ -= 5;
			if (random.nextInt() < getTemplate().getRobPos()) {
				onRob(entity);
			}
		} else if (entity instanceof Monster) {
			Util.NotifyIfOnline(getOwner(), "你的 " + getName() + " 正在被怪物破坏！");
			if (health_ < 100) {
				Util.NotifyIfOnline(getOwner(), "你的 " + getName() + " 正在被怪物破坏！耐久度即将耗尽！");
			}
			health_ -= 3;
			if (random.nextInt() < getTemplate().getRobPos()) {
				onRob(entity);
			}
		}
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
}
