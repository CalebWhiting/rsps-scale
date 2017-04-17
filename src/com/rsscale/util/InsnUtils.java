package com.rsscale.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.util.ASMifier;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Caleb Whiting
 */
public class InsnUtils {

	public static String toString(AbstractInsnNode[] array) {
		String[] s = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			s[i] = array[i] == null ? "null" : toString(array[i]);
		}
		return Arrays.toString(s);
	}

	public static String toString(AbstractInsnNode node) {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("#").append(node.getIndex()).append(" ");
			Class c = node.getClass();
			Field[] fields = c.getDeclaredFields();
			int opcode = node.getOpcode();
			if (opcode != -1) {
				sb.append(getOpcodeName(opcode)).append(" ");
			}
			for (Field field : fields) {
				if (!Modifier.isStatic(field.getModifiers())) {
					field.setAccessible(true);
					Object value = field.get(node);
					sb.append(field.getName()).append("=").append(value).append(" ");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sb.toString().trim();
	}

	public static String getOpcodeName(int opcode) {
		return opcode == -1 ? "null" : ASMifier.OPCODES[opcode];
	}

	public static String getOpcodeOrTypeName(AbstractInsnNode node) {
		return node.getOpcode() != -1 ? getOpcodeName(node.getOpcode()) : getTypeName(node.getType());
	}

	public static String getTypeName(int type) {
		switch (type) {
			case AbstractInsnNode.INSN:
				return "INSN";
			case AbstractInsnNode.INT_INSN:
				return "INT_INSN";
			case AbstractInsnNode.VAR_INSN:
				return "VAR_INSN";
			case AbstractInsnNode.TYPE_INSN:
				return "TYPE_INSN";
			case AbstractInsnNode.FIELD_INSN:
				return "FIELD_INSN";
			case AbstractInsnNode.METHOD_INSN:
				return "METHOD_INSN";
			case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
				return "INVOKE_DYNAMIC_INSN";
			case AbstractInsnNode.JUMP_INSN:
				return "JUMP_INSN";
			case AbstractInsnNode.LABEL:
				return "LABEL";
			case AbstractInsnNode.LDC_INSN:
				return "LDC_INSN";
			case AbstractInsnNode.IINC_INSN:
				return "IINC_INSN";
			case AbstractInsnNode.TABLESWITCH_INSN:
				return "TABLESWITCH_INSN";
			case AbstractInsnNode.LOOKUPSWITCH_INSN:
				return "LOOKUPSWITCH_INSN";
			case AbstractInsnNode.MULTIANEWARRAY_INSN:
				return "MULTIANEWARRAY_INSN";
			case AbstractInsnNode.FRAME:
				return "FRAME";
			case AbstractInsnNode.LINE:
				return "LINE";
		}
		return null;
	}

	public static int getTypeForName(String typeName) {
		typeName = typeName.toUpperCase();
		if (typeName.endsWith("_INSN") || typeName.endsWith("-INSN")) {
			typeName = typeName.substring(0, typeName.length() - 5);
		}
		switch (typeName) {
			case "INSN":
				return AbstractInsnNode.INSN;
			case "INT":
				return AbstractInsnNode.INT_INSN;
			case "VAR":
				return AbstractInsnNode.VAR_INSN;
			case "TYPE":
				return AbstractInsnNode.TYPE_INSN;
			case "FIELD":
				return AbstractInsnNode.FIELD_INSN;
			case "METHOD":
				return AbstractInsnNode.METHOD_INSN;
			case "INVOKE_DYNAMIC":
				return AbstractInsnNode.INVOKE_DYNAMIC_INSN;
			case "JUMP":
				return AbstractInsnNode.JUMP_INSN;
			case "LABEL":
				return AbstractInsnNode.LABEL;
			case "LDC":
				return AbstractInsnNode.LDC_INSN;
			case "IINC":
				return AbstractInsnNode.IINC_INSN;
			case "TABLESWITCH":
				return AbstractInsnNode.TABLESWITCH_INSN;
			case "LOOKUPSWITCH":
				return AbstractInsnNode.LOOKUPSWITCH_INSN;
			case "MULTIANEWARRAY":
				return AbstractInsnNode.MULTIANEWARRAY_INSN;
			case "FRAME":
				return AbstractInsnNode.FRAME;
			case "LINE":
				return AbstractInsnNode.LINE;
		}
		return -1;
	}

	public static int getOpcodeForName(String opcodeName) {
		for (int j = 0; j < ASMifier.OPCODES.length; j++) {
			String name = ASMifier.OPCODES[j];
			if (name == null || !opcodeName.equalsIgnoreCase(name)) {
				continue;
			}
			return j;
		}
		return -1;
	}

	public static int getOpcodeType(int opcode) {
		if (opcode == Opcodes.LDC) {
			return AbstractInsnNode.LDC_INSN;
		}
		if (opcode == Opcodes.TABLESWITCH) {
			return AbstractInsnNode.TABLESWITCH_INSN;
		}
		if (opcode == Opcodes.LOOKUPSWITCH) {
			return AbstractInsnNode.LOOKUPSWITCH_INSN;
		}
		if (opcode == Opcodes.MULTIANEWARRAY) {
			return AbstractInsnNode.MULTIANEWARRAY_INSN;
		}
		if (opcode == Opcodes.IINC) {
			return AbstractInsnNode.IINC_INSN;
		}
		if (opcode == Opcodes.INVOKEDYNAMIC) {
			return AbstractInsnNode.INVOKE_DYNAMIC_INSN;
		}
		if (Arrays.binarySearch(Constants.INSN_OPCODES, opcode) >= 0) {
			return AbstractInsnNode.INSN;
		}
		if (Arrays.binarySearch(Constants.FIELD_OPCODES, opcode) >= 0) {
			return AbstractInsnNode.FIELD_INSN;
		}
		if (Arrays.binarySearch(Constants.INT_OPCODES, opcode) >= 0) {
			return AbstractInsnNode.INT_INSN;
		}
		if (Arrays.binarySearch(Constants.METHOD_OPCODES, opcode) >= 0) {
			return AbstractInsnNode.METHOD_INSN;
		}
		if (Arrays.binarySearch(Constants.JUMP_OPCODES, opcode) >= 0) {
			return AbstractInsnNode.JUMP_INSN;
		}
		if (Arrays.binarySearch(Constants.TYPE_OPCODES, opcode) >= 0) {
			return AbstractInsnNode.TYPE_INSN;
		}
		if (Arrays.binarySearch(Constants.VAR_OPCODES, opcode) >= 0) {
			return AbstractInsnNode.VAR_INSN;
		}
		return -1;
	}

	public interface Constants {

		int[] INSN_OPCODES = {
				NOP, ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5, LCONST_0,
				LCONST_1, FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD, LALOAD, FALOAD, DALOAD, AALOAD,
				BALOAD, CALOAD, SALOAD, IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE, POP,
				POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP, IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB,
				IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM, FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL,
				LSHL, ISHR, LSHR, IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR, I2L, I2F, I2D, L2I, L2F, L2D, F2I,
				F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S, LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN, FRETURN,
				DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW, MONITORENTER, MONITOREXIT
		};
		int[] FIELD_OPCODES = {
				GETSTATIC, PUTSTATIC, GETFIELD, PUTFIELD
		};
		int[] INT_OPCODES = {
				BIPUSH, SIPUSH, NEWARRAY
		};
		int[] JUMP_OPCODES = {
				IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE,
				IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL, IFNONNULL
		};
		int[] METHOD_OPCODES = {
				INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE
		};
		int[] TYPE_OPCODES = {
				NEW, ANEWARRAY, CHECKCAST, INSTANCEOF
		};
		int[] VAR_OPCODES = {
				ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE, RET
		};
	}
}
