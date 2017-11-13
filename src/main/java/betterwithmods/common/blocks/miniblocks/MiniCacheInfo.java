package betterwithmods.common.blocks.miniblocks;

import betterwithmods.client.model.render.RenderUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;

public class MiniCacheInfo implements IRenderComparable<MiniCacheInfo> {
    final TextureAtlasSprite texture;
    final Orientation orientation;
    final Material material;


    public MiniCacheInfo(TextureAtlasSprite texture, Orientation orientation, Material material) {
        this.texture = texture;
        this.orientation = orientation;
        this.material = material;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MiniCacheInfo cacheInfo = (MiniCacheInfo) o;

        if (material != cacheInfo.material) return false;
        if (!texture.equals(cacheInfo.texture)) return false;
        return orientation == cacheInfo.orientation;
    }

    @Override
    public int hashCode() {
        int result = texture.hashCode();
        result = 31 * result + orientation.hashCode();
        result = 31 * result + material.hashCode();
        return result;
    }

    @Override
    public boolean renderEquals(MiniCacheInfo other) {
        return equals(other);
    }

    @Override
    public int renderHashCode() {
        return hashCode();
    }

    public static MiniCacheInfo from(TileDynamicMini tile) {
        return new MiniCacheInfo(RenderUtils.getSprite(tile.texture),tile.orientation,tile.material);
    }

    public static MiniCacheInfo from(ItemStack stack) {
        TileDynamicMini tile = new TileDynamicMini();
        tile.loadFromStack(stack);
        tile.orientation = new Orientation();
        return from(tile);
    }
}
