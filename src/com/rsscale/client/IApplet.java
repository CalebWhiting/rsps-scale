package com.rsscale.client;

import com.rsscale.Boot;
import com.rsscale.EventTarget;
import com.rsscale.asm.Adapter;
import com.rsscale.asm.Processors;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Project: RSPSScale
 *
 * @author Caleb Whiting
 */
public class IApplet extends Applet implements EventTarget {

	public static IApplet instance;

	public IApplet() {
		super();
		instance = this;
		Boot.eventTarget = this;
		setPreferredSize(new Dimension(Boot.width, Boot.height));
		setMinimumSize(new Dimension(Boot.width, Boot.height));
		setMaximumSize(new Dimension(Boot.width, Boot.height));
		setBounds(0, 0, Boot.width, Boot.height);
	}

	@Override
	public Graphics getGraphics() {
		return Boot.renderer;
	}

	@Override
	public void processEvent(AWTEvent e) {
		super.processEvent(e);
	}

	@Adapter("Shell")
	public static void changeAppletSuperclasses() {
		Boot.library.values().forEach(c -> {
			if (c.superName.equals("java/applet/Applet")) {
				Processors.setSuperclass(c, IApplet.class.getName().replace('.', '/'));
			}
		});
	}


}
