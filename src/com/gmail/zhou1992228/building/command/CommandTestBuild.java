package com.gmail.zhou1992228.building.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.BuildingTemplate;

public class CommandTestBuild implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			if (p.isOp()) {
				try {
					BuildingTemplate.building_templates.get(arg3[0]).BuildAt(p.getLocation());
				} catch (Exception e) {
					
				}
			}
		}
		return true;
	}
}