package ch.santiagovollmar.gol.util;

import java.io.*;
import java.util.Properties;

public class PropertyManager {
    private static final Properties defaultProperties;
    private static final Properties properties;
    private static File file;

    static {
        defaultProperties = new Properties();
        properties = new Properties(defaultProperties);
    }

    public static void readProperties() {
        try {
            defaultProperties.load(PropertyManager.class.getResourceAsStream("../configuration/default.properties"));

            String home;
            if (System.getProperty("os.name")
                    .toLowerCase()
                    .contains("windows")) {
                file = new File(System.getenv("APPDATA") + "\\jcgol\\config.properties");
                home = System.getenv("APPDATA") + "\\jcgol\\";
            } else {
                file = new File("/etc/jcgol/config.properties");
                home = "/etc/jcgol/";
            }

            if (file.exists()) {
                FileInputStream in = new FileInputStream(file);
                properties.load(in);
                in.close();
            } else {
                new File(file.getAbsolutePath()
                        .replace("config.properties", "")).mkdirs();
                file.createNewFile();
            }

            defaultProperties.setProperty("jcgol.home", home);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static void set(String key, String value) {
        properties.setProperty(key, value);
    }

    public static void store() throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        properties.store(out, "");
        out.close();
    }
}
