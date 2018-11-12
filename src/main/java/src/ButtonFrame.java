package src;

import io.restassured.response.Response;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Created by Shelkopryad on 06.11.2018.
 */
public class ButtonFrame extends JFrame {

    private RestHelper restHelper;
    private File file = null;
    private JTextField nameField, uriField, emailField, phoneField, tokenField;

    public ButtonFrame() {
        super("Personal Jesus");
        constuctGUI();
        restHelper = RestHelper.getInstance();
    }

    private void constuctGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setSize(440, 205);
        setResizable(false);
        setLocationByPlatform(true);
        setAlwaysOnTop(true);

        JLabel tokenLbl = new JLabel("Token");
        JLabel uriLbl = new JLabel("URL");
        JLabel nameLbl = new JLabel("Название");
        JLabel phoneLbl = new JLabel("Телефон");
        JLabel emailLbl = new JLabel("Email");

        tokenField = new JTextField(40);
        uriField = new JTextField(40);
        nameField = new JTextField(40);
        phoneField = new JTextField(40);
        emailField = new JTextField(40);

        tokenLbl.setSize(60, 20);
        tokenLbl.setLocation(10, 10);
        uriLbl.setSize(60, 20);
        uriLbl.setLocation(10, 35);
        nameLbl.setSize(60, 20);
        nameLbl.setLocation(10, 60);
        phoneLbl.setSize(60, 20);
        phoneLbl.setLocation(10, 85);
        emailLbl.setSize(60, 20);
        emailLbl.setLocation(10, 110);

        tokenField.setSize(350, 20);
        tokenField.setLocation(70, 10);
        uriField.setSize(350, 20);
        uriField.setLocation(70, 35);
        nameField.setSize(350, 20);
        nameField.setLocation(70, 60);
        phoneField.setSize(350, 20);
        phoneField.setLocation(70, 85);
        emailField.setSize(350, 20);
        emailField.setLocation(70, 110);

        JButton save = new JButton("Write");
        JButton getFileChooser = new JButton("Choose file");
        save.addActionListener(new CopyListener());
        getFileChooser.addActionListener(new FileChooserListener());
        save.setSize(100, 30);
        save.setLocation(210, 140);
        getFileChooser.setSize(100, 30);
        getFileChooser.setLocation(315, 140);

        add(tokenLbl);
        add(tokenField);
        add(nameLbl);
        add(nameField);
        add(phoneLbl);
        add(phoneField);
        add(emailLbl);
        add(emailField);
        add(uriLbl);
        add(uriField);
        add(save);
        add(getFileChooser);

        setVisible(true);
    }

    private void writeFile() throws IOException {
        Workbook workbook = getWorkbook();
        if (workbook == null) {
            JOptionPane.showMessageDialog(null, "Не выбран файл!");
            return;
        }
        pushInfo(workbook);
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

    private void pushInfo(Workbook workbook) {
        Sheet sheet = workbook.getSheet("list");
        int rowCount = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(rowCount);
        Cell nameCell = row.createCell(1);
        Cell phoneCell = row.createCell(4);
        Cell emailCell = row.createCell(5);
        Cell uriCell = row.createCell(8);

        String uri = uriField.getText();
        String name = restHelper.getValue("applicant.shortName");
        String phone = restHelper.getValue("applicant.contacts[0].value");
        String email = restHelper.getValue("applicant.contacts[1].value");

        nameField.setText(name);
        phoneField.setText(phone);
        emailField.setText(email);

        nameCell.setCellValue(name);
        phoneCell.setCellValue(phone);
        emailCell.setCellValue(email);
        uriCell.setCellValue(uri);
    }

    private class CopyListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String token = tokenField.getText();
            String baseUri = uriField.getText();
            Response response = restHelper.getResponse(baseUri, token);

            System.out.println(baseUri + " - " + token);
            response.prettyPrint();

            try {
                writeFile();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Какая-то ошибка! Наши эксперты уже разбираются!");
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
