package com.rsscale.util;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;

/**
 * Project: RSPSScale
 *
 * @author Caleb Whiting
 */
public class ClassUtil {

	public static Class<?> loadDescriptor(ClassLoader classLoader, String desc) throws ClassNotFoundException {
		Class c;
		if (desc.startsWith("[")) {
			c = Class.forName(desc, false, classLoader);
		} else if (desc.equals("I")) {
			c = int.class;
		} else if (desc.equals("J")) {
			c = long.class;
		} else if (desc.equals("C")) {
			c = char.class;
		} else if (desc.equals("F")) {
			c = float.class;
		} else if (desc.equals("D")) {
			c = double.class;
		} else if (desc.equals("S")) {
			c = double.class;
		} else if (desc.equals("B")) {
			c = byte.class;
		} else if (desc.equals("Z")) {
			c = boolean.class;
		} else {
			c = Class.forName(desc.substring(1, desc.length() - 1), false, classLoader);
		}
		return c;
	}

	public static ClassLoader createClassLoader(Collection<ClassNode> library) {
		return new ClassLoader(ClassLoader.getSystemClassLoader()) {
			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				for (ClassNode node : library) {
					if (node.name.replace('/', '.').equals(name)) {
						ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
						node.accept(cw);
						byte[] buffer = cw.toByteArray();
						return defineClass(name, buffer, 0, buffer.length);
					}
				}
				return super.findClass(name);
			}
		};
	}

}
