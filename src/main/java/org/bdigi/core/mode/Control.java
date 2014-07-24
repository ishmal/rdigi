package org.bdigi.core.mode;

/**
 *
 */
public class Control {
    public final static int BOOLEAN = 0;
    public final static int RADIO = 1;




    private int type;
    private String name;
    private double value;
    private double values[];

    public Control(int type, String name, double value, double values[]) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.values = values;
    }


}
