

package org.bdigi.core;


import org.bdigi.core.mode.Mode;

import java.util.ArrayList;

public class Property {

    public static interface Control {
        String getName();
        String getTooltip();
    }

    private Mode mode;
    private String name;
    private String tooltip;
    private ArrayList<Control> xs;

    public Property(String name, String tooltip) {
        this.name = name;
        this.tooltip = tooltip;
        xs = new ArrayList<Control>();
    }
    public void setMode(Mode m) {
        mode = m;
    }
    public String getName() { return name; }
    public String getTooltip() { return tooltip; }
    public Control[] getControls() {
            return xs.toArray(new Control[0]);
    }
    public Property bool(String name, String tooltip, boolean value) {
        xs.add(new Boolean(name, tooltip, value));
        return this;
    }

    public Property radio(String name, String tooltip, String value, String... choices) {
        xs.add(new Radio(name, tooltip, value, choices));
        return this;
    }


    public class Boolean implements Control {
        private String name;
        private String tooltip;
        private boolean value;

        public Boolean(String name, String tooltip, boolean value) {
            this.name = name;
            this.tooltip = tooltip;
            this.value = value;
        }
        public String getName() { return name; }
        public String getTooltip() { return tooltip; }
        public boolean getValue() {
            return value;
        }
        
        public void setValue(boolean v) {
            value = v;
            mode.booleanControl(name, v);
        }
        
    }


    public class Radio implements Control {
        private String name;
        private String tooltip;
        private String value;
        private String choices[];

        public Radio(String name, String tooltip,
            String value, String... choices) {
            this.name = name;
            this.tooltip = tooltip;
            this.value = value;
            this.choices = choices;
        }
        public String getName() { return name; }
        public String getTooltip() { return tooltip; }
        public String getValue() {
            return value;
        }
        public String[] getChoices() { return choices; }
        
        public void setValue(String v) {
            value = v;
            mode.radioControl(name, v);
        }
        
    }



}
