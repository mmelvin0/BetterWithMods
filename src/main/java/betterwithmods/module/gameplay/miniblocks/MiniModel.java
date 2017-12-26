package betterwithmods.module.gameplay.miniblocks;

import betterwithmods.client.model.render.RenderUtils;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

public class MiniModel extends ModelFactory<MiniCacheInfo> {

    public static final MiniModel INSTANCE = new MiniModel();
    public IModel template;

    protected MiniModel() {
        super(BlockMini.MINI_INFO, TextureMap.LOCATION_MISSING_TEXTURE);
    }

    @Override
    public IBakedModel bake(MiniCacheInfo object, boolean isItem, BlockRenderLayer layer) {
        ImmutableMap.Builder<String, String> textures = new ImmutableMap.Builder<>();
        textures.put("side", object.texture.getIconName());
        IModelState state = object.orientation.toTransformation();
        IModel retexture = template.retexture(textures.build());
        return new WrappedBakedModel(retexture.bake(state, DefaultVertexFormats.BLOCK, RenderUtils.textureGetter), object.texture).addDefaultBlockTransforms();
    }

    @Override
    public MiniCacheInfo fromItemStack(ItemStack stack) {
        return MiniCacheInfo.from(stack);
    }
}
