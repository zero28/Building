package com.gmail.zhou1992228.building.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.gmail.zhou1992228.building.Building;

public class Util {
	
	public static Block getBlockLookingAt(Player p, int range) {
		double dis = 0.2;
		Location loc = p.getLocation();
		Vector add = loc.getDirection();
		for (int i = 0; i < 100; ++i) {
			loc.add(add);
			if (loc.getBlock().getType().isSolid()) {
				return loc.getBlock();
			}
		}
		return null;
	}
	
    public static FileConfiguration getConfigWithName(String name) {
		File file = new File(Building.ins.getDataFolder(), name);
		if (file == null || !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return YamlConfiguration.loadConfiguration(file);
	}
    
    public static void NotifyIfOnline(String player_name, String message) {
    	Player p = Bukkit.getPlayer(player_name);
    	if (p != null) {
    		p.sendMessage(message);
    	}
    }
    
    public static void SaveConfigToName(FileConfiguration config, String sub_dir, String fileName) {
    	File file = new File(Building.ins.getDataFolder() + File.separator + sub_dir, fileName);
    	if (file == null || !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    	try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void SaveConfigToName(FileConfiguration config, String fileName) {
    	SaveConfigToName(config, "", fileName);
    }
   
	public static void giveItems(Player p, String items) {
		if (items == null || items.isEmpty()) {
			return;
		}
		for (String item : items.split(" ")) {
			giveItem(p, item);
		}
	}

	@SuppressWarnings("deprecation")
	public static void giveItem(Player p, String item) {
		String par[] = item.split(":");
		if (par[0].startsWith("$")) {
			int count = Integer.parseInt(item.substring(1));
			Building.econ.depositPlayer(p.getName(), count);
		} else if (par[0].equalsIgnoreCase("p")) {
			if (par.length < 2) {
				p.sendMessage("配置文件错误，请联系管理员");
			}
		} else if (par[0].equalsIgnoreCase("m")) {
			ItemStack it = createFromString(item);
			p.getInventory().addItem(it);
		} else {
			int id = 0, count = 0, meta = 0;
			if (par.length == 1) {
				id = Integer.parseInt(par[0]);
				count = 1;
			} else if (par.length == 2) {
				id = Integer.parseInt(par[0]);
				count = Integer.parseInt(par[1]);
			} else if (par.length == 3) {
				id = Integer.parseInt(par[0]);
				meta = Integer.parseInt(par[1]);
				count = Integer.parseInt(par[2]);
			}
			p.getInventory().addItem(new ItemStack(id, count, (short) meta));
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack createFromString(String s) {
		try {
			String[] part1 = s.split(":l:");
			String[] part = part1[0].split(":");
			String[] lore = part1[1].split(":");
			if (!part[0].equals("m"))
				return null;
			ItemStack it = null;
			if (part.length == 2) {
				it = new ItemStack(Integer.parseInt(part[1]));
			} else if (part.length == 3) {
				it = new ItemStack(Integer.parseInt(part[1]),
						Integer.parseInt(part[2]));
			} else if (part.length == 4) {
				it = new ItemStack(Integer.parseInt(part[1]),
						Integer.parseInt(part[3]),
						(short) Integer.parseInt(part[2]));
			} else {
				return null;
			}
			ItemMeta im = it.getItemMeta();
			ArrayList<String> l = new ArrayList<String>();
			for (String ss : lore) {
				l.add(ss);
			}
			im.setLore(l);
			it.setItemMeta(im);
			return it;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean InsidePos(Location target, Location pos1, Location pos2) {
		boolean x = (pos1.getBlockX() <= target.getBlockX() && target.getBlockX() <= pos2.getBlockX()) ||
		            (pos2.getBlockX() <= target.getBlockX() && target.getBlockX() <= pos1.getBlockX());
		boolean y = (pos1.getBlockY() <= target.getBlockY() && target.getBlockY() <= pos2.getBlockY()) ||
		            (pos2.getBlockY() <= target.getBlockY() && target.getBlockY() <= pos1.getBlockY());
		boolean z = (pos1.getBlockZ() <= target.getBlockZ() && target.getBlockZ() <= pos2.getBlockZ()) ||
		            (pos2.getBlockZ() <= target.getBlockZ() && target.getBlockZ() <= pos1.getBlockZ());
		return x && y && z;
	}
	
	public static boolean takeRequires(Player p, String requires) {
		if (haveRequires(p, requires)) {
			takeItems(p, requires);
			return true;
		}
		return false;
	}
	
	public static boolean haveRequires(Player p, String requires) {
		if (requires == null || requires.isEmpty()) {
			return true;
		}
		for (String req : requires.split(" ")) {
			if (!haveRequire(p, req)) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	public static boolean haveRequire(Player p, String req) {
		String par[] = req.split(":");
		if (par[0].startsWith("$")) {
			int count = Integer.parseInt(req.substring(1));
			if (Building.econ.bankHas(p.getName(), count).type != ResponseType.SUCCESS) {
				return false;
			}
			return true;
		} else if (par[0].equalsIgnoreCase("p")) {
			if (par.length < 2) {
				p.sendMessage("配置文件错误，请联系管理员");
				return false;
			}
			return p.hasPermission(par[1]);
		} else if (par[0].equalsIgnoreCase("m")) {
			ItemStack it = createFromString(req);
			if (!p.getInventory().containsAtLeast(it, it.getAmount())) {
				return false;
			}
			return true;
		} else if (par[0].equalsIgnoreCase("t")) {
			int count = Integer.parseInt(par[par.length - 1]);
			for (ItemStack it : p.getInventory()) {
				if (it != null) {
					if (it.getItemMeta() != null) {
						if (it.getItemMeta().getLore() != null) {
							boolean yes = true;
							for (int i = 1; i < par.length - 1; ++i) {
								if (!it.getItemMeta().getLore().contains(par[i])) {
									yes = false;
									break;
								}
							}
							if (yes) {
								count -= it.getAmount();
							}
						}
					}
				}
			}
			return count <= 0;
		} else {
			String args[] = req.split(":");
			int id = 0, count = 0, meta = 0;
			if (args.length == 1) {
				id = Integer.parseInt(args[0]);
				count = 1;
			} else if (args.length == 2) {
				id = Integer.parseInt(args[0]);
				count = Integer.parseInt(args[1]);
			} else if (args.length == 3) {
				id = Integer.parseInt(args[0]);
				meta = Integer.parseInt(args[1]);
				count = Integer.parseInt(args[2]);
			}
			ItemStack it = new ItemStack(id, 1, (short) meta);
			if (!p.getInventory().containsAtLeast(it, count)) {
				return false;
			}
			return true;
		}
	}

	public static void takeItems(Player p, String items) {
		if (items == null || items.isEmpty()) {
			return;
		}
		for (String item : items.split(" ")) {
			takeItem(p, item);
		}
	}

	@SuppressWarnings("deprecation")
	public static void takeItem(Player p, String item) {
		String par[] = item.split(":");
		if (item.startsWith("$")) {
			int count = Integer.parseInt(item.substring(1));
			Building.econ.withdrawPlayer(p.getName(), count);
		} else if (par[0].equalsIgnoreCase("m")) {
			ItemStack it = createFromString(item);
			p.getInventory().removeItem(it);
		} else if (par[0].equalsIgnoreCase("t")) {
			int count = Integer.parseInt(par[par.length - 1]);
			for (ItemStack it : p.getInventory()) {
				if (it != null) {
					if (it.getItemMeta() != null) {
						if (it.getItemMeta().getLore() != null) {
							boolean yes = true;
							for (int i = 1; i < par.length - 1; ++i) {
								if (!it.getItemMeta().getLore().contains(par[i])) {
									yes = false;
									break;
								}
							}
							if (yes) {
								p.getInventory().removeItem(it);
								count -= it.getAmount();
								if (count <= 0) return;
							}
						}
					}
				}
			}
		} else {
			int id = 0, count = 0, meta = 0;
			if (par.length == 1) {
				id = Integer.parseInt(par[0]);
				count = 1;
			} else if (par.length == 2) {
				id = Integer.parseInt(par[0]);
				count = Integer.parseInt(par[1]);
			} else if (par.length == 3) {
				id = Integer.parseInt(par[0]);
				meta = Integer.parseInt(par[1]);
				count = Integer.parseInt(par[2]);
			}
			p.getInventory().removeItem(new ItemStack(id, count, (short) meta));
		}
	}
}
