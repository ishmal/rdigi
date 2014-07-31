package org.bdigi.core.audio;

/**
 * Created by Bob on 7/26/2014.
 */
public interface AudioInput {

    public double[] read();

    public boolean start();

    public boolean stop();
}
