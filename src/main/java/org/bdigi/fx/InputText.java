
package org.bdigi.fx;


import javafx.scene.control.TextArea;
import org.bdigi.core.Digi;

public class InputText extends TextArea {

    private int lastPos;
    private Digi par;

    public InputText(Digi par, int rows, int cols) {
        super();
        this.par = par;
		setPrefRowCount(rows);
		setPrefColumnCount(cols);
		setEditable(true);
		setWrapText(true);
		lastPos = 0;
    }
    
    public void clear() {
        super.clear();
        lastPos = 0;
    }
    
    public String gettext() {
        String text = getText();
        int len = text.length();
        String res = (lastPos < len) ? text.substring(lastPos) : "";
        lastPos = len;
        return res;
    }
}
