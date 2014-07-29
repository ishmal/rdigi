
package org.bdigi.fx;

public class Console(par: App) extends TextArea {

    private App par;
    private boolean autoAdjust;
    private Queue queue;
    private boolean busy;
    private Refresher refresher;

    public Console(App par) {
        this.par = par;
        setEditable(false)
        autoAdjust = true;
        queue = new Queue();
        busy = false;
        refresher = new Refresher();
		addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent evt) {
				if (evt.getCode == KeyCode.SPACE)
					autoAdjust = !autoAdjust;
			}
		});
    }
    
    class Refresher implements Runnable {

        public void run() {
            try {
                while (!queue.isEmpty) {
                    appendText(queue.dequeue);
                    if (autoAdjust)
                        positionCaret(getText.length);
                }
            } catch (Exception e {
                par.error("Console 2: ", e);
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


