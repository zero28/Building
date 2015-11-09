package com.gmail.zhou1992228.building.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.BuildingEntity;
import com.gmail.zhou1992228.building.BuildingManager;

public class CommandPut implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			BuildingEntity building = BuildingManager.ins.getNearBuilding(p);
			if (building == null) {
				p.sendMessage("��ĸ���û�н���");
				return true;
			}
			if (!building.getOwner().equals(p.getName())) {
				p.sendMessage("�ⲻ����Ľ���");
				return true;
			}
			int count = 1;
			try {
				count = Integer.parseInt(arg3[0]);
			} catch (Exception e) {}
			building.putInput(p, count);
		}
		return true;
	}
}