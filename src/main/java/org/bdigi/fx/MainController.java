package org.bdigi.fx;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.bdigi.core.Digi;
import org.bdigi.core.Property;
import org.bdigi.core.mode.Mode;

import java.io.IOException;

/**
 * The controller for the "main" view (main.fxml)
 */
public class MainController extends Digi {


    @FXML
    AnchorPane tuningPanelBox;
    @FXML
    TabPane modePane;
    @FXML
    VBox consoleTextBox;
    @FXML
    VBox inputTextBox;
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


    @FXML
    public void doClose(Event evt) {
        /* stopProcessing(); */
        Platform.exit();
    }

    @FXML
    public void doClear(Event evt) {
        consoleText.clear();
        inputText.clear();
    }

    @FXML
    public void doLog(Event evt) {
        logDialog.show();
    }

    @FXML
    public void doAbout(Event evt) {
        if (aboutDialog != null)
            aboutDialog.show();
    }

    @FXML
    public void doPreferences(Event evt) {
        prefsDialog.show();
    }

    @FXML
    public void doRxTx(Event evt) {
        setTx(((ToggleButton) evt.getSource()).isSelected());
    }

    @FXML
    public void doAgc(Event evt) {
        setAgc(((ToggleButton) evt.getSource()).isSelected());
    }

    @FXML
    public void doAfc(Event evt) {
        setAfc(((ToggleButton) evt.getSource()).isSelected());
    }


    /**
     * Called by the FXMLLoader as a place to do post-loading setup
     */
    @FXML
    public void initialize() {
        tuningPanelBox.getChildren().add(tuningPanel);
        tuningPanel.setManaged(true);
        consoleTextBox.getChildren().add(consoleText);
        inputTextBox.getChildren().add(inputText);

        for (final Mode mode : getModes()) {
            Property props = mode.getProperties();
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

    @Override
    public String gettext() {
        return (inputText != null) ? inputText.gettext() : "";
    }

    @Override
    public void puttext(String msg) {
        if (consoleText != null)
            consoleText.puttext(msg);
    }

    @Override
    public void status(String msg) {
        if (logDialog != null)
            logDialog.puttext(msg + "\n");
    }

    @Override
    public void showSpectrum(double ps[]) {
        if (tuningPanel != null)
            tuningPanel.updateSpectrum(ps);
    }

    @Override
    public void showScope(double buf[][]) {
        if (tuningPanel != null)
            tuningPanel.updateScope(buf);
    }

    //startProcessing();
}
