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



import org.bdigi.{App, Complex, Constants, DFft, MathUtil, Window}

import javafx.application.Platform
import javafx.animation.{Animation, KeyFrame, TimelineBuilder}
import javafx.util.Duration
import javafx.beans.value.{ChangeListener,ObservableValue}
import javafx.scene.layout.{AnchorPane, BorderPane, HBox,VBox,Pane}
import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.image.{ImageView,WritableImage,PixelFormat}
import javafx.scene.shape.{Rectangle}
import javafx.scene.paint.Color
import javafx.event.{ActionEvent, Event, EventHandler}
import javafx.scene.input.{KeyEvent,MouseEvent,ScrollEvent}

//########################################################################
//#    Tuning Panel.    A single canvas with three widgets
//########################################################################

public class TuningPanel extends AnchorPane {


    private Digi par;
    final static double minFreq = 0.0;
    final static double maxFreq = 2500.0;
	final static int initialWidth = 500;
	final static int initialHeight = 100;
	final static int TUNER_HEIGHT = 24;
	private Canvas canvas;
	private Context ctx;
	private WaterfallArea waterfallArea;
	private TunerArea tunerArea;
	private ScopeArea scopeArea;
    
    public TuningPanel(Digi par) {
        this.par = par;
		AnchorPane.setLeftAnchor(this, 0);
		AnchorPane.setTopAnchor(this, 0);
		AnchorPane.setRightAnchor(this, 0);
		AnchorPane.setBottomAnchor(this, 0);
        canvas = new Canvas(initialWidth, initialHeight);
		AnchorPane.setLeftAnchor(canvas, 0);
		AnchorPane.setTopAnchor(canvas, 0);
		AnchorPane.setRightAnchor(canvas, 0);
		AnchorPane.setBottomAnchor(canvas, 0);
		getChildren.add(canvas);
		ctx = canvas.getGraphicsContext2D();

		waterfall = new WaterfallArea(initialWidth, initialHeight - TUNER_HEIGHT);
		tuner = new TunerArea(initialWidth, TUNER_HEIGHT);
		scope = new ScopeArea(initialHeight - TUNER_HEIGHT, initialHeight - TUNER_HEIGHT);
		ChangeListener resizeListener = new ChangeListener<Number>() {

			public void changed(observableValue: ObservableValue[_ <: Number], oldval: Number, newval: Number){
				int w = (getWidth != 0) ? getWidth.toInt : 50;
				int h = (getHeight != 0) ? getHeight.toInt : 50;
				trace("w: " + w + "  h:" + h);
				canvas.setWidth(w);
				canvas.setHeight(h);
				waterfall = new WaterfallArea(w, h - TUNER_HEIGHT);
				tuner     = new TunerArea(w, TUNER_HEIGHT);
				scope     = new ScopeArea(h - TUNER_HEIGHT, h - TUNER_HEIGHT);
				}
		};
	
		widthProperty.addListener(resizeListener);
		heightProperty.addListener(resizeListener);
    }



    class Waterfall {
    
        private Image img;
        private int nrPix;
        private int pixels;
        private int lastRow;
        private PixelWriter writer;
        private int colors[];
        private int pslen;
        private int psIndices;
        private int psbuf[];
    
        public WaterfallArea(int width, int height) {
        
            img     = new WritableImage(width, height);
            nrPix   = width * height;
            pixels  = new int[nrPix];
            lastRow = nrPix - width;
            writer  = img.getPixelWriter();
            format  = PixelFormat.getIntArgbInstance();
            genColors();
        }
                
        /**
         * Make a palette. tweak this often
         */                 
        private void genColors() {
            colors = new int[256];
            for (int i=0 ; i<256 ; i++) {
				val r = (i < 170) ? 0 : (i-170) * 3;
				val g = (i <  85) ? 0 : (i < 170) ? (i-85) * 3 : 255;
				val b = (i <  85) ? i * 3 : 255;
				var col = 0xff;
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
                for (int i=0 ; i<width ; i++) {
                    psIndices[i] = i * pslen / width;
                }
            }
            int pix[] = pixels;
            System.arraycopy(pix, width, pix, 0, lastRow);
            int pixptr = lastRow;
            for (int i=0 ; i<width ; i++) {
                int p = psbuf[psIndices[i]];
                pix[pixptr++] = colors[p & 0xff];
            }
            //trace("iw:" + iwidth + "  ih:" + iheight + "  pix:" + pix.size + " pslen:" + pslen)
            writer.setPixels(0, 0, width, height, format, pix, 0, width);
            ctx.drawImage(img, 0.0, 0.0, width, height);
            }

        def update(int ps[]) {
            psbuf = ps.clone();
        }
                
    }//Waterfall


    class TunerArea {
    
        private int width;
        private int height;
        private double range;
    
        public TunerArea(int width, int height) {
            this.width = width;
            this.height = height;
            range = maxFreq - minFreq;
			addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					switch (evt.getEventType()) {
						case MouseEvent.MOUSE_CLICKED :
						     setFrequency(x2freq(evt.getX));
						     break;
						case MouseEvent.MOUSE_DRAGGED :
						    setFrequencu(x2freq(evt.getX));
						    break;
						default:
						}
					}
			});
			
			setOnScroll(new EventHandler<ScrollEvent>() {
				public void handle(ScrollEvent evt) {
					setFrequency((evt.getDeltaY > 0) ? freq + 1 : freq - 1);
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

        function getBandwidth() {
            return par.getBandwidth();
        }
            
        void redraw() {
            int top = (int)(getHeight() - height);
            
            ctx.setFill(Color.BLACK);
            ctx.fillRect(0, top, width.toInt, height.toInt);
            
            //draw the tickmarks
            double hzWidth   = width / range;
            int tickRes   = 25;
            double tickSpace = hzWidth * tickRes;
            int nrTicks = (int)(range / tickRes);
    
            for (int i=1 ; i<nrTicks ; i++) {
                int tick = i * tickRes;
                double hx = i * tickSpace;
                if (tick % 500 == 0) {
                    ctx.setFill(Color.GREEN);
                    ctx.fillRect(hx, top, 2.0, 10.0);
                    String str = String.format("%d", tick);
                    ctx.setFill(Color.CYAN);
                    ctx.fillText(str, hx-16.0, top+19.0);
                } else if (tick % 100 == 0) {
                    ctx.setFill(Color.GREEN);
                    ctx.fillRect(hx, top, 2.0, 5.0);
                } else {
                    ctx.setFill(Color.GREEN)
                    ctx.fillRect(hx, top, 2.0, 2.0)
                }
            }
    
            
            ctx.setFill(Color.GREEN);
            double fx = width * freq / range;
            ctx.fillRect(fx, 3, 1.0, getHeight-10);
            
            if (getBandwidth() > 0.0) {
                ctx.setFill(Color.RED);
                double lox = width * (freq - bw * 0.5) / range;
                ctx.fillRect(lox, top+5, 1.0, 10);
                double hix = width * (freq + bw * 0.5) / range;
                ctx.fillRect(hix, top+5, 1.0, 10);
            }
        }
            
        
        private double x2freq(double x) {
            return range * x / width;
            }
        
        
        
    }//Tuner
    

    class ScopeArea extends Canvas {

		private int BUFSIZE = 512
		private double buf[][];
		private int bufptr;
		private double lastx;
		private double lasty;
		private double vscale;
		private int timeScale;
		Context ctx;

        public ScopeArea(int width, int height) {
            super(width, height);
            buf = new double[0][2];
            butptr = 0;
            lastx = 0.0;
            lasty = 0.0;
            vscale = 10.0;
            timeScale = 2;

			ctx = canvas.getGraphicsContext2D();
		}
        
        //only call from javafx thread
        public void redraw() {
            int w   = width;
            double w2  = w * 0.5;
            int h   = height
            double h2  = h * 0.5;
            double x0  = w2
            double y0  = h2
            int ptr = bufptr;
            double x   = 0.0;
            double y   = 0.0;

            //crosshairs
            ctx.setFill(Color.BLACK);
            ctx.fillRect(0, 0, w, h);
            ctx.setStroke(Color.WHITE);
            ctx.strokeLine(0, h2,  w, h2);
            ctx.strokeLine(w2,  0, w2,  h);

            //the trace line
            ctx.setStroke(Color.YELLOW);
            for (int i=0 ; i<BUFSIZE ; i+=timeScale) {
                double v[] = buf[ptr];
                val vx = v[0];
                val vy = v[1];
                ptr = (ptr + 1) % BUFSIZE;
                x = x0 + vx * vscale;
                y = y0 + vy * vscale;
                ctx.strokeLine(lastx, lasty, x, y);
                lastx = x;
                lasty = y;
                }
            ctx.setFill(Color.RED);
            ctx.fillRect(x-2.0, y-2.0, 4.0, 4.0);
            }    

 
         public void update(double x, double y) {
            buf(bufPtr) = (x, y)
            bufPtr = (bufPtr + 1) % BUFSIZE;
            }
            
                  
    } //scope
    
    public void trace(String msg) {
        par.trace(msg);
    }
    
    public void error(String msg) {
        par.error(msg);
    }
    


    class Redrawer extends EventHandler<ActionEvent> {
        public void handle(javafx.event.ActionEvent event) {
            waterfall.redraw();
            tuner.redraw();
            scope.redraw();
        }       
    }
        
    private void start() {
        val oneFrameAmt = Duration.millis(70);
        var oneFrame = new KeyFrame(oneFrameAmt, new Redrawer)

		TimelineBuilder.create()
		   .cycleCount(Animation.INDEFINITE)
		   .keyFrames(oneFrame)
		   .build()
		   .play();
	}

    public void update(int ps[]) {
        waterfall.update(ps);
    }

    public void updateScope(double buf[][]) {
        scope.update(buf);
    }
        

}




