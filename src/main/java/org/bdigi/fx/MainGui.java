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
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.bdigi.core.Digi;
import org.bdigi.core.Property;
import org.bdigi.core.mode.Mode;

import java.io.IOException;


public class MainGui extends Application {

    public void start(Stage stage) throws Exception {
		setUserAgentStylesheet(STYLESHEET_MODENA);
		MainController controller = new MainController(stage);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
		loader.setController(controller);
		Parent page = (Parent) loader.load();
		Scene scene = new Scene(page);
		stage.setTitle("bdigi");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
		stage.setScene(scene);
		stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}






