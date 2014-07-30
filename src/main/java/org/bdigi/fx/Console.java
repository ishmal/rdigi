
package org.bdigi.fx;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.bdigi.core.Digi;

import java.util.LinkedList;
import java.util.Queue;

public class Console extends TextArea {

    private Digi par;
    private boolean autoAdjust;
    private Queue<String> queue;
    private boolean busy;
    private Refresher refresher;

    public Console(Digi par) {
        this.par = par;
        setEditable(false);
        autoAdjust = true;
        queue = new LinkedList<String>();
        busy = false;
        refresher = new Refresher();
		addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent evt) {
				if (evt.getCode() == KeyCode.SPACE)
					autoAdjust = !autoAdjust;
			}
		});
    }
    
    class Refresher implements Runnable {

        public void run() {
            try {
                while (!queue.isEmpty()) {
                    appendText(queue.remove());
                    if (autoAdjust)
                        positionCaret(getText().length());
                }
            } catch (Exception e) {
                par.error("Console: " + e);
                  e.printStackTrace();
            }
        }
    }
    
    public void puttext(String str) {
        queue.add(str);
        Platform.runLater(refresher);
    }
        
    public void clear() {
        queue.clear();
        super.clear();
    }
        
}


