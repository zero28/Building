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
			p.sendMessage("�㲻�Ǵ˽��������ˣ�");
			return;
		}
		if (output_count_ == 0) {
			p.sendMessage("��û�����أ���Ҫ��ô��~");
			return;
		}
		while (output_count_ > 0 && count > 0) {
			--output_count_;
			--count;
			Util.giveItems(p, getTemplate().getOutput());
			p.sendMessage("��� " + getName() + " �� ��� " + getTemplate().getRewardMessage());
		}
	}
	@Override
	public void onDamage(Entity entity) {
		if (entity instanceof Player) {
			Player p = (Player) entity;
			if (p.getName().equals(getOwner())) {
				return;
			}
			Util.NotifyIfOnline(getOwner(), "��� " + getName() + " ���ڱ���� " + p.getName() + " �ƻ���");
			if (health_ < 100) {
				Util.NotifyIfOnline(getOwner(), "��� " + getName() + " ���ڱ���� " + p.getName() + " �ƻ����;öȼ����ľ���");
			}
			health_ -= 5;
			if (random.nextInt() < getTemplate().getRobPos()) {
				onRob(entity);
			}
		} else if (entity instanceof Monster) {
			Util.NotifyIfOnline(getOwner(), "��� " + getName() + " ���ڱ������ƻ���");
			if (health_ < 100) {
				Util.NotifyIfOnline(getOwner(), "��� " + getName() + " ���ڱ������ƻ����;öȼ����ľ���");
			}
			health_ -= 3;
			if (random.nextInt() < getTemplate().getRobPos()) {
				onRob(entity);
			}
		}
	}

	private void onRob(Entity e) {
		if (output_count_ > 0) {
			String robber = "����";
			if (e instanceof Player) {
				Player p = (Player) e;
				Util.giveItems(p, getTemplate().getOutput());
				robber = p.getName();
				p.sendMessage(String.format("��� %s �� %s �� ������ %s",
						getOwner(), getName(), getTemplate().getRewardMessage()));
			}
			--output_count_;
			Util.NotifyIfOnline(
					getOwner(), String.format("��� %s �е���Ʒ�� %s ������һЩ",
										  getName(),
										  robber));
		}
	}
}
