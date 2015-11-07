package com.gmail.zhou1992228.building.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.zhou1992228.building.Building;

public class Util {
    public static FileConfiguration getConfigWithName(String name) {
    	Building.LOG("123");
		File file = new File(Building.ins.getDataFolder(), name);
		Building.LOG("123");
		if (file == null || !file.exists()) {
            try {
            	Building.LOG("123");
                file.createNewFile();
            } catch (IOException e) {
            	Building.LOG("123");
                e.printStackTrace();
            }
        }
		Building.LOG("123");
		return YamlConfiguration.loadConfiguration(file);
	}
    
    public static void SaveConfigToName(FileConfiguration config, String fileName) {
    	File file = new File(Building.ins.getDataFolder(), fileName);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
