package src;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
    private JLabel nameLbl, uriLbl;
    private JTextField name, uri;

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
        setLayout(new FlowLayout(FlowLayout.LEFT));
        GlobalScreen.addNativeKeyListener(new KeyListener());
        setMinimumSize(new Dimension(490, 130));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setAlwaysOnTop(true);
        nameLbl = new JLabel(" - название");
        uriLbl = new JLabel(" - URL");
        name = new JTextField(35);
        uri = new JTextField(35);

        JButton save = new JButton("Write");
        save.addActionListener(new CopyListener());
        JButton getFileChooser = new JButton("Choose file");
        getFileChooser.addActionListener(new FileChooserListener());

        add(name);
        add(nameLbl);
        add(uri);
        add(uriLbl);
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
        } else {
            name.setText(content);
        }
    }

    private void writeFile() throws IOException {
        String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf('.') + 1);
        Workbook workbook = null;

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            switch (extension) {
                case "xlsx":
                    workbook = new XSSFWorkbook(bis);
                    break;
                case "xls":
                    workbook = new HSSFWorkbook(bis);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Формат файла должен быть .xls или .xlsx!");
            }

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            Row row = sheet.createRow(rowCount);
            Cell nameCell = row.createCell(1);
            nameCell.setCellValue(name.getText().equals("") ? "undefined" : name.getText());
            Cell uriCell = row.createCell(8);
            uriCell.setCellValue(uri.getText().equals("") ? "undefined" : uri.getText());
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
                JOptionPane.showMessageDialog(null, "Какая-то ошибка!");
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
