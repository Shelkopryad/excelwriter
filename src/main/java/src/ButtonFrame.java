package src;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.dispatcher.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Shelkopryad on 06.11.2018.
 */
public class ButtonFrame extends JFrame {

    private ClipboardWorker worker;
    private File file = null;
    private List<String> content = new ArrayList<>();
    int globalIndex = 0;

    public ButtonFrame() {
        super();
        GlobalScreen.setEventDispatcher(new SwingDispatchService());
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        constuctGUI();
        worker = new ClipboardWorker();

    }

    private void constuctGUI() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        GlobalScreen.addNativeKeyListener(new KeyListener());
        setMinimumSize(new Dimension(200, 75));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setAlwaysOnTop(true);
        JPanel panel = new JPanel();

        JButton getFileChooser = new JButton("Choose file");
        getFileChooser.addActionListener(new FileChooserListener());
        panel.add(getFileChooser);
        getContentPane().add(panel);

        setVisible(true);
    }

    private class KeyListener implements NativeKeyListener {

        @Override
        public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {}

        @Override
        public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {}

        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
            System.out.println("Key Released: " + NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
            if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_TAB) {
                content.add(globalIndex, worker.getClipboardContents());
                System.out.println(content.get(globalIndex));
                if (globalIndex == 1) {
                    try {
                        writeFile(content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                globalIndex = globalIndex == 0 ? 1 : 0;
            }
        }

    }

    private void writeFile(List<String> newRow) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            Workbook workbook = new XSSFWorkbook(bis);
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            Row row = sheet.createRow(rowCount + 1);
            Cell name = row.createCell(1);
            name.setCellValue(newRow.get(0));
            Cell uri = row.createCell(8);
            uri.setCellValue(newRow.get(1));
            try (BufferedOutputStream fio = new BufferedOutputStream(new FileOutputStream(file))) {
                workbook.write(fio);
            }
        }
    }

    private class FileChooserListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            JFileChooser chooser = new JFileChooser();
            int ret = chooser.showDialog(null, "Открыть файл");
            if (ret == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
            }
        }
    }

}
