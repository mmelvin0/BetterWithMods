package betterwithmods.client.model.render;

import betterwithmods.BWMod;
import betterwithmods.client.model.filters.*;
import betterwithmods.common.BWMBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Function;

public class RenderUtils {
    protected static final Minecraft minecraft = Minecraft.getMinecraft();
    public static final Function<ResourceLocation, TextureAtlasSprite> textureGetter = ModelLoader.defaultTextureGetter();
    private static HashMap<String, ModelWithResource> filterLocations = new HashMap<>();
    private static RenderItem renderItem;

    public static String fromStack(ItemStack stack) {
        return stack.getItem().getRegistryName().toString() + ":" + stack.getMetadata();
    }

    public static boolean filterContains(ItemStack stack) {
        return !stack.isEmpty() && filterLocations.containsKey(fromStack(stack));
    }

    public static ModelWithResource getModelFromStack(ItemStack stack) {
        if (filterContains(stack))
            return filterLocations.get(fromStack(stack));
        return null;
    }

    public static void addFilter(ItemStack stack, ModelWithResource resource) {
        filterLocations.put(fromStack(stack), resource);
    }

    public static void registerFilters() {
        String[] woodTypes = {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"};
        for (int i = 0; i < 6; i++) {
            addFilter(new ItemStack(BWMBlocks.SLATS, 1, i), new ModelSlats(new ResourceLocation(BWMod.MODID, "textures/blocks/wood_side_" + woodTypes[i] + ".png")));
            addFilter(new ItemStack(BWMBlocks.GRATE, 1, i), new ModelGrate(new ResourceLocation(BWMod.MODID, "textures/blocks/wood_side_" + woodTypes[i] + ".png")));
        }
        addFilter(new ItemStack(BWMBlocks.WICKER, 1, 2), new ModelOpaque(new ResourceLocation(BWMod.MODID, "textures/blocks/wicker.png")));
        addFilter(new ItemStack(Blocks.SOUL_SAND), new ModelOpaque(new ResourceLocation("minecraft", "textures/blocks/soul_sand.png")));
        addFilter(new ItemStack(Blocks.IRON_BARS), new ModelTransparent(new ResourceLocation("minecraft", "textures/blocks/iron_bars.png")));
        addFilter(new ItemStack(Blocks.LADDER), new ModelTransparent(new ResourceLocation("minecraft", "textures/blocks/ladder.png")));
        addFilter(new ItemStack(Blocks.TRAPDOOR), new ModelTransparent(new ResourceLocation("minecraft", "textures/blocks/trapdoor.png")));
        addFilter(new ItemStack(Blocks.IRON_TRAPDOOR), new ModelTransparent(new ResourceLocation("minecraft", "textures/blocks/iron_trapdoor.png")));
    }

    public static void renderFill(ResourceLocation textureLocation, BlockPos pos, double x, double y, double z, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        renderFill(textureLocation, pos, x, y, z, minX, minY, minZ, maxX, maxY, maxZ, EnumFacing.VALUES);
    }

    public static void renderFill(ResourceLocation textureLocation, BlockPos pos, double x, double y, double z, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, EnumFacing[] facing) {
        Tessellator t = Tessellator.getInstance();
        BufferBuilder renderer = t.getBuffer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        int brightness = minecraft.world.getCombinedLight(pos, Math.max(5, minecraft.world.getLight(pos)));
        preRender(x, y, z);

        TextureAtlasSprite sprite = minecraft.getTextureMapBlocks().getTextureExtry(textureLocation.toString());
        for (EnumFacing f : facing)
            drawTexturedQuad(renderer, sprite, minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ, brightness, f);

        t.draw();
        postRender();
    }

    /*
    Everything from this point onward was shamelessly taken from Tinkers Construct. I'm sorry, but at some point, models are just too limited.
     */
    public static void preRender(double x, double y, double z) {
        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        } else
            GL11.glShadeModel(GL11.GL_FLAT);
        GlStateManager.translate(x, y, z);
    }

    public static void postRender() {
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }

    public static void drawTexturedQuad(BufferBuilder renderer, TextureAtlasSprite sprite, double x, double y, double z, double w, double h, double d, int brightness, EnumFacing facing) {
        if (sprite == null) {
            sprite = minecraft.getTextureMapBlocks().getMissingSprite();
        }
        int light1 = brightness >> 0x10 & 0xFFFF;
        int light2 = brightness & 0xFFFF;
        int r = 255;
        int g = 255;
        int b = 255;
        int a = 255;
        double minU;
        double maxU;
        double minV;
        double maxV;

        double size = 16F;

        double x2 = x + w;
        double y2 = y + h;
        double z2 = z + d;

        double xt1 = x % 1D;
        double xt2 = xt1 + w;
        while (xt2 > 1D) xt2 -= 1D;
        double yt1 = y % 1D;
        double yt2 = yt1 + h;
        while (yt2 > 1D) yt2 -= 1D;
        double zt1 = z % 1D;
        double zt2 = zt1 + d;
        while (zt2 > 1D) zt2 -= 1D;

        switch (facing) {
            case DOWN:
            case UP:
                minU = sprite.getInterpolatedU(xt1 * size);
                maxU = sprite.getInterpolatedU(xt2 * size);
                minV = sprite.getInterpolatedV(zt1 * size);
                maxV = sprite.getInterpolatedV(zt2 * size);
                break;
            case NORTH:
            case SOUTH:
                minU = sprite.getInterpolatedU(xt2 * size);
                maxU = sprite.getInterpolatedU(xt1 * size);
                minV = sprite.getInterpolatedV(yt1 * size);
                maxV = sprite.getInterpolatedV(yt2 * size);
                break;
            case WEST:
            case EAST:
                minU = sprite.getInterpolatedU(zt2 * size);
                maxU = sprite.getInterpolatedU(zt1 * size);
                minV = sprite.getInterpolatedV(yt1 * size);
                maxV = sprite.getInterpolatedV(yt2 * size);
                break;
            default:
                minU = sprite.getMinU();
                maxU = sprite.getMaxU();
                minV = sprite.getMinV();
                maxV = sprite.getMaxV();
        }

        switch (facing) {
            case DOWN:
                renderer.pos(x, y, z).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y, z).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x, y, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                break;
            case UP:
                renderer.pos(x, y2, z).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x, y2, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                break;
            case NORTH:
                renderer.pos(x, y, z).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x, y2, z).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y, z).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                break;
            case SOUTH:
                renderer.pos(x, y, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z2).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x, y2, z2).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                break;
            case WEST:
                renderer.pos(x, y, z).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x, y, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x, y2, z2).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x, y2, z).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                break;
            case EAST:
                renderer.pos(x2, y, z).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z2).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                break;
        }
    }

    public static TextureAtlasSprite getSprite(ItemStack stack) {
        if (renderItem == null) {
            renderItem = Minecraft.getMinecraft().getRenderItem();
        }

        return renderItem.getItemModelWithOverrides(stack, null, null).getParticleTexture();
    }

    public static ResourceLocation getResourceLocation(ItemStack stack) {
        TextureAtlasSprite sprite = getSprite(stack);
        String iconLoc = sprite.getIconName();
        String domain = iconLoc.substring(0, iconLoc.indexOf(':')), resource = iconLoc.substring(iconLoc.indexOf(':') + 1, iconLoc.length());
        return new ResourceLocation(domain, "textures/" + resource + ".png");
    }

    public static int multiplyColor(int src, int dst) {
        int out = 0;
        for (int i = 0; i < 32; i += 8) {
            out |= ((((src >> i) & 0xFF) * ((dst >> i) & 0xFF) / 0xFF) & 0xFF) << i;
        }
        return out;
    }

    public static BakedQuad recolorQuad(BakedQuad quad, int color) {
        int c = DefaultVertexFormats.BLOCK.getColorOffset() / 4;
        int v = DefaultVertexFormats.BLOCK.getIntegerSize() / 4;
        int[] vertexData = quad.getVertexData();
        for (int i = 0; i < 4; i++) {
            vertexData[v * i + c] = RenderUtils.multiplyColor(vertexData[v * i + c], color);
        }
        return quad;
    }

    public static BufferedImage getTextureImage(ResourceLocation location) {
        try {
            ResourceLocation pngLocation = new ResourceLocation(location.getResourceDomain(), String.format("%s/%s%s", "textures", location.getResourcePath(), ".png"));
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(pngLocation);
            return TextureUtil.readBufferedImage(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static IModel getModel(ResourceLocation location) {
        try {
            return ModelLoaderRegistry.getModel(location);
        } catch (Exception e) {
            BWMod.logger.error("Model " + location.toString() + " is missing! THIS WILL CAUSE A CRASH!");
            e.printStackTrace();
            return null;
        }
    }
}
