package src;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * Created by 16734975 on 06.11.2018.
 */
public class ClipboardWorker implements ClipboardOwner {

    public void lostOwnership(Clipboard clipboard, Transferable transferable) {

    }

    /**
     * Get the String residing on the clipboard.
     *
     * @return any text found on the Clipboard; if none found, return an
     * empty String.
     */
    public String getClipboardContents() {
        JOptionPane errorFrame = new JOptionPane();
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText =
                (contents != null) &&
                        contents.isDataFlavorSupported(DataFlavor.stringFlavor)
                ;
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException ex){
                errorFrame.setMessage(ex);
            } catch (IOException ex) {
                errorFrame.setMessage(ex);
            }
        }
        return result;
    }

}
