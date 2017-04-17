package com.rsscale.client;

import com.rsscale.Boot;

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Project: RSPSScale
 *
 * @author Caleb Whiting
 */
public class IAppletStub implements AppletStub, AppletContext {

	private final Map<String, InputStream> streams = new HashMap<>();
	private final Properties properties;

	public IAppletStub(String propertiesPath) throws IOException {
		properties = new Properties();
		if (propertiesPath != null) {
			try (InputStream in = Boot.getResource(propertiesPath)) {
				properties.load(in);
			}
		}
	}

	@Override
	public AudioClip getAudioClip(URL url) {
		return Applet.newAudioClip(url);
	}

	@Override
	public Image getImage(URL url) {
		try {
			return ImageIO.read(url);
		} catch (IOException e) {
			throw new RuntimeException(url.toExternalForm());
		}
	}

	@Override
	public Applet getApplet(String name) {
		System.out.println("getApplet(" + name + ");");
		throw new RuntimeException();
	}

	@Override
	public Enumeration<Applet> getApplets() {
		System.out.println("getApplets();");
		throw new RuntimeException();
	}

	@Override
	public void showDocument(URL url) {

	}

	@Override
	public void showDocument(URL url, String target) {

	}

	@Override
	public void showStatus(String status) {

	}

	@Override
	public void setStream(String key, InputStream stream) throws IOException {
		streams.put(key, stream);
	}

	@Override
	public InputStream getStream(String key) {
		return streams.get(key);
	}

	@Override
	public Iterator<String> getStreamKeys() {
		return streams.keySet().iterator();
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public URL getDocumentBase() {
		try {
			String codebase = getParameter("codebase");
			return new URL(codebase);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public URL getCodeBase() {
		return getDocumentBase();
	}

	@Override
	public String getParameter(String name) {
		String value = properties.getProperty(name);
		if (value != null) {
			return value;
		}
		System.err.println("Parameter not found: " + name);
		return null;
	}

	@Override
	public AppletContext getAppletContext() {
		return this;
	}

	@Override
	public void appletResize(int width, int height) {

	}

}
