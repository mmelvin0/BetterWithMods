

/**
 * This class was created by <Vazkii>. It's distributed as
 * part of Better With Mods. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [26/03/2016, 21:31:04 (GMT)]
 */
package betterwithmods.core;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class ClassTransformer implements IClassTransformer {

    private static final String ASM_HOOKS = "betterwithmods/core/ASMHooks";

    public static final ClassnameMap CLASS_MAPPINGS = new ClassnameMap(
            "net/minecraft/entity/item/EntityItem", "acj"
    );

    private static final Map<String, Transformer> transformers = new HashMap();

    static {
        transformers.put("net.minecraft.entity.item.EntityItem", ClassTransformer::transformEntityItem);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformers.containsKey(transformedName))
            return transformers.get(transformedName).apply(basicClass);

        return basicClass;
    }


    private static byte[] transformEntityItem(byte[] basicClass) {

        MethodSignature sig = new MethodSignature("onUpdate", "func_70071_h_", "B_", "()V");
        return transform(basicClass, Pair.of(sig, combine(anode -> {
            if (anode.getOpcode() == Opcodes.PUTFIELD) {
                System.out.println(anode.getClass());
                FieldInsnNode node = (FieldInsnNode) anode;
                if ((node.owner + "." + node.name).equalsIgnoreCase("net/minecraft/entity/item/EntityItem.motionY"))
                    return true;
            }

            return false;
        }, (method, anode) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "updateBuoy", "(Lnet/minecraft/entity/item/EntityItem;)V"));
            method.instructions.insert(anode.getNext(), newInstructions);
            return true;
        })));
    }
    // BOILERPLATE BELOW ==========================================================================================================================================

    private static byte[] transform(byte[] basicClass, Pair<MethodSignature, MethodAction>... methods) {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        boolean didAnything = false;

        for (Pair<MethodSignature, MethodAction> pair : methods) {
            log("Applying Transformation to method (" + pair.getLeft() + ")");
            didAnything |= findMethodAndTransform(node, pair.getLeft(), pair.getRight());
        }

        if (didAnything) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);
            return writer.toByteArray();
        }

        return basicClass;
    }

    public static boolean findMethodAndTransform(ClassNode node, MethodSignature sig, MethodAction pred) {
        String funcName = sig.funcName;
        if (LoadingPlugin.runtimeDeobfEnabled)
            funcName = sig.srgName;

        for (MethodNode method : node.methods) {
            if ((method.name.equals(funcName) || method.name.equals(sig.obfName) || method.name.equals(sig.srgName)) && (method.desc.equals(sig.funcDesc) || method.desc.equals(sig.obfDesc))) {
                log("Located Method, patching...");

                boolean finish = pred.test(method);
                log("Patch result: " + finish);

                return finish;
            }
        }

        log("Failed to locate the method!");
        return false;
    }

    public static MethodAction combine(NodeFilter filter, NodeAction action) {
        return (MethodNode mnode) -> applyOnNode(mnode, filter, action);
    }

    public static boolean applyOnNode(MethodNode method, NodeFilter filter, NodeAction action) {
        Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

        boolean didAny = false;
        while (iterator.hasNext()) {
            AbstractInsnNode anode = iterator.next();
            if (filter.test(anode)) {
                log("Located patch target node " + getNodeString(anode));
                didAny = true;
                if (action.test(method, anode))
                    break;
            }
        }

        return didAny;
    }

    private static void log(String str) {
        FMLLog.info("[BetterWithMods ASM] %s", str);
    }

    private static void prettyPrint(AbstractInsnNode node) {
        log(getNodeString(node));
    }

    private static String getNodeString(AbstractInsnNode node) {
        Printer printer = new Textifier();

        TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
        node.accept(visitor);

        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();

        return sw.toString().replaceAll("\n", "").trim();
    }

    private static boolean checkDesc(String desc, String expected) {
        return desc.equals(expected) || desc.equals(MethodSignature.obfuscate(expected));
    }

    private static boolean hasOptifine(String msg) {
        try {
            if (Class.forName("optifine.OptiFineTweaker") != null) {
                log("Optifine Detected. Disabling Patch for " + msg);
                return true;
            }
        } catch (ClassNotFoundException e) {
        }
        return false;
    }

    private static class MethodSignature {
        String funcName, srgName, obfName, funcDesc, obfDesc;

        public MethodSignature(String funcName, String srgName, String obfName, String funcDesc) {
            this.funcName = funcName;
            this.srgName = srgName;
            this.obfName = obfName;
            this.funcDesc = funcDesc;
            this.obfDesc = obfuscate(funcDesc);
        }

        @Override
        public String toString() {
            return "Names [" + funcName + ", " + srgName + ", " + obfName + "] Descriptor " + funcDesc + " / " + obfDesc;
        }

        private static String obfuscate(String desc) {
            for (String s : CLASS_MAPPINGS.keySet())
                if (desc.contains(s))
                    desc = desc.replaceAll(s, CLASS_MAPPINGS.get(s));

            return desc;
        }

    }

    // Basic interface aliases to not have to clutter up the code with generics over and over again
    private interface Transformer extends Function<byte[], byte[]> {
    }

    private interface MethodAction extends Predicate<MethodNode> {
    }

    private interface NodeFilter extends Predicate<AbstractInsnNode> {
    }

    private interface NodeAction extends BiPredicate<MethodNode, AbstractInsnNode> {
    }

}