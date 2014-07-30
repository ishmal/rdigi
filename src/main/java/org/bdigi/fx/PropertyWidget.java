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


import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.bdigi.core.BooleanProperty
import org.bdigi.core.RadioProperty;


public class PropertyWidget {

    public static class Boolean extends ToggleButton 
        implements EventHandler<ActionEvent> {
    
        public Boolean(Property.Boolean p) {
            super(p.getLabel());
            setSelected(p.getValue())
            if (p.getTooltip().length() > 0)
                setTooltip(new Tooltip(p.getTooltip()))
            setOnAction(this);
        }
        
		public void handle(ActionEvent evt) {
			p.setValue(isSelected());
		}

    }

    public static class Radio extends VBox {
        
        public Radio(Property.Radio p) {

            getStyleClass().add("radiogroup");
            HBox hbox = new HBox();
            getChildren().addAll(new Label(p.getLabel()), hbox);
            ToggleGroup group = new ToggleGroup();
            
        
            for (int idx=0 ; i<p.getItems().length ; i++) {
                RadioButton btn = new RadioButton(p.items[idx]);
                btn.setToggleGroup(group);
                hbox.getChildren.add(btn);
                if (idx == p.value)
                    btn.setSelected(true);
                if (p.tooltip.size > 0)
                    btn.setTooltip(new Tooltip(p.getTooltip()));
                btn.setOnAction(new EventHandler<ActionEvent>() {
                     int index = idx;
                     
                     override def handle(evt: ActionEvent)
                         {
                         p.value = index
                         }
                     })
                }
}
