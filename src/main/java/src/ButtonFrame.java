package src;

import io.restassured.response.Response;
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

    private RestHelper restHelper;
    private File file = null;
    private JTextField name, uri, email, phone;

    public ButtonFrame() {
        super("Personal Jesus");
        constuctGUI();
        restHelper = RestHelper.getInstance();
    }

    private void constuctGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setSize(440, 180);
        setResizable(false);
        setLocationByPlatform(true);
        setAlwaysOnTop(true);

        JLabel uriLbl = new JLabel("URL");
        JLabel nameLbl = new JLabel("Название");
        JLabel phoneLbl = new JLabel("Телефон");
        JLabel emailLbl = new JLabel("Email");
        uri = new JTextField(40);
        name = new JTextField(40);
        phone = new JTextField(40);
        email = new JTextField(40);

        uriLbl.setSize(60, 20);
        uriLbl.setLocation(10, 10);
        nameLbl.setSize(60, 20);
        nameLbl.setLocation(10, 35);
        phoneLbl.setSize(60, 20);
        phoneLbl.setLocation(10, 60);
        emailLbl.setSize(60, 20);
        emailLbl.setLocation(10, 85);

        uri.setSize(350, 20);
        uri.setLocation(70, 10);
        name.setSize(350, 20);
        name.setLocation(70, 35);
        phone.setSize(350, 20);
        phone.setLocation(70, 60);
        email.setSize(350, 20);
        email.setLocation(70, 85);

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

    private void writeFile(Response response) throws IOException {
        Workbook workbook = getWorkbook();
        if (workbook == null) {
            JOptionPane.showMessageDialog(null, "Не выбран файл!");
            return;
        }
        Sheet sheet = workbook.getSheet("LIST_NAME");
        int rowCount = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(rowCount);
        Cell nameCell = row.createCell(1);
        Cell phoneCell = row.createCell(3);
        Cell emailCell = row.createCell(4);
        Cell uriCell = row.createCell(6);

        String uriS = uri.getText();
        String nameS = restHelper.getValue(response, "applicant.shortName");
        String phoneS = restHelper.getValue(response, "applicant.contacts[0].value");
        String emailS = restHelper.getValue(response, "applicant.contacts[1].value");

        name.setText(nameS);
        phone.setText(phoneS);
        email.setText(emailS);

        nameCell.setCellValue(nameS);
        phoneCell.setCellValue(phoneS);
        emailCell.setCellValue(emailS);
        uriCell.setCellValue(uriS);

        try (BufferedOutputStream fio = new BufferedOutputStream(new FileOutputStream(file))) {
            workbook.write(fio);
        }
    }

    private Workbook getWorkbook() {
        if (file == null) {
            return null;
        }
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Не выбран файл!");
        }
        return workbook;
    }

    private class CopyListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String baseUri = uri.getText();
            Response response = restHelper.getResponse(baseUri);

            try {
                writeFile(response);
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
