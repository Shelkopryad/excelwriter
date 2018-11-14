package src;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

/**
 * Created by Shelkopryad on 12.11.2018.
 */
public class Props {

    private static Props instance;
    private static Properties property;
    private static File file;

    private Props() {
        file = new File("application.properties");
        load();
    }

    public static Props getInstance() {
        if (instance == null) {
            instance = new Props();
        }
        return instance;
    }

    private void load() {
        property = new Properties();

        try (FileInputStream fis = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fis, "ISO8859-1")) {
            property.load(reader);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Файл свойств отсуствует!");
        }
    }

    public void write() {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            property.store(fos, "Properties");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Файл свойств отсуствует!");
        }
    }

    public String getProperty(String key) {
        return property.getProperty(key);
    }

    public void setProperty(String key, String value) {
        property.setProperty(key, value);
    }

}
