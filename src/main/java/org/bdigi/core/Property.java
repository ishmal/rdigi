

package org.bdigi.core;


public class Property {

    public static interface Control {
        String getName();
        String getTooltip();
    }

    public static class Mode {
        private String name;
        private String tooltip;
        private Control controls[];

        public Mode(String name, String tooltip, Control... controls) {
            this.name = name;
            this.tooltip = tooltip;
            this.controls = controls;
        }
        public String getName() { return name; }
        public String getTooltip() { return tooltip; }
        public Control[] getControls() {
            return controls;
        }
    }

    public static class Boolean implements Control {
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
            changed(v);
        }
        
        public void changed(boolean v) {
        }
    }


    public static class Radio implements Control {
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
            changed(v);
        }
        
        public void changed(String v) {
        }
    }

}
