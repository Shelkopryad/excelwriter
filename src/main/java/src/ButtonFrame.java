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
import javax.swing.filechooser.FileFilter;
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
    private JTextField name, uri;

    public ButtonFrame() {
        super();
        setGlobalKeyListner();
        constuctGUI();
        worker = new ClipboardWorker();
    }

    private void setGlobalKeyListner() {
        GlobalScreen.setEventDispatcher(new SwingDispatchService());
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        GlobalScreen.addNativeKeyListener(new KeyListener());
    }

    private void constuctGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setSize(430, 130);
        setResizable(false);
        setLocationByPlatform(true);
        setAlwaysOnTop(true);

        JLabel nameLbl = new JLabel("Название");
        JLabel uriLbl = new JLabel("URL");
        name = new JTextField(40);
        uri = new JTextField(40);
        nameLbl.setSize(60, 20);
        nameLbl.setLocation(10, 10);
        uriLbl.setSize(60, 20);
        uriLbl.setLocation(10, 35);
        name.setSize(350, 20);
        name.setLocation(65, 10);
        uri.setSize(350, 20);
        uri.setLocation(65, 35);

        JButton save = new JButton("Write");
        save.addActionListener(new CopyListener());
        JButton getFileChooser = new JButton("Choose file");
        getFileChooser.addActionListener(new FileChooserListener());
        save.setSize(100, 30);
        save.setLocation(210, 60);
        getFileChooser.setSize(100, 30);
        getFileChooser.setLocation(315, 60);

        add(name);
        add(nameLbl);
        add(uri);
        add(uriLbl);
        add(save);
        add(getFileChooser);

        setVisible(true);
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
        final String UNDEFINED = "UNDEFINED";
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

            Sheet sheet = workbook.getSheet("НАЗВАНИЕ_ЛИСТА");
            int rowCount = sheet.getPhysicalNumberOfRows();
            Row row = sheet.createRow(rowCount);
            Cell nameCell = row.createCell(1);
            nameCell.setCellValue(name.getText().equals("") ? UNDEFINED : name.getText());
            Cell uriCell = row.createCell(8);
            uriCell.setCellValue(uri.getText().equals("") ? UNDEFINED : uri.getText());
            try (BufferedOutputStream fio = new BufferedOutputStream(new FileOutputStream(file))) {
                workbook.write(fio);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Не выбран файл!");
        }
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
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.getAbsolutePath().endsWith("xls") || file.getAbsolutePath().endsWith("xlsx");
                }

                @Override
                public String getDescription() {
                    return null;
                }
            });
            int ret = chooser.showDialog(null, "Открыть файл");
            if (ret == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
            }
        }
    }

}
