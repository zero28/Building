package com.gmail.zhou1992228.building.eventhandler;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gmail.zhou1992228.building.BuildingEntity;
import com.gmail.zhou1992228.building.BuildingManager;

public class PlayerAttackBuildingEventHandler implements Listener {
	@EventHandler
	public void onPlayerAttackBuilding(PlayerInteractEvent e) {
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			BuildingEntity be = BuildingManager.ins.getBuildingWithLocation(b.getLocation());
			if (be != null) {
				if (be.inTemplate(b.getLocation())) {
					be.attackBy(e.getPlayer());
				}
			}
		}
	}
}
