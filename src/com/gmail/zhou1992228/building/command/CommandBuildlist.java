package com.gmail.zhou1992228.building.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.zhou1992228.building.BuildingTemplate;

public class CommandBuildlist implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		arg0.sendMessage(BuildingTemplate.buildingList());
		return true;
	}
}
