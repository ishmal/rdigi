 
package org.bdigi.core;

import java.io.*;
import java.util.Properties;

/**
 * Simple POJO class with loader and saver
 */
 public class Config {
    private static String propFile = "bdigi.ini";
    private Digi par;

    /**
     * Fields
     */
    public String call;
    public String name;
    public String qth;
    public String locator;
    public String audioInput;
    public String audioOutput;
    
    
    public Config(Digi par) {
        this.par = par;
        call = name = qth = locator = audioInput = audioOutput = "";
    }

    public void load(InputStream ins) throws IOException {
        Properties p = new Properties();
        p.load(ins);        
        call        = p.getProperty("call");
        name        = p.getProperty("name");
        locator     = p.getProperty("locator");
        audioInput  = p.getProperty("audioInput");
        par.setAudioInput(audioInput);
        audioOutput = p.getProperty("audioOutput");
        par.setAudioOutput(audioOutput);
    }
    
    public boolean loadFile() {
        try {
            FileInputStream ins = new FileInputStream(propFile);
            load(ins);
            ins.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void save(OutputStream outs) throws IOException {
        Properties p = new Properties();
        p.setProperty("call", call);
        p.setProperty("name", name);
        p.setProperty("locator", locator);
        p.setProperty("audioInput", audioInput);
        p.setProperty("audioOutput", audioOutput);
        p.store(outs, "Bdigi properties");
    }

    public boolean saveFile() {
        try {
            FileOutputStream outs = new FileOutputStream(propFile);
            save(outs);
            outs.close();
            return true;
        } catch (IOException e) {
            return false;
        }
   
    
    }

}

