package com.lefu.es.wheelview;

/**
 * Wheel changed listener interface.
 * <p>The currentItemChanged() method is called whenever current wheel positions is changed:
 * <li> New Wheel position is set
 * <li> Wheel view is scrolled
 */
public interface OnWheelChangedListener {
	void onChanged(WheelView wheel, int oldValue, int newValue);
}
