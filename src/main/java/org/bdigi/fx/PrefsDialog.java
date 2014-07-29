package org.bdigi.fx;


class PrefsDialog(par: App) extends Stage
{

    @FXML TextField callField;
    @FXML TextField nameField;
    @FXML TextField locatorField;
    @FXML ChoiceBox<String> inputDeviceList;
    @FXML ChoiceBox<String> outputDeviceList;
    
    public PrefsDialog(App par) {
        this.par = par;
		try {
			val loader = new FXMLLoader(getClass.getResource("/prefs.fxml"));
			loader.setController(this);
			loader.load();
			val scene = new Scene((Parent)loader.getRoot());
			setTitle("Preferences");
			setScene(scene);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private  mapToFxList(Map<String, Audio.Info> xs) {
        List list = FXCollections.observableArrayList[String]()
        for (Map.Entry dev : xs) {
            list.add(dev._1);
        }
        return list;
    }

    @FXML public void initialize() {
        callField.setText(par.config.call);
        nameField.setText(par.config.name);
        locatorField.setText(par.config.locator);
        inputDeviceList.setItems(mapToFxList(AudioDevice.inputDevices));
        inputDeviceList.setValue(par.config.audioInputDevice);
        outputDeviceList.setItems(mapToFxList(AudioDevice.outputDevices));
        outputDeviceList.setValue(par.config.audioOutputDevice);
        
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
        
    @FXML public void doOk(Event evt) = {
        par.config.call    = callField.getText();
        par.config.name    = nameField.getText();
        par.config.locator = locatorField.getText();
        Audio.Info inp     = inputDeviceList.getValue();
        if (inp != par.config.audioInputDevice) {
            par.setInputDevice(inp);
        }
        par.config.audioInputDevice = inp;
        Audio.Info outp = outputDeviceList.getValue();
        if (outp != par.config.audioOutputDevice) {
            par.setOutputDevice(outp) ;
        }
        par.config.audioOutputDevice = outp;
        par.configSave();
        close();
    }

    @FXML public void doCancel(Event evt) {
        close();
    }

}

