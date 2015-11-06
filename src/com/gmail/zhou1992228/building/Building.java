package com.gmail.zhou1992228.building;

import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.zhou1992228.building.command.CommandAddBuilding;

public class Building extends JavaPlugin {

	public static Building ins;
	@Override
	public void onEnable() {
		ins = this;
		this.getCommand("ttbd").setExecutor(new CommandAddBuilding());
		BuildingManager.ins.Init(this);
	}
}
