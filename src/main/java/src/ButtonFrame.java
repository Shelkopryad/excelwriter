package src;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 16734975 on 06.11.2018.
 */
public class ButtonFrame extends JFrame {

    private ClipboardWorker worker;
    private File file = null;
    private JButton copy;

    public ButtonFrame() {
        super("Personal Jesus");
        constuctGUI();
        worker = new ClipboardWorker();
    }

    private void constuctGUI() {
        setSize(200, 110);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setAlwaysOnTop(true);
        JPanel panel = new JPanel();
        copy = new JButton("Copy");
        copy.setEnabled(false);
        copy.addActionListener(new Handler());
        panel.add(copy);

        JButton getFileChooser = new JButton("Choose file");
        getFileChooser.addActionListener(new FileChooserListener());
        panel.add(getFileChooser);
        getContentPane().add(panel);

        setVisible(true);
    }

    private class Handler implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            String content = worker.getClipboardContents();

            try {
                writeFile(Arrays.asList(content));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Какая-то ошибка");
            }

            System.out.println(worker.getClipboardContents());
        }

        private void writeFile(List<String> newRow) throws IOException {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                Workbook workbook = new XSSFWorkbook(bis);
                Sheet sheet = workbook.getSheetAt(0);
                int rowCount = sheet.getPhysicalNumberOfRows();
                Row row = sheet.createRow(rowCount + 1);
                for (int cellIndex = 0; cellIndex < newRow.size(); cellIndex++) {
                    Cell cell = row.createCell(cellIndex);
                    cell.setCellValue(newRow.get(cellIndex));
                }

                try (BufferedOutputStream fio = new BufferedOutputStream(new FileOutputStream(file))) {
                    workbook.write(fio);
                }
            }
        }
    }

    private class FileChooserListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            JFileChooser chooser = new JFileChooser();
            int ret = chooser.showDialog(null, "Открыть файл");
            if (ret == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
                copy.setEnabled(true);
            }
        }
    }

}
