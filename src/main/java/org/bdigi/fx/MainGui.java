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


package org.bdigi.fx


import javafx.application.{Application, Platform}
import javafx.beans.value.{ChangeListener,ObservableValue}
import javafx.geometry.{HPos,VPos}
import javafx.stage.{Stage,WindowEvent}
import javafx.collections.{FXCollections,ObservableList}
import javafx.fxml.{FXML,FXMLLoader}
import javafx.scene.{Node=>jfxNode, Parent, Scene}
import javafx.scene.control.{Button,CheckBox,ChoiceBox,Tab,TabPane,TextArea,TextField,ToggleButton,Tooltip}
import javafx.event.{ActionEvent,Event,EventHandler}
import javafx.scene.layout.{AnchorPane,FlowPane,HBox,VBox,VBoxBuilder,Pane}
import javafx.scene.input.{KeyEvent,KeyCode}
import javafx.scene.paint.Color
import javafx.scene.image.Image
import javafx.scene.text.Text



import org.bdigi.core.*;













public class MainController extends Digi {


    @FXML AnchorPane tuningPanelBox;
    @FXML TabPane modePane;  
    @FXML VBox consoleTextBox;
    @FXML VBox inputTextBox;
    TuningPanel tuningPanel;
    Console consoleText;
    InputText inputText;
    Stage aboutDialog;
    PrefsDialog prefsDialog;
    LogDialog logDialog;

    public MainController(Stage stage) {
		tuningPanel = new TuningPanel(this);
		consoleText = new Console(this);
		inputText = new InputText(this, 20, 80);
	
		aboutDialog = new Stage() {
			Parent root = (Parent) FXMLLoader.load(getClass.getResource("/about.fxml"));
			setTitle("About ScalaDigi");
			setScene(new Scene(root));
		}
		
		prefsDialog = new PrefsDialog(this);
		val logDialog = new LogDialog(this);
		logDialog.puttext("Hello, world");
	
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent evt) {
				doClose(evt);
			}
		});
    
    }
    
    
        
        
    
    public void doClose      (Event evt){ stopProcessing(); Platform.exit(); }
    public void doClear      (Event evt){ consoleText.clear(); inputText.clear(); }
    public void doLog        (Event evt){logDialog.show();}
    public void doAbout      (Event evt){aboutDialog.show();}
    public void doPreferences(Event evt){prefsDialog.show();}
    public void doRxTx       (Event evt){rxtx = ((ToggleButton) evt.getSource()).isSelected();}
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
        
        for (Mode mode : modes) {
            Tab tab = new Tab(mode.name);
            tab.setTooltip(new Tooltip(mode.tooltip));
            modePane.getTabs().add(tab);
            FlowPane pane = new FlowPane();
            tab.setContent(pane);
            tab.setOnSelectionChanged(new EventHandler<Event>() {
                public void handle(Event evt) {
                    self.mode = mode; //set parent to this mode
                }
            })
            
            for (Properties prop : mode.properties.properties) {
                switch (prop) {
                     case p : BooleanProperty :
                         pane.getChildren.add(new BooleanPropertyWidget(p));
                         break;
                     case p : RadioProperty :
                         pane.getChildren.add(new RadioPropertyWidget(p));
                         break;
                     default:
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
    
    public void updateScope(double x, double y) {
        if (tuningPanel != null)
            tuningPanel.updateScope(x, y);
    }

    startProcessing();    
}


class Main extends Application
{
    
    override def start(stage: Stage) =
        {
        try
            {
            val controller = new MainController(stage)
            val loader = new FXMLLoader(getClass.getResource("/main.fxml"))
            loader.setController(controller)
            val page = loader.load.asInstanceOf[Parent]
            val scene = new Scene(page)
            stage.setTitle("bdigi")
            stage.getIcons.add(new Image(getClass.getResourceAsStream("/icon.png")));
            stage.setScene(scene)
            stage.show
            }
        catch
            {
            case e: java.io.IOException => println("error:" + e)
                                e.printStackTrace
            }
        }     
}


object Main
{
    def main(argv: Array[String]) : Unit =
        {
        javafx.application.Application.launch(classOf[Main], argv:_*)
        }
}











