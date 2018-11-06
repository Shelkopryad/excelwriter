package src;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Iterator;

/**
 * Created by 16734975 on 06.11.2018.
 */
public class ButtonFrame extends JFrame {

    private ClipboardWorker worker;
    private File file = null;

    public ButtonFrame() {
        super("Personal Jesus");
        constuctGUI();
        worker = new ClipboardWorker();
    }

    private void constuctGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setAlwaysOnTop(true);
        JPanel panel = new JPanel();
        JButton button = new JButton("Copy");
        button.addActionListener(new Handler());
        panel.add(button);

        JButton getFileChooser = new JButton("Показать JFileChooser");
        getFileChooser.addActionListener(new FileChooserListener());
        panel.add(getFileChooser);
        getContentPane().add(panel);

        setVisible(true);
    }

    private class Handler implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            String content = worker.getClipboardContents();


//            XSSFWorkbook workbook = new XSSFWorkbook(fis);
//
//            XSSFSheet sheet = workbook.getSheetAt(0);
//
//            Iterator<Row> rowIterator = sheet.iterator();
//
//            Iterator<Cell> cellIterator = row.cellIterator();



//            try (FileWriter writer = new FileWriter(file, true)) {
//                writer.append(content + "\n");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            System.out.println(worker.getClipboardContents());
        }

        private void writeFile() throws IOException {
            FileInputStream fis;

            if (file != null) {
                fis = new FileInputStream(file);
            } else {
                JFileChooser chooser = new JFileChooser();
                int ret = chooser.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    file = chooser.getSelectedFile();
                }
                fis = new FileInputStream(file);
            }

            HSSFWorkbook workbook = new HSSFWorkbook(fis);
            HSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
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
