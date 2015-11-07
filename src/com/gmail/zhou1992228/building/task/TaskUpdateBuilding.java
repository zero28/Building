package com.gmail.zhou1992228.building.task;

import com.gmail.zhou1992228.building.BuildingManager;

public class TaskUpdateBuilding implements Runnable {

	@Override
	public void run() {
		BuildingManager.ins.onUpdate();
	}

}
