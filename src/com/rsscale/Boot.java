package com.rsscale;

import com.rsscale.asm.Adapter;
import com.rsscale.client.IApplet;
import com.rsscale.client.IAppletStub;
import com.rsscale.util.MainArguments;
import com.sun.xml.internal.ws.org.objectweb.asm.Type;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Project: RSPSScale
 *
 * @author Caleb Whiting
 */
@SuppressWarnings({"FieldCanBeLocal", "unused", "WeakerAccess"})
public class Boot {

	public static final Map<String, ClassNode> library = new HashMap<>();
	public static final Map<String, byte[]> extra = new HashMap<>();
	/* Configuration */
	public static double scale;
	public static String path;
	public static String client;
	public static int width;
	public static int height;
	public static String properties;
	/* Variables */
	public static Graphics2D renderer;
	public static EventTarget eventTarget;
	public static JFrame frame;
	public static Applet applet;
	public static JPanel root;
	public static Manifest manifest;
	private static URLClassLoader classLoader;
	private static boolean modernApplet = false;

	public static void main(String[] args0) {
		MainArguments.setArguments(args0);
		scale = MainArguments.getDoubleValue("scale", 2D);
		path = MainArguments.getStringValue("path", "client.jar");
		client = MainArguments.getStringValue("client", "client");
		width = MainArguments.getIntegerValue("width", 765);
		height = MainArguments.getIntegerValue("height", 503);
		properties = MainArguments.getStringValue("properties", null);
		try {
			start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void start() throws Exception {
		try (JarInputStream in = new JarInputStream(getResource(path))) {
			manifest = in.getManifest();
			JarEntry entry;
			while ((entry = in.getNextJarEntry()) != null) {
				if (!entry.getName().endsWith(".class")) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					int n;
					byte[] buffer = new byte[128];
					while ((n = in.read(buffer)) != -1) {
						bos.write(buffer, 0, n);
					}
					extra.put(entry.getName(), bos.toByteArray());
					continue;
				}
				ClassNode node = new ClassNode();
				ClassReader reader = new ClassReader(in);
				reader.accept(node, ClassReader.EXPAND_FRAMES);
				library.put(node.name, node);
			}
		}
		runAdapters();
		/* Check if the client is "modern" */
		ClassNode clientClassNode = library.get(client.replace('.', '/'));
		while (clientClassNode != null) {
			clientClassNode.methods.stream()
								   .filter(m -> m.name.equals("supplyApplet"))
								   .forEach(m -> modernApplet = true);
			clientClassNode = library.get(clientClassNode.superName);
		}
		/* Modify the game so that it can be scaled */
		File temp = File.createTempFile("gamepack_", ".jar", new File(System.getProperty("java.io.tmpdir")));
		System.out.println("Temporary file: " + temp.getAbsolutePath());
		try (JarOutputStream out = new JarOutputStream(new FileOutputStream(temp))) {
			if (manifest != null) {
				out.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
				manifest.write(out);
				out.closeEntry();
			}
			for (ClassNode n : library.values()) {
				ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				n.accept(w);
				out.putNextEntry(new JarEntry(n.name + ".class"));
				out.write(w.toByteArray());
				out.closeEntry();
			}
			for (Map.Entry<String, byte[]> n : extra.entrySet()) {
				out.putNextEntry(new JarEntry(n.getKey()));
				out.write(n.getValue());
				out.closeEntry();
			}
		}
		//Files.copy(temp.toPath(), new File("client_dump.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
		classLoader = new URLClassLoader(new URL[]{temp.toURI().toURL()});
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				classLoader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (!temp.delete()) {
					System.err.println("Failed to delete temp file: " + temp);
				}
			}
		}));
		root = new Display();
		frame = new JFrame("RS-Scale");
		InputController adapter = new InputController();
		root.addMouseWheelListener(adapter);
		root.addMouseMotionListener(adapter);
		root.addMouseListener(adapter);
		root.addFocusListener(adapter);
		root.addKeyListener(adapter);
		root.setLayout(null);
		root.setFocusable(true);
		root.setFocusTraversalKeysEnabled(true);
		root.setRequestFocusEnabled(true);
		root.setPreferredSize(new Dimension((int) (scale * width), (int) (scale * height)));

		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setContentPane(root);
		frame.pack();
		frame.setVisible(true);

		renderer = (Graphics2D) root.getGraphics();
		renderer.setTransform(AffineTransform.getScaleInstance(scale, scale));
		renderer.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		renderer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderer.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		new Thread(Boot::startClient).start();
	}

	private static void startClient() {
		try {
			Class clientClass = classLoader.loadClass(client);
			if (modernApplet) {
				Object client = clientClass.newInstance();
				applet = new IApplet();
				applet.setStub(new IAppletStub(properties));
				invoke(client, "supplyApplet", "(Ljava/applet/Applet;)V", applet);
				invoke(client, "init", "()V");
				invoke(client, "start", "()V");
				applet.setSize(width, height);
			} else {
				applet = (Applet) clientClass.newInstance();
				try {
					invoke(client, "setHighMem", "()V");
				} catch (NoSuchMethodException ignore) {
					// either obfuscated, different name or client doesn't support memory modes
				}
				applet.setStub(new IAppletStub(properties));
				applet.init();
				applet.start();
				applet.setSize(width, height);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("There was an error loading the client");
			System.exit(0);
		}
	}

	private static void setField(Class c, String name, Object value) throws ReflectiveOperationException {
		Field f = c.getDeclaredField(name);
		f.setAccessible(true);
		f.set(null, value);
	}

	private static void runAdapters() throws Exception {
		Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(
				"com.rsscale")).setScanners(new MethodAnnotationsScanner()));
		Set<Method> adapters = reflections.getMethodsAnnotatedWith(Adapter.class);
		Set<String> visited = new HashSet<>();
		while (adapters.size() > 0) {
			for (Iterator<Method> iterator = adapters.iterator(); iterator.hasNext(); ) {
				Method method = iterator.next();
				Adapter annotation = method.getAnnotation(Adapter.class);
				if (visited.containsAll(Arrays.asList(annotation.dependencies()))) {
					method.invoke(null);
					iterator.remove();
					visited.add(annotation.value());
					System.out.println("Adapter completed: " + annotation.value());
				}
			}
		}
	}

	private static void updateScale() {
		if (scale < 1) {
			scale = 1;
		}
		if (scale > 4) {
			scale = 4;
		}
		renderer = (Graphics2D) root.getGraphics();
		renderer.setTransform(AffineTransform.getScaleInstance(scale, scale));
		renderer.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		renderer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderer.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		root.setPreferredSize(new Dimension((int) (scale * width), (int) (scale * height)));
		frame.setSize(1, 1);
		SwingUtilities.updateComponentTreeUI(frame);
		frame.pack();
	}

	public static InputStream getResource(String path) throws IOException {
		if (path.startsWith("http:") || path.startsWith("https:")) {
			URLConnection c = new URL(path).openConnection();
			c.setRequestProperty("User-Agent",
								 "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36");
			return c.getInputStream();
		}
		return new FileInputStream(path);
	}

	private static Object invoke(Object instance, String name, String desc, Object... args) throws
			ReflectiveOperationException {
		return invoke(instance.getClass(), instance, name, desc, args);
	}

	private static Object invoke(Class c, Object instance, String name, String desc, Object... args) throws
			ReflectiveOperationException {
		for (Method method : c.getDeclaredMethods()) {
			if (Type.getMethodDescriptor(method).equals(desc)) {
				method.setAccessible(true);
				return method.invoke(instance, args);
			}
		}
		if (c.getSuperclass() == Object.class) {
			throw new NoSuchMethodException(name + desc);
		}
		return invoke(c.getSuperclass(), instance, name, desc, args);
	}

	public static void setScale(double scale) {
		Boot.scale = scale;
		updateScale();
	}

}
