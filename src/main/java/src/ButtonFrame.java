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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Shelkopryad on 06.11.2018.
 */
public class ButtonFrame extends JFrame {

    private ClipboardWorker worker;
    private File file = null;
    private JTextField name;
    private JTextField uri;
    private boolean nameIsPresent = false;
    private boolean uriIsPresent = false;

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
        setLayout(new FlowLayout());
        GlobalScreen.addNativeKeyListener(new KeyListener());
        setMinimumSize(new Dimension(420, 130));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setAlwaysOnTop(true);
        name = new JTextField(20);
        uri = new JTextField(35);

        JButton save = new JButton("Write");
        save.addActionListener(new CopyListener());
        JButton getFileChooser = new JButton("Choose file");
        getFileChooser.addActionListener(new FileChooserListener());

        add(name);
        add(uri);
        add(save);
        add(getFileChooser);

        setVisible(true);
    }

    private class KeyListener implements NativeKeyListener {

        @Override
        public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        }

        @Override
        public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        }

        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
            if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_TAB) {
                String content = worker.getClipboardContents();
                setText(content);
            }
        }
    }

    private void setText(String content) {
        if (content.matches("http.*://.*")) {
            uri.setText(content);
            uriIsPresent = true;
        } else {
            name.setText(content);
            nameIsPresent = true;
        }
    }

    private void writeFile() throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            Workbook workbook = new XSSFWorkbook(bis);
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            Row row = sheet.createRow(rowCount);
            Cell nameCell = row.createCell(1);
            nameCell.setCellValue(name.getText());
            Cell uriCell = row.createCell(8);
            uriCell.setCellValue(uri.getText());
            try (BufferedOutputStream fio = new BufferedOutputStream(new FileOutputStream(file))) {
                workbook.write(fio);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Не выбран файл!");
        }
    }

    private class CopyListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                writeFile();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Какая-то ошибка.!");
            }
            nameIsPresent = false;
            uriIsPresent = false;
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
