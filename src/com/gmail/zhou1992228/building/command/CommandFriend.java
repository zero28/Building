package com.gmail.zhou1992228.building.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.friend.Friend;

public class CommandFriend implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			if (arg3.length > 0) {
				Friend.ins.AddRelation(p.getName().toLowerCase(), arg3[0].toLowerCase());
				p.sendMessage(String.format("你 或 你的建筑 现在 不会攻击 %s 或 %s 的建筑",
						arg3[0], arg3[0]));
			} else {
				p.sendMessage("请输入玩家ID");
			}
		}
		return true;
	}
}