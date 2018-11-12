package src;

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
public class MainFrame extends JFrame {

    private RestHelper restHelper;
    private Props props;
    private File file = null;
    private JTextField uriField, nameField, phoneField, emailField;

    public MainFrame() {
        super("Personal Jesus");
        constuctGUI();
        restHelper = RestHelper.getInstance();
        props = Props.getInstance();
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

        uriField = new JTextField(40);
        nameField = new JTextField(40);
        phoneField = new JTextField(40);
        emailField = new JTextField(40);

        uriLbl.setSize(60, 20);
        uriLbl.setLocation(10, 10);
        nameLbl.setSize(60, 20);
        nameLbl.setLocation(10, 35);
        phoneLbl.setSize(60, 20);
        phoneLbl.setLocation(10, 60);
        emailLbl.setSize(60, 20);
        emailLbl.setLocation(10, 85);

        uriField.setSize(350, 20);
        uriField.setLocation(70, 10);
        nameField.setSize(350, 20);
        nameField.setLocation(70, 35);
        phoneField.setSize(350, 20);
        phoneField.setLocation(70, 60);
        emailField.setSize(350, 20);
        emailField.setLocation(70, 85);

        JButton saveProperties = new JButton("Settings");
        JButton save = new JButton("Write file");
        JButton getFileChooser = new JButton("Choose file");
        saveProperties.addActionListener(new GetSettingsListener());
        save.addActionListener(new CopyListener());
        getFileChooser.addActionListener(new FileChooserListener());
        saveProperties.setSize(100, 30);
        saveProperties.setLocation(105, 115);
        save.setSize(100, 30);
        save.setLocation(210, 115);
        getFileChooser.setSize(100, 30);
        getFileChooser.setLocation(315, 115);

        add(uriLbl);
        add(uriField);
        add(nameLbl);
        add(nameField);
        add(phoneLbl);
        add(phoneField);
        add(emailLbl);
        add(emailField);
        add(saveProperties);
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

        boolean flag = pushInfo(workbook);
        if (!flag) {
            JOptionPane.showMessageDialog(null, "Ошибка. Свяжитесь с разработчиком.");
            return;
        }

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
            JOptionPane.showMessageDialog(null, "Неизвестная ошибка!");
        }
        return workbook;
    }

    private boolean pushInfo(Workbook workbook) {
        Sheet sheet = workbook.getSheet(props.getProperty("listName"));
        int rowCount = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(rowCount);

        if (props.getProperty("nameInd") == null || props.getProperty("phoneInd") == null || props.getProperty("emailInd") == null || props.getProperty("uriInd") == null) {
            return false;
        }

        Cell nameCell = row.createCell(Integer.parseInt(props.getProperty("nameInd")) - 1);
        Cell phoneCell = row.createCell(Integer.parseInt(props.getProperty("phoneInd")) - 1);
        Cell emailCell = row.createCell(Integer.parseInt(props.getProperty("emailInd")) - 1);
        Cell uriCell = row.createCell(Integer.parseInt(props.getProperty("uriInd")) - 1);

        String uri = uriField.getText();
        String name = restHelper.getValue(props.getProperty("name"));
        String phone = restHelper.getValue(props.getProperty("phone"));
        String email = restHelper.getValue(props.getProperty("email"));

        nameField.setText(name);
        phoneField.setText(phone);
        emailField.setText(email);

        nameCell.setCellValue(name);
        phoneCell.setCellValue(phone);
        emailCell.setCellValue(email);
        uriCell.setCellValue(uri);
        return true;
    }

    private class GetSettingsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            new Settings();
        }
    }

    private class CopyListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String token = props.getProperty("token");
            String baseUri = uriField.getText();
            restHelper.getResponse(baseUri, token);

            try {
                writeFile();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка записи в файл!");
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
