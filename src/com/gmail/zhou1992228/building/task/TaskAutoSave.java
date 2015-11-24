package com.gmail.zhou1992228.building.task;

import com.gmail.zhou1992228.building.Building;
import com.gmail.zhou1992228.building.BuildingManager;
import com.gmail.zhou1992228.building.friend.Friend;

public class TaskAutoSave implements Runnable {

	@Override
	public void run() {
		Building.LOG("Building Auto Saving");
		BuildingManager.ins.Save();
		Friend.ins.Save();
	}
}
