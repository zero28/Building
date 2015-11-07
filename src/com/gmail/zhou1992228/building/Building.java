package com.gmail.zhou1992228.building;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.zhou1992228.building.command.CommandAddBuilding;

public class Building extends JavaPlugin {

	public static Building ins;
	public static void LOG(String str) {
		Bukkit.getLogger().info(str);
	}
	@Override
	public void onEnable() {
		ins = this;
		this.getCommand("ttbd").setExecutor(new CommandAddBuilding());
		BuildingManager.ins.Init(this);
	}
	
	@Override
	public void onDisable() {
		LOG("Saving");
		BuildingManager.ins.Save();
	}
}
