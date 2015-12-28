package com.gmail.zhou1992228.building.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.util.Util;

public class CommandTestGive implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			if (p.isOp()) {
				try {
					Util.giveItem(p, arg3[0]);
				} catch (Exception e) {
					
				}
			}
		}
		return true;
	}
}