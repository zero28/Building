package com.gmail.zhou1992228.building.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.zhou1992228.building.BuildingTemplate;

public class CommandBuildingInfo implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg3.length == 0) {
			arg0.sendMessage("输入 /bi [建筑名称] 查看该建筑的详细资料");
			return true;
		}
		BuildingTemplate bt = BuildingTemplate.building_templates.get(arg3[0]);
		if (bt == null) {
			arg0.sendMessage("没有这个建筑");
			return true;
		}
		arg0.sendMessage(bt.getTemplateInfo());
		return true;
	}
}