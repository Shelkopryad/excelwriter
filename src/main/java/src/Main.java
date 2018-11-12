package src;

import javax.swing.*;

/**
 * Created by Shelkopryad on 06.11.2018.
 */
public class Main {

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new MainFrame();
    }

}
