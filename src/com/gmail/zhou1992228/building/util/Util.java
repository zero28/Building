package com.gmail.zhou1992228.building.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.zhou1992228.building.Building;

public class Util {
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
