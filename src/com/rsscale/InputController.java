package com.rsscale;

import java.awt.event.*;

/**
 * Project: RSPSScale
 *
 * @author Caleb Whiting
 */
public class InputController
		implements MouseListener, MouseMotionListener, MouseWheelListener, FocusListener, KeyListener {

	@Override
	public void focusGained(FocusEvent e) {

	}

	@Override
	public void focusLost(FocusEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.isControlDown() && e.getExtendedKeyCode() == KeyEvent.VK_PAGE_DOWN) {
			Boot.setScale(Boot.scale - 0.05);
		}
		if (e.isControlDown() && e.getExtendedKeyCode() == KeyEvent.VK_PAGE_UP) {
			Boot.setScale(Boot.scale + 0.05);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

	}
}
