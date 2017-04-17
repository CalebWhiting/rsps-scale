package com.rsscale;

import javax.swing.JPanel;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Project: RSPSScale
 *
 * @author Caleb Whiting
 */
public class Display extends JPanel {

	public Display() {
		super(null);
	}

	@Override
	protected void processEvent(AWTEvent evt) {
		super.processEvent(evt);
		if (evt instanceof MouseWheelEvent) {
			MouseWheelEvent e = (MouseWheelEvent) evt;
			int x = (int) (e.getX() / Boot.scale);
			int y = (int) (e.getY() / Boot.scale);
			evt = new MouseWheelEvent(e.getComponent(),
									  e.getID(),
									  e.getWhen(),
									  e.getModifiers(),
									  x,
									  y,
									  e.getClickCount(),
									  e.isPopupTrigger(),
									  e.getScrollType(),
									  e.getScrollAmount(),
									  e.getWheelRotation());
		} else if (evt instanceof MouseEvent) {
			MouseEvent e = (MouseEvent) evt;
			int x = (int) (e.getX() / Boot.scale);
			int y = (int) (e.getY() / Boot.scale);
			evt = new MouseEvent(e.getComponent(),
								 e.getID(),
								 e.getWhen(),
								 e.getModifiers(),
								 x,
								 y,
								 e.getClickCount(),
								 e.isPopupTrigger(),
								 e.getButton());
		}
		if (Boot.eventTarget != null) {
			Boot.eventTarget.processEvent(evt);
		}
	}

}
