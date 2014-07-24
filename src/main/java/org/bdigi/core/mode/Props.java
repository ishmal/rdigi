package org.bdigi.core.mode;

/**
 *
 */
public class Props {
    private String name;
    private String tooltip;
    private Control controls[];

    public Props(String name, String tooltip, Control controls[]) {
        this.name = name;
        this.tooltip = tooltip;
        this.controls = controls;
    }

    public String getName() {
        return name;
    }
    public String getTooltip() {
        return tooltip;
    }
    public Control[] getControls() {
        return controls;
    }

}
