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


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.bdigi.core.Constants;
import org.bdigi.core.Digi;

import java.nio.IntBuffer;
import java.util.Arrays;

//########################################################################
//#    Tuning Panel.    A single canvas with three widgets
//########################################################################

public class TuningPanel extends AnchorPane {


    private Digi par;
    final double MIN_FREQ;
    final double MAX_FREQ;
    final static int initialWidth = 500;
    final static int initialHeight = 100;
    final static int TUNER_HEIGHT = 24;
    private Canvas canvas;
    private GraphicsContext ctx;
    private Waterfall waterfall;
    private TunerArea tuner;
    private ScopeArea scope;

    public TuningPanel(Digi par) {
        this.par = par;
        MIN_FREQ = 0.0;
        MAX_FREQ = par.getSampleRate() * 0.5;
        AnchorPane.setLeftAnchor(this, 0.);
        AnchorPane.setTopAnchor(this, 0.);
        AnchorPane.setRightAnchor(this, 0.);
        AnchorPane.setBottomAnchor(this, 0.);
        canvas = new Canvas(initialWidth, initialHeight);
        AnchorPane.setLeftAnchor(canvas, 0.);
        AnchorPane.setTopAnchor(canvas, 0.);
        AnchorPane.setRightAnchor(canvas, 0.);
        AnchorPane.setBottomAnchor(canvas, 0.);
        getChildren().add(canvas);
        ctx = canvas.getGraphicsContext2D();
        doLayout();
        ChangeListener<Number> listener = new ChangeListener<Number>() {

            public void changed(ObservableValue<? extends Number> value,
                                Number oldval, Number newval) {
                doLayout();
            }
        };

        widthProperty().addListener(listener);
        heightProperty().addListener(listener);
        start();
    }
    
    private void doLayout() {
        int w = (getWidth() > 50) ? (int) getWidth() : 50;
        int h = (getHeight() > 50) ? (int) getHeight() : 50;
        trace("w: " + w + "  h:" + h);
        canvas.setWidth(w);
        canvas.setHeight(h);
        waterfall = new Waterfall(w, h - TUNER_HEIGHT);
        tuner = new TunerArea(w, TUNER_HEIGHT);
        int scopesize = h-TUNER_HEIGHT;
        scope = new ScopeArea(w-scopesize,0, scopesize,scopesize);
    }


    class Waterfall {

        private int width;
        private int height;
        private WritableImage img;
        private int nrPix;
        private int pixels[];
        private int lastRow;
        private PixelWriter writer;
        private PixelFormat<IntBuffer> format;
        private int colors[];
        private int pslen;
        private int psIndices[];
        private double psbuf[];

        public Waterfall(int width, int height) {
            this.width = width;
            this.height = height;
            img = new WritableImage(width, height);
            nrPix = width * height;
            pixels = new int[nrPix];
            lastRow = nrPix - width;
            writer = img.getPixelWriter();
            format = PixelFormat.getIntArgbInstance();
            psbuf = new double[Constants.BINS];
            genColors();
        }

        /**
         * Make a palette. tweak this often
         */
        private void genColors() {
            colors = new int[256];
            for (int i = 0; i < 256; i++) {
                int r = (i < 170) ? 0 : (i - 170) * 3;
                int g = (i < 85) ? 0 : (i < 170) ? (i - 85) * 3 : 255;
                int b = (i < 85) ? i * 3 : 255;
                int col = 0xff;
                col = (col << 8) + r;
                col = (col << 8) + g;
                col = (col << 8) + b;
                colors[i] = col;
            }
        }


        //only call from javafx thread
        private void redraw() {
            if (pslen != psbuf.length) {
                pslen = psbuf.length;
                psIndices = new int[width];
                for (int i = 0; i < width; i++) {
                    psIndices[i] = i * pslen / width;
                }
            }
            int pix[] = pixels;
            System.arraycopy(pix, width, pix, 0, lastRow);
            int pixptr = lastRow;
            for (int i = 0; i < width; i++) {
                double v = psbuf[psIndices[i]];
                int p = (int)(Math.log(Math.abs(v) + 1.0) * 20.0);
                pix[pixptr++] = colors[p & 0xff];
            }
            //trace("iw:" + iwidth + "  ih:" + iheight + "  pix:" + pix.size + " pslen:" + pslen)
            writer.setPixels(0, 0, width, height, format, pix, 0, width);
            ctx.drawImage(img, 0.0, 0.0, width, height);
        }

        public void update(double ps[]) {
            psbuf = Arrays.copyOf(ps, ps.length);
        }

    }//Waterfall


    class TunerArea {

        private int width;
        private int height;
        private double range;

        public TunerArea(int width, int height) {
            this.width = width;
            this.height = height;
            range = MAX_FREQ - MIN_FREQ;
            addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                public void handle(MouseEvent evt) {
                    EventType typ = evt.getEventType();
                    if (typ == MouseEvent.MOUSE_CLICKED) {
                        setFrequency(x2freq(evt.getX()));
                    } else if (typ == MouseEvent.MOUSE_DRAGGED) {
                        setFrequency(x2freq(evt.getX()));
                    }
                }
            });

            setOnScroll(new EventHandler<ScrollEvent>() {
                public void handle(ScrollEvent evt) {
                    setFrequency((evt.getDeltaY() > 0) ?
                    getFrequency() + 1 : getFrequency() - 1);
                    redraw();
                }
            });
        }


        double getFrequency() {
            return par.getFrequency();
        }

        void setFrequency(double v) {
            par.setFrequency(v);
        }

        double getBandwidth() {
            return par.getBandwidth();
        }

        void redraw() {
            int top = (int) (getHeight() - height);

            ctx.setFill(Color.BLACK);
            ctx.fillRect(0, top, width, height);

            //draw the tickmarks
            double hzWidth = width / range;
            int tickRes = 25;
            double tickSpace = hzWidth * tickRes;
            int nrTicks = (int) (range / tickRes);

            for (int i = 1; i < nrTicks; i++) {
                int tick = i * tickRes;
                double hx = i * tickSpace;
                if (tick % 500 == 0) {
                    ctx.setFill(Color.GREEN);
                    ctx.fillRect(hx, top, 2.0, 10.0);
                    String str = String.format("%d", tick);
                    ctx.setFill(Color.CYAN);
                    ctx.fillText(str, hx - 16.0, top + 19.0);
                } else if (tick % 100 == 0) {
                    ctx.setFill(Color.GREEN);
                    ctx.fillRect(hx, top, 2.0, 5.0);
                } else {
                    ctx.setFill(Color.GREEN);
                    ctx.fillRect(hx, top, 2.0, 2.0);
                }
            }


            ctx.setFill(Color.GREEN);
            double fx = width * getFrequency() / range;
            ctx.fillRect(fx, 3, 1.0, getHeight() - 10);

            if (getBandwidth() > 0.0) {
                ctx.setFill(Color.RED);
                double lox = width * (getFrequency() - getBandwidth() * 0.5) / range;
                ctx.fillRect(lox, top + 5, 1.0, 10);
                double hix = width * (getFrequency() + getBandwidth() * 0.5) / range;
                ctx.fillRect(hix, top + 5, 1.0, 10);
            }
        }


        private double x2freq(double x) {
            return range * x / width;
        }


    }//Tuner


    class ScopeArea {

        private double buf[][];
        private double lastx;
        private double lasty;
        private double scale;
        private int timeScale;
        private int left;
        private int top;
        private int width;
        private int height;

        public ScopeArea(int left, int top, int width, int height) {
            this.left = left;
            this.top = top;
            this.width = width;
            this.height = height;
            buf = new double[0][2];
            lastx = 0.0;
            lasty = 0.0;
            scale = 0.5 * width;
            timeScale = 2;
        }

        //only call from javafx thread
        public void redraw() {
            double w2 = width * 0.5;
            double h2 = height * 0.5;
            double x0 = left + w2;
            double y0 = top + h2;
            double x = x0;
            double y = y0;

            //crosshairs
            ctx.setFill(Color.BLACK);
            ctx.fillRect(left, top, width, height);
            ctx.setStroke(Color.WHITE);
            ctx.strokeLine(left, y0, left+width, y0);
            ctx.strokeLine(x0, top, x0, top+height);

            //the trace line
            ctx.setStroke(Color.YELLOW);
            int len = buf.length;
            for (int i = 0; i < len; i += timeScale) {
                double v[] = buf[i];
                double vx = v[0];
                double vy = v[1];
                x = x0 + vx * scale;
                y = y0 + vy * scale;
                ctx.strokeLine(lastx, lasty, x, y);
                lastx = x;
                lasty = y;
            }
            ctx.setFill(Color.RED);
            ctx.fillRect(x - 2.0, y - 2.0, 4.0, 4.0);
        }


        public void update(double points[][]) {
            buf = points;
        }


    } //scope

    public void trace(String msg) {
        par.trace(msg);
    }

    public void error(String msg) {
        par.error(msg);
    }


    class Redrawer implements EventHandler<ActionEvent> {
        public void handle(javafx.event.ActionEvent event) {
            waterfall.redraw();
            tuner.redraw();
            scope.redraw();
        }
    }

    private void start() {
        Duration oneFrameAmt = Duration.millis(70);
        KeyFrame oneFrame = new KeyFrame(oneFrameAmt, new Redrawer());
        Timeline timeline = new Timeline(oneFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void updateSpectrum(double ps[]) {
        waterfall.update(ps);
    }

    public void updateScope(double buf[][]) {
        scope.update(buf);
    }


}




