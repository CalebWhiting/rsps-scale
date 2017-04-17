package com.rsscale.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

/**
 * Project: RSPSScale
 *
 * @author Caleb Whiting
 */
public class Processors {

	private static final boolean DEBUG = true;

	public static void setSuperclass(ClassNode node, String name) {
		debug("[DEBUG] Set superclass of " + node.name + " to " + name);
		String old = node.superName;
		node.superName = name;
		for (MethodNode m : node.methods) {
			ListIterator<AbstractInsnNode> itr = m.instructions.iterator();
			while (itr.hasNext()) {
				AbstractInsnNode n = itr.next();
				if (n.getOpcode() == Opcodes.INVOKESPECIAL) {
					MethodInsnNode min = (MethodInsnNode) n;
					min.owner = min.owner.equals(old) ? name : min.owner;
				}
			}
		}
	}

	private static void debug(String s) {
		if (DEBUG) {
			System.out.println(s);
		}
	}

}
