package org.bdigi.fx;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bdigi.core.Config;
import org.bdigi.core.Digi;
import org.bdigi.core.audio.Audio;

import java.util.List;
import java.util.Map;

class PrefsDialog  extends Stage
{

    @FXML TextField callField;
    @FXML TextField nameField;
    @FXML TextField locatorField;
    @FXML ChoiceBox<String> inputDeviceList;
    @FXML ChoiceBox<String> outputDeviceList;

    private Digi par;
    
    public PrefsDialog(Digi par) {
        this.par = par;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/prefs.fxml"));
			loader.setController(this);
			loader.load();
			Scene scene = new Scene((Parent)loader.getRoot());
			setTitle("Preferences");
			setScene(scene);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private ObservableList<String> mapToFxList(Map<String, Audio.Info> xs) {
        ObservableList<String> list = FXCollections.observableArrayList();
        for (Map.Entry<String, Audio.Info> dev : xs.entrySet()) {
            list.add(dev.getKey());
        }
        return list;
    }

    @FXML public void initialize() {
        Config c = par.getConfig();
        callField.setText(par.getConfig().call);
        nameField.setText(c.name);
        locatorField.setText(c.locator);
        inputDeviceList.setItems(mapToFxList(Audio.getInputDevices()));
        inputDeviceList.setValue(c.audioInput);
        outputDeviceList.setItems(mapToFxList(Audio.getOutputDevices()));
        outputDeviceList.setValue(c.audioOutput);
        
        /*
        inputDeviceList.valueProperty.addListener(new ChangeListener[String]
            {
            override def changed(ov: ObservableValue[_ <: String], t: String, t1: String)
                {       
                val name = ov.getValue         
                println("Selected: " + name) 
                par.setInputDevice(name)               
                }    
            })
        */
    }
        
    @FXML public void doOk(Event evt)  {
        Config c = par.getConfig();
        c.call    = callField.getText();
        c.name    = nameField.getText();
        c.locator = locatorField.getText();
        String inp = inputDeviceList.getValue();
        if (!c.audioInput.equals(inp)) {
            par.setAudioInput(inp);
        }
        c.audioInput = inp;
        String outp = outputDeviceList.getValue();
        if (!c.audioOutput.equals(outp)) {
            par.setAudioOutput(outp);
        }
        c.audioOutput = outp;
        c.saveFile();
        close();
    }

    @FXML public void doCancel(Event evt) {
        close();
    }

}

