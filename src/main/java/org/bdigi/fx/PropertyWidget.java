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


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.bdigi.core.Property;


public class PropertyWidget {

    public static class Boolean extends ToggleButton
    implements EventHandler<ActionEvent> {

        private Property.Boolean p;

        public Boolean(Property.Boolean p) {
            super(p.getName());
            this.p = p;
            setSelected(p.getValue());
            if (p.getTooltip().length() > 0)
                setTooltip(new Tooltip(p.getTooltip()));
            setOnAction(this);
        }

        public void handle(ActionEvent evt) {
            p.setValue(isSelected());
        }

    }

    public static class Radio extends VBox {

        public Radio(final Property.Radio p) {
            getStyleClass().add("radiogroup");
            HBox hbox = new HBox();
            getChildren().addAll(new Label(p.getName()), hbox);
            ToggleGroup group = new ToggleGroup();

            String choices[] = p.getChoices();
            int len = choices.length;

            for (int i = 0; i < len; i++) {
                final String choice = choices[i];
                RadioButton btn = new RadioButton(choice);
                btn.setToggleGroup(group);
                hbox.getChildren().add(btn);
                if (p.getValue().equals(choice))
                    btn.setSelected(true);
                if (p.getTooltip().length() > 0)
                    btn.setTooltip(new Tooltip(p.getTooltip()));
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent evt) {
                        p.setValue(choice);
                    }
                });
            }
        }
    }
}