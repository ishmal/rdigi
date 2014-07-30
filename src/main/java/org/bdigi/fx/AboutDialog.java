package org.bdigi.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * A simple About dialog
 */
public class AboutDialog extends Stage {

    public AboutDialog() throws IOException {
        super();
        Parent root = (Parent) FXMLLoader.load(getClass().getResource("/about.fxml"));
        setTitle("About ScalaDigi");
        setScene(new Scene(root));
    }
}
