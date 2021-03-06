package com.gmail.zhou1992228.building;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.zhou1992228.building.command.CommandAddBuilding;
import com.gmail.zhou1992228.building.command.CommandBuildingInfo;
import com.gmail.zhou1992228.building.command.CommandBuildlist;
import com.gmail.zhou1992228.building.command.CommandCollect;
import com.gmail.zhou1992228.building.command.CommandFriend;
import com.gmail.zhou1992228.building.command.CommandInfo;
import com.gmail.zhou1992228.building.command.CommandPut;
import com.gmail.zhou1992228.building.command.CommandTemplate;
import com.gmail.zhou1992228.building.command.CommandTestBuild;
import com.gmail.zhou1992228.building.command.CommandTestGive;
import com.gmail.zhou1992228.building.command.CommandUnFriend;
import com.gmail.zhou1992228.building.eventhandler.PlayerAttackBuildingEventHandler;
import com.gmail.zhou1992228.building.eventhandler.RedstoneEventHandler;
import com.gmail.zhou1992228.building.friend.Friend;
import com.gmail.zhou1992228.building.task.TaskAutoSave;
import com.gmail.zhou1992228.building.task.TaskBuildingAttack;
import com.gmail.zhou1992228.building.task.TaskDamageBuildings;
import com.gmail.zhou1992228.building.task.TaskUpdateBuilding;
import com.gmail.zhou1992228.building.task.TaskValidateBuilding;

public class Building extends JavaPlugin {
	// TODO: Repair
	// TODO: show input material
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
		setupEconomy();
		this.getCommand("build").setExecutor(new CommandAddBuilding());
		this.getCommand("collect").setExecutor(new CommandCollect());
		this.getCommand("put").setExecutor(new CommandPut());
		this.getCommand("testbuild").setExecutor(new CommandTestBuild());
		this.getCommand("testgive").setExecutor(new CommandTestGive());
		this.getCommand("xx").setExecutor(new CommandInfo());
		this.getCommand("friend").setExecutor(new CommandFriend());
		this.getCommand("unfriend").setExecutor(new CommandUnFriend());
		this.getCommand("template").setExecutor(new CommandTemplate());
		this.getCommand("buildlist").setExecutor(new CommandBuildlist());
		this.getCommand("bi").setExecutor(new CommandBuildingInfo());
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TaskUpdateBuilding(), 60 * 20, 60 * 20);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TaskValidateBuilding(), 20 * 20, 20 * 20);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TaskDamageBuildings(), 10 * 20, 10 * 20);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TaskBuildingAttack(), 1 * 20, 1 * 20);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TaskAutoSave(), 10 * 20, 60 * 60 * 20);
		this.getServer().getPluginManager().registerEvents(new RedstoneEventHandler(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerAttackBuildingEventHandler(), this);
		BuildingManager.ins.Init(this);
		Friend.ins.Init(this);
	}
	
	@Override
	public void onDisable() {
		LOG("Saving");
		BuildingManager.ins.Save();
		Friend.ins.Save();
	}
}
