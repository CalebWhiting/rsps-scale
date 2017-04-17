package com.rsscale.client;

import com.rsscale.Boot;
import com.rsscale.EventTarget;
import com.rsscale.asm.Adapter;
import com.rsscale.asm.Processors;

import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Project: RSPSScale
 *
 * @author Caleb Whiting
 */
public class ICanvas extends Canvas implements EventTarget {

	public static ICanvas instance;

	public ICanvas() {
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

	@Adapter("Canvas")
	public static void changeCanvasSuperclasses() {
		Boot.library.values().forEach(c -> {
			if (c.superName.equals("java/awt/Canvas")) {
				Processors.setSuperclass(c, ICanvas.class.getName().replace('.', '/'));
			}
		});
	}

}
