
package org.bdigi.fx;

public class LogDialog extends Stage {

    public LogDialog(App par) {
        this.par = par;
        setTitle("Log");
		VBox vbox = new VBox();
		Console console = new Console(par);
		vbox.getChildren().addAll(console);
		Scene scene = new Scene(vbox);
		setScene(scene);
    }
    
    
    
    public void puttext(String str) {
        console.puttext(str);
        console.puttext("\n");
    }
    
}

