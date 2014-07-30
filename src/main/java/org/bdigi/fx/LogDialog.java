
package org.bdigi.fx;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bdigi.core.Digi;

public class LogDialog extends Stage {

    private Digi par;
    private Console console;
    public LogDialog(Digi par) {
        this.par = par;
        setTitle("Log");
		VBox vbox = new VBox();
		console = new Console(par);
		vbox.getChildren().addAll(console);
		Scene scene = new Scene(vbox);
		setScene(scene);
    }
    
    
    
    public void puttext(String str) {
        console.puttext(str);
        console.puttext("\n");
    }
    
}

