package net.encode.wurmesp.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.encode.wurmesp.WurmEspMod;

public class ConfigUtils {
	public static void loadProperties(String name) {
		WurmEspMod.modProperties.clear();
		
		InputStream inputStream = null;
		
		Path path = Paths.get("mods", new String[0]);
		path = path.resolve(name +".properties");
		
		try {
			inputStream = Files.newInputStream(path, new OpenOption[0]);
			WurmEspMod.modProperties.load(inputStream);
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (inputStream != null) {
                try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
	}
}
