
package org.bdigi.fx;


public class InputText extends TextArea {

    private int lastPos;

    public InputText(App par, int rows, int cols) {
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
        {
        String text = getText();
        int len = text.length();
        String res = (lastPos < len) ? text.substring(lastPos) : "";
        lastPos = len;
        return res;
    }
}
