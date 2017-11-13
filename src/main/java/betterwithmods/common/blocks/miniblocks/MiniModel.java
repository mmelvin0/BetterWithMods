package betterwithmods.common.blocks.miniblocks;

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
    public final static MiniModel SIDING = new MiniModel();
    public IModel template;
    protected MiniModel() {
        super(BlockDynamicMini.MINI_CACHE_INFO, TextureMap.LOCATION_MISSING_TEXTURE);
        addDefaultBlockTransforms();
    }

    @Override
    public IBakedModel bake(MiniCacheInfo object, boolean isItem, BlockRenderLayer layer) {
        ImmutableMap.Builder<String, String> textures = new ImmutableMap.Builder<>();
        if (isItem || layer == BlockRenderLayer.SOLID) {
            textures.put("side", object.texture.getIconName());
        } else {
            textures.put("#side", ""); textures.put("side", "");
        }
//        if (isItem || layer == BlockRenderLayer.TRANSLUCENT) {
//            textures.put("side", object.texture.getIconName());
//        } else {
//            textures.put("#side", ""); textures.put("side", "");
//        }

        IModelState state = object.orientation.toTransformation();
        IModel retexture = template.retexture(textures.build());

        IBakedModel baked = retexture.bake(state, DefaultVertexFormats.BLOCK, RenderUtils::getTextureSprite);
        return new WrappedBakedModel(baked, object.texture).addDefaultBlockTransforms();
    }

    @Override
    public MiniCacheInfo fromItemStack(ItemStack stack) {
        return MiniCacheInfo.from(stack);
    }
}
