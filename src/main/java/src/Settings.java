package src;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Shelkopryad on 12.11.2018.
 */
public class Settings extends JFrame {

    private Props props;
    private JTextField tokenField, uriIngex, nameIndex, phoneIndex, emailIndex, listName;

    public Settings() {
        super("Settings");
        props = Props.getInstance();
        constuctGUI();
    }

    private void constuctGUI() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(null);
        setSize(440, 185);
        setResizable(false);
        setLocationByPlatform(true);
        setAlwaysOnTop(true);

        JLabel tokenLbl = new JLabel("Token");
        JLabel listNameLbl = new JLabel("Лист");
        JLabel uriLbl = new JLabel("URL");
        JLabel nameLbl = new JLabel("Название");
        JLabel phoneLbl = new JLabel("Телефон");
        JLabel emailLbl = new JLabel("Email");

        tokenLbl.setSize(60, 20);
        tokenLbl.setLocation(10, 10);
        listNameLbl.setSize(60, 20);
        listNameLbl.setLocation(10, 35);
        uriLbl.setSize(60, 20);
        uriLbl.setLocation(10, 60);
        nameLbl.setSize(60, 20);
        nameLbl.setLocation(135, 60);
        phoneLbl.setSize(60, 20);
        phoneLbl.setLocation(10, 85);
        emailLbl.setSize(60, 20);
        emailLbl.setLocation(135, 85);

        tokenField = new JTextField(50);
        uriIngex = new JTextField(2);
        nameIndex = new JTextField(2);
        phoneIndex = new JTextField(2);
        emailIndex = new JTextField(2);
        listName = new JTextField(40);

        tokenField.setSize(350, 20);
        tokenField.setLocation(70, 10);
        listName.setSize(350, 20);
        listName.setLocation(70, 35);
        uriIngex.setSize(20, 20);
        uriIngex.setLocation(70, 60);
        nameIndex.setSize(20, 20);
        nameIndex.setLocation(200, 60);
        phoneIndex.setSize(20, 20);
        phoneIndex.setLocation(70, 85);
        emailIndex.setSize(20, 20);
        emailIndex.setLocation(200, 85);

        JButton saveProperties = new JButton("Save");
        saveProperties.setSize(100, 30);
        saveProperties.setLocation(170, 120);
        saveProperties.addActionListener(new SaveSettingsListener());

        add(tokenLbl);
        add(tokenField);
        add(listNameLbl);
        add(listName);
        add(uriLbl);
        add(uriIngex);
        add(nameLbl);
        add(nameIndex);
        add(phoneLbl);
        add(phoneIndex);
        add(emailLbl);
        add(emailIndex);
        add(saveProperties);

        setVisible(true);
        loadProperties();
    }

    private void loadProperties() {
        String token = props.getProperty("token");
        String list = props.getProperty("listName");
        String uriInd = props.getProperty("uriInd");
        String nameInd = props.getProperty("nameInd");
        String phoneInd = props.getProperty("phoneInd");
        String emailInd = props.getProperty("emailInd");
        tokenField.setText(token);
        listName.setText(list);
        uriIngex.setText(uriInd);
        nameIndex.setText(nameInd);
        phoneIndex.setText(phoneInd);
        emailIndex.setText(emailInd);
    }

    private class SaveSettingsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            props.setProperty("token", tokenField.getText());
            props.setProperty("listName", listName.getText());
            props.setProperty("uriInd", uriIngex.getText());
            props.setProperty("nameInd", nameIndex.getText());
            props.setProperty("phoneInd", phoneIndex.getText());
            props.setProperty("emailInd", emailIndex.getText());
            props.write();
            setVisible(false);
        }
    }

}
