/**
 * Scala SDR tool
 *
 * Authors:
 *   Bob Jamison
 *
 * Copyright (C) 2014 Bob Jamison
 * 
 *  This file is part of the Scala SDR library.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package org.bdigi.fx;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.layout.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.text.Text;



import org.bdigi.core.*;
import org.bdigi.core.mode.Mode;

import java.io.IOException;

class AboutDialog extends Stage {

    public AboutDialog() throws IOException {
        super();
        Parent root = (Parent) FXMLLoader.load(getClass().getResource("/about.fxml"));
        setTitle("About ScalaDigi");
        setScene(new Scene(root));    }
}

class MainController extends Digi {


    @FXML AnchorPane tuningPanelBox;
    @FXML TabPane modePane;  
    @FXML VBox consoleTextBox;
    @FXML VBox inputTextBox;
    private TuningPanel tuningPanel;
    private Console consoleText;
    private InputText inputText;
    private Stage aboutDialog;
    private PrefsDialog prefsDialog;
    private LogDialog logDialog;

    public MainController(Stage stage) {
		tuningPanel = new TuningPanel(this);
		consoleText = new Console(this);
		inputText = new InputText(this, 20, 80);
	
		try {
            aboutDialog = new AboutDialog();
        } catch (IOException e) {

        }

		prefsDialog = new PrefsDialog(this);
		logDialog = new LogDialog(this);
		logDialog.puttext("Hello, world");
	
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent evt) {
				doClose(evt);
			}
		});
    
    }
    
    
        
        
    
    public void doClose      (Event evt){ /* stopProcessing(); */ Platform.exit(); }
    public void doClear      (Event evt){ consoleText.clear(); inputText.clear(); }
    public void doLog        (Event evt){logDialog.show();}
    public void doAbout      (Event evt){aboutDialog.show();}
    public void doPreferences(Event evt){prefsDialog.show();}
    public void doRxTx       (Event evt){setTx(((ToggleButton) evt.getSource()).isSelected());}
    public void doAgc        (Event evt){setAgc(((ToggleButton) evt.getSource()).isSelected());}
    public void doAfc        (Event evt){setAfc(((ToggleButton) evt.getSource()).isSelected());}
    
    
    
    /**
     * Called by the FXMLLoader as a place to do post-loading setup
     */
    @FXML public void initialize() {
        tuningPanelBox.getChildren().add(tuningPanel);
        tuningPanel.setManaged(true);
        consoleTextBox.getChildren().add(consoleText);
        inputTextBox.getChildren().add(inputText);

        for (final Mode mode : getModes()) {
            Property.Mode props = mode.getProperties();
            Tab tab = new Tab(props.getName());
            tab.setTooltip(new Tooltip(props.getTooltip()));
            modePane.getTabs().add(tab);
            FlowPane pane = new FlowPane();
            tab.setContent(pane);
            tab.setOnSelectionChanged(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    setMode(mode);
                }
            });

            for (Property.Control control : props.getControls()) {
                if (control instanceof Property.Boolean) {
                    pane.getChildren().add(new PropertyWidget.Boolean((Property.Boolean) control));
                } else if (control instanceof Property.Radio) {
                    pane.getChildren().add(new PropertyWidget.Radio((Property.Radio) control));
                }
            }
        }
    }
        
    /**
     * Override these in your client code, especially for a GUI
     */

    public String gettext() {
        return (inputText != null) ? inputText.gettext() : "";
    }

    public void puttext(String msg) {
        if (consoleText != null)
            consoleText.puttext(msg);
    }

    public void status(String msg) {
        if (logDialog != null)
            logDialog.puttext(msg + "\n");
    }

    public void updateSpectrum(int ps[]) {
        if (tuningPanel != null)
            tuningPanel.update(ps);
    }
    
    public void updateScope(double buf[][]) {
        if (tuningPanel != null)
            tuningPanel.updateScope(buf);
    }

    //startProcessing();
}


public class MainGui extends Application {
    
    public void start(Stage stage) {
        try {
            MainController controller = new MainController(stage);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            loader.setController(controller);
            Parent page = (Parent) loader.load();
            Scene scene = new Scene(page);
            stage.setTitle("bdigi");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("error:" + e);
            e.printStackTrace();
        }
    }
}






