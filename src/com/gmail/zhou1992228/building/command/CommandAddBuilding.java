package com.gmail.zhou1992228.building.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.BuildingManager;

public class CommandAddBuilding implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg3.length == 0) {
			arg0.sendMessage("请输入需要建造的建筑");
			return true;
		}
		if (arg3.length > 1) {
			BuildingManager.ins.TryAddBuilding((Player) arg0, arg3[0], arg3[1]);
		} else {
			BuildingManager.ins.TryAddBuilding((Player) arg0, arg3[0]);
		}
		return true;
	}
}
