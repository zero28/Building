package com.gmail.zhou1992228.building.command;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.gmail.zhou1992228.building.Building;
import com.gmail.zhou1992228.building.util.Util;

public class CommandTemplate implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			if (!arg0.isOp()) {
				arg0.sendMessage("你不是op");
				return true;
			}
			Player p = (Player) arg0;
			if (arg3.length < 1) {
				p.sendMessage("请输入正确的指令");
				return true;
			}
			if (arg3[0].equalsIgnoreCase("select1")) {
				Block l = Util.getBlockLookingAt(p, 1000);
				if (l == null) { p.sendMessage("眼前没有方块"); return true; }
				pos1.put(p.getName(), l.getLocation());
				Building.LOG(l.toString());
				return true;
			} else if (arg3[0].equalsIgnoreCase("select2")) {
				Block l = Util.getBlockLookingAt(p, 1000);
				if (l == null) { p.sendMessage("眼前没有方块"); return true; }
				pos2.put(p.getName(), l.getLocation());
				Building.LOG(l.toString());
				return true;
			}
			if (arg3.length < 2) {
				p.sendMessage("请输入正确的指令");
				return true;
			}
			if (arg3[1].equals("template")) {
				SetTemplate(arg3[0], p);
				return true;
			}
			if (arg3.length < 3) {
				p.sendMessage("请输入正确的指令");
				return true;
			}
			SetTemplate(arg3[0], arg3[1], arg3[2]);
		}
		return true;
	}
	
	private void SetTemplate(String building_name, String key, String value) {
		FileConfiguration config = Util.getConfigWithName("template.yml");
		config.set("buildings." + building_name + "." + key, value);
		Util.SaveConfigToName(config, "template.yml");
	}
	
	private void SetTemplate(String building_name, Player p) {
		FileConfiguration config = Util.getConfigWithName("template.yml");
		Location l1 = pos1.get(p.getName());
		Location l2 = pos2.get(p.getName());
		if (l1 == null || l2 == null) {
			p.sendMessage("请先选择两个点");
			return;
		}
		if (!l1.getWorld().getName().equals(l2.getWorld().getName())) {
			p.sendMessage("两点不在同一个世界");
			return;
		}
		SetTemplate(config.createSection("buildings." + building_name),
				pos1.get(p.getName()), pos2.get(p.getName()));
		Util.SaveConfigToName(config, "template.yml");
	}
	static private String validChar = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private void SetTemplate(ConfigurationSection config, Location l1, Location l2) {
		HashMap<String, String> block_map = new HashMap<String, String>();
		block_map.put("0", " ");
		ArrayList<String> template = new ArrayList<String>();
		ArrayList<String> typelist = new ArrayList<String>();
		int index = 0;
		int min_x = Math.min(l1.getBlockX(), l2.getBlockX());
		int min_y = Math.min(l1.getBlockY(), l2.getBlockY());
		int min_z = Math.min(l1.getBlockZ(), l2.getBlockZ());
		int max_x = Math.max(l1.getBlockX(), l2.getBlockX());
		int max_y = Math.max(l1.getBlockY(), l2.getBlockY());
		int max_z = Math.max(l1.getBlockZ(), l2.getBlockZ());
		int width = Math.max(max_x - min_x,  max_z - min_z) + 1;
		config.set("width", width);
		int empty_x_up = (width - ((max_x - min_x) + 1)) / 2;
		int empty_x_down = width - ((max_x - min_x) + 1) - empty_x_up;
		String empty_x = "";
		for (int i = 0; i < width; ++i) empty_x += " ";
		
		int empty_z_up = (width - ((max_z - min_z) + 1)) / 2;
		int empty_z_down = width - ((max_z - min_z) + 1) - empty_z_up;
		World w = l1.getWorld();
		String empty_z_front = "", empty_z_end = "";
		for (int i = 0; i < empty_z_up; ++i) empty_z_front += " ";
		for (int i = 0; i < empty_z_down; ++i) empty_z_end += " ";
		Building.LOG(String.format("min_x: %d min_y: %d min_z: %d\n"
								 + "max_x: %d max_y: %d max_z: %d\n"
		                         + "exu: %d exd: %d ezu: %d ezd: %d",
		                         min_x, min_y, min_z,
		                         max_x, max_y, max_z,
		                         empty_x_up, empty_x_down, empty_z_up, empty_z_down));
		for (int y = min_y; y <= max_y; ++y) {
			for (int i = 0; i < empty_x_up; ++i) {
				template.add(empty_x);
			}
			for (int x = min_x; x <= max_x; ++x) {
				String row = "";
				row += empty_z_front;
				for (int z = min_z; z <= max_z; ++z) {
					String b_str = BlockToString(w.getBlockAt(x, y, z));
					if (!block_map.containsKey(b_str)) {
						block_map.put(b_str, validChar.charAt(index++) + "");
					}
					row += block_map.get(b_str);
				}
				row += empty_z_end;
				template.add(row);
			}
			for (int i = 0; i < empty_x_down; ++i) {
				template.add(empty_x);
			}
		}
		config.set("template", template);
		for (String k : block_map.keySet()) {
			typelist.add(block_map.get(k) + ":" + k);
		}
		config.set("typelist", typelist);
	}
	
	@SuppressWarnings("deprecation")
	private String BlockToString(Block b) {
		String ret = b.getTypeId() + "";
		if (b.getData() != 0) {
			ret += ":" + ((int)(b.getData()));
		}
		return ret;
	}
	
	private HashMap<String, Location> pos1 = new HashMap<String, Location>();
	private HashMap<String, Location> pos2 = new HashMap<String, Location>();
}