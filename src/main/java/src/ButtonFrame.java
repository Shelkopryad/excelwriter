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

    private ClipboardHelper helper;
    private File file = null;
    private JTextField name, uri, email, phone;

    public ButtonFrame() {
        super("Personal Jesus");
        setGlobalKeyListner();
        constuctGUI();
        helper = ClipboardHelper.getInstance();
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
        setSize(430, 175);
        setResizable(false);
        setLocationByPlatform(true);
        setAlwaysOnTop(true);

        JLabel nameLbl = new JLabel("Название");
        JLabel uriLbl = new JLabel("URL");
        JLabel phoneLbl = new JLabel("Телефон");
        JLabel emailLbl = new JLabel("Email");
        name = new JTextField(40);
        phone = new JTextField(40);
        email = new JTextField(40);
        uri = new JTextField(40);

        nameLbl.setSize(60, 20);
        nameLbl.setLocation(10, 10);
        phoneLbl.setSize(60, 20);
        phoneLbl.setLocation(10, 35);
        emailLbl.setSize(60, 20);
        emailLbl.setLocation(10, 60);
        uriLbl.setSize(60, 20);
        uriLbl.setLocation(10, 85);

        name.setSize(350, 20);
        name.setLocation(65, 10);
        phone.setSize(350, 20);
        phone.setLocation(65, 35);
        email.setSize(350, 20);
        email.setLocation(65, 60);
        uri.setSize(350, 20);
        uri.setLocation(65, 85);

        JButton save = new JButton("Write");
        JButton getFileChooser = new JButton("Choose file");
        save.addActionListener(new CopyListener());
        getFileChooser.addActionListener(new FileChooserListener());
        save.setSize(100, 30);
        save.setLocation(210, 110);
        getFileChooser.setSize(100, 30);
        getFileChooser.setLocation(315, 110);

        add(nameLbl);
        add(name);
        add(phoneLbl);
        add(phone);
        add(emailLbl);
        add(email);
        add(uriLbl);
        add(uri);
        add(save);
        add(getFileChooser);

        setVisible(true);
    }

    private void setText(String content) {
        content = content.trim();

        if (content.matches("http.*://.*")) {
            uri.setText(content);
        } else if (content.matches("\\+[\\d( )?]*")) {
            phone.setText(content);
        } else if (content.matches("[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")) {
            email.setText(content);
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

            Sheet sheet = workbook.getSheet("LIST_NAME");
            int rowCount = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(rowCount);
            Cell nameCell = row.createCell(1);
            nameCell.setCellValue(name.getText().equals("") ? UNDEFINED : name.getText());
            Cell phoneCell = row.createCell(3);
            phoneCell.setCellValue(phone.getText().equals("") ? UNDEFINED : phone.getText());
            Cell emailCell = row.createCell(4);
            emailCell.setCellValue(email.getText().equals("") ? UNDEFINED : email.getText());
            Cell uriCell = row.createCell(6);
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
                String content = helper.getClipboardContents();
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
