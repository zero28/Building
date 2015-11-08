package com.gmail.zhou1992228.building;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.zhou1992228.building.command.CommandAddBuilding;
import com.gmail.zhou1992228.building.command.CommandCollect;
import com.gmail.zhou1992228.building.task.TaskUpdateBuilding;
import com.gmail.zhou1992228.building.task.TaskValidateBuilding;

public class Building extends JavaPlugin {

	public static Economy econ = null;
	public boolean setupEconomy() {
		if (Building.ins.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = 
				Building.ins.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = (Economy) rsp.getProvider();
		return econ != null;
	}
	
	public static Building ins;
	public static void LOG(String str) {
		Bukkit.getLogger().info(str);
	}
	@Override
	public void onEnable() {
		ins = this;
		this.getCommand("ttbd").setExecutor(new CommandAddBuilding());
		this.getCommand("collect").setExecutor(new CommandCollect());
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TaskUpdateBuilding(), 5 * 20, 5 * 20);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TaskValidateBuilding(), 10 * 20, 10 * 20);
		BuildingManager.ins.Init(this);
	}
	
	@Override
	public void onDisable() {
		LOG("Saving");
		BuildingManager.ins.Save();
	}
}
