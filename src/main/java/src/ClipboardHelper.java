package src;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * Created by Shelkopryad on 06.11.2018.
 */
public class ClipboardHelper implements ClipboardOwner {

    private static ClipboardHelper instance;

    private ClipboardHelper() {
    }

    public static ClipboardHelper getInstance() {
        if (instance == null) {
            instance = new ClipboardHelper();
        }
        return instance;
    }

    public void lostOwnership(Clipboard clipboard, Transferable transferable) {

    }

    /**
     * Get the String residing on the clipboard.
     *
     * @return any text found on the Clipboard; if none found, return an
     * empty String.
     */
    public String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText =
                (contents != null) &&
                        contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        return result;
    }
}
