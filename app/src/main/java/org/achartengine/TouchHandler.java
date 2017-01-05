/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.achartengine;

import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.RoundChart;
import org.achartengine.chart.XYChart;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.tools.Pan;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.Zoom;
import org.achartengine.tools.ZoomListener;

/**
 * The main handler of the touch events.
 */
public class TouchHandler implements ITouchHandler {
  /** The chart renderer. */
  private DefaultRenderer mRenderer;
  /** The old x coordinate. */
  private float oldX;
  /** The old y coordinate. */
  private float oldY;
  /** The old x2 coordinate. */
  private float oldX2;
  /** The old y2 coordinate. */
  private float oldY2;
  /** The zoom buttons rectangle. */
  private RectF zoomR = new RectF();
  /** The pan tool. */
  private Pan mPan;
  /** The zoom for the pinch gesture. */
  private Zoom mPinchZoom;
  /** The graphical view. */
  private GraphicalView graphicalView;
  private AbstractChart mChart;
  public static int orientation = 0;//1LEFT ; 2 RIGHT

  /**
   * Creates a new graphical view.
   * 
   * @param view the graphical view
   * @param chart the chart to be drawn
   */
  public TouchHandler(GraphicalView view, AbstractChart chart) {
	  mChart=chart;

    graphicalView = view;
    zoomR = graphicalView.getZoomRectangle();
    if (chart instanceof XYChart) {
      mRenderer = ((XYChart) chart).getRenderer();
    } else {
      mRenderer = ((RoundChart) chart).getRenderer();
    }
    if (mRenderer.isPanEnabled()) {
      mPan = new Pan(chart);
    }
    if (mRenderer.isZoomEnabled()) {
      mPinchZoom = new Zoom(chart, true, 1);
    }
  }
  
  
  public void move(float newX,float newY){
	  mPan.apply(oldX, oldY, newX, newY);
  }

  /**
   * Handles the touch event.
   * 
   * @param event the touch event
   */
  public static int isZoom=0;//1��zoonIn��2��zoomOut;
  private int lastOrientation = 0;
  public boolean handleTouch(MotionEvent event) {

	
    int action = event.getAction();
    
    if (mRenderer != null && action == MotionEvent.ACTION_MOVE) {
      if (oldX >= 0 || oldY >= 0) {
        float newX = event.getX(0);
        float newY = event.getY(0);
       
        	if(newX - oldX > 0){
        		orientation = 2;
        	}else{
        		orientation = 1; 
        	}
          String fx = orientation==2?"右":"左";
          Log.e("test", "方向："+fx);
       
        if (event.getPointerCount() > 1 && (oldX2 >= 0 || oldY2 >= 0) && mRenderer.isZoomEnabled()) {
          float newX2 = event.getX(1);
          float newY2 = event.getY(1);
          float pXY2=Math.abs(newX2-newX);
          float pXY=Math.abs(oldX2-oldX);
          
          if(pXY2>pXY){
        	  isZoom=1; 
          }else{
        	  isZoom=2; 
          }
          float newDeltaX = Math.abs(newX - newX2);
          float newDeltaY = Math.abs(newY - newY2);
          float oldDeltaX = Math.abs(oldX - oldX2);
          float oldDeltaY = Math.abs(oldY - oldY2);
          float zoomRate = 1;

          float tan1 = Math.abs(newY - oldY) / Math.abs(newX - oldX);
          float tan2 = Math.abs(newY2 - oldY2) / Math.abs(newX2 - oldX2);
          if (tan1 <= 0.25 && tan2 <= 0.25) {
        	 //Log.e("test", "Y1");
            // horizontal pinch zoom, |deltaY| / |deltaX| is [0 ~ 0.25], 0.25 is
            // the approximate value of tan(PI / 12)
            zoomRate = newDeltaX / oldDeltaX;
            applyZoom(zoomRate, Zoom.ZOOM_AXIS_X);
          } else if (tan1 >= 3.73 && tan2 >= 3.73) {
        	 // Log.e("test", "Y2");
            // pinch zoom vertically, |deltaY| / |deltaX| is [3.73 ~ infinity],
            // 3.732 is the approximate value of tan(PI / 2 - PI / 12)
            zoomRate = newDeltaY / oldDeltaY;
            applyZoom(zoomRate, Zoom.ZOOM_AXIS_Y);
          } else {
        	  //Log.e("test", "Y3");
            // pinch zoom diagonally
            if (Math.abs(newX - oldX) >= Math.abs(newY - oldY)) {
              zoomRate = newDeltaX / oldDeltaX;
            } else {
              zoomRate = newDeltaY / oldDeltaY;
            }
            applyZoom(zoomRate, Zoom.ZOOM_AXIS_XY);
          }
          
          oldX2 = newX2;
          oldY2 = newY2;
        } else if (mRenderer.isPanEnabled()) {
        	
        	if(AbstractChart.isMoveOut){
        		Log.e("test", "Touch: 1");
        		mPan.apply(oldX, oldY, newX, newY);
        	}else{
        		if(AbstractChart.sIndex==orientation){
        		 AbstractChart.isMoveOut=true;
				 if (AbstractChart.sIndex==2) {
					 move(oldX+20, oldY);
				}else if(AbstractChart.sIndex==1){
					 move(oldX-20, oldY);
				}
        		}
        	}
          oldX2 = 0;
          oldY2 = 0;
        }
        oldX = newX;
        oldY = newY;
        graphicalView.repaint();
        return true;
      }
    } else if (action == MotionEvent.ACTION_DOWN) {
    	isZoom=0;
      oldX = event.getX(0);
      oldY = event.getY(0);
      if (mRenderer != null && mRenderer.isZoomEnabled() && zoomR.contains(oldX, oldY)) {
        if (oldX < zoomR.left + zoomR.width() / 3) {
          graphicalView.zoomIn();
        } else if (oldX < zoomR.left + zoomR.width() * 2 / 3) {
          graphicalView.zoomOut();
        } else {
          graphicalView.zoomReset();

        }
        return true;
      }
    } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
    	oldX = 0;
      oldY = 0;
      oldX2 = 0;
      oldY2 = 0;
      if (action == MotionEvent.ACTION_POINTER_UP) {
        oldX = -1;
        oldY = -1;
      }
      
      isZoom=0;
    }
    return !mRenderer.isClickEnabled();
  }
  

  private void applyZoom(float zoomRate, int axis) {
	//Log.e("test",""+isZoom+"  _  "+ GraphicalView.mZoomOutEnable+"_"+GraphicalView.mZoomInEnable);
	if((isZoom==2 && GraphicalView.mZoomOutEnable) || (isZoom==1 && GraphicalView.mZoomInEnable)){
	  //if(graphicalView.mZoomInEnable && graphicalView.mZoomOutEnable){
		zoomRate = Math.max(zoomRate, 0.9f);
		zoomRate = Math.min(zoomRate, 1.1f);
		if (zoomRate > 0.9 && zoomRate < 1.1) {
			 if(AbstractChart.isMoveOut){
				 mPinchZoom.setZoomRate(zoomRate);
				 mPinchZoom.apply(axis);
			 }else{
				 AbstractChart.isMoveOut=true;
				 if (AbstractChart.sIndex==2) {
					 //left
					 move(oldX+20, oldY);
				}else if(AbstractChart.sIndex==1){
					//right
					move(oldX-20, oldY);
				}
				 
				
			 }
		}
	}
  }

  /**
   * Adds a new zoom listener.
   * 
   * @param listener zoom listener
   */
  public void addZoomListener(ZoomListener listener) {
    if (mPinchZoom != null) {
      mPinchZoom.addZoomListener(listener);
    }
  }

  /**
   * Removes a zoom listener.
   * 
   * @param listener zoom listener
   */
  public void removeZoomListener(ZoomListener listener) {
    if (mPinchZoom != null) {
      mPinchZoom.removeZoomListener(listener);
    }
  }

  /**
   * Adds a new pan listener.
   * 
   * @param listener pan listener
   */
  public void addPanListener(PanListener listener) {
    if (mPan != null) {
      mPan.addPanListener(listener);
    }
  }

  /**
   * Removes a pan listener.
   * 
   * @param listener pan listener
   */
  public void removePanListener(PanListener listener) {
    if (mPan != null) {
      mPan.removePanListener(listener);
    }
  }
}