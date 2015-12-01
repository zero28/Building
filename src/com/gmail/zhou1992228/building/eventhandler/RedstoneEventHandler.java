package com.gmail.zhou1992228.building.eventhandler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import com.gmail.zhou1992228.building.BuildingEntity;
import com.gmail.zhou1992228.building.BuildingManager;

public class RedstoneEventHandler implements Listener {
	@EventHandler
	public void onRedstoneChange(BlockRedstoneEvent e) {
		if (e.getBlock().getType() == Material.STONE_BUTTON ||
			e.getBlock().getType() == Material.WOOD_BUTTON) {
			BuildingEntity b = BuildingManager.ins.getBuildingWithLocation(e.getBlock().getLocation());
			if (b != null) {
				if (b.inTemplate(e.getBlock().getLocation())) {
					Player p = Bukkit.getPlayer(b.getOwner());
					if (p != null) {
						if (b.inBuilding(p.getLocation())) {
							b.onCollect(p, 1000);
						}
					}
				}
			}
		}
	}
}
