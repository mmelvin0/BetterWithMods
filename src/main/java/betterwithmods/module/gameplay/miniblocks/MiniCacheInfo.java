package betterwithmods.module.gameplay.miniblocks;

import betterwithmods.client.model.render.RenderUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MiniCacheInfo implements IRenderComparable<MiniCacheInfo> {
    public final TextureAtlasSprite texture;
    public final Orientation orientation;

    final transient ItemStack block;

    public MiniCacheInfo(TextureAtlasSprite texture, Orientation orientation, ItemStack block) {
        this.texture = texture;
        this.orientation = orientation;
        this.block = block;
    }

    public static MiniCacheInfo from(TileMini mini) {
        return new MiniCacheInfo(RenderUtils.getSprite(mini.texture), mini.orientation, mini.texture);
    }

    public static MiniCacheInfo from(ItemStack mini) {
        NBTTagCompound tag = mini.getTagCompound();
        if(tag != null && tag.hasKey("texture")) {
            ItemStack texture = new ItemStack(tag.getCompoundTag("texture"));
            return new MiniCacheInfo(RenderUtils.getSprite(texture), Orientation.FACE_NORTH_POINT_UP, texture);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MiniCacheInfo cacheInfo = (MiniCacheInfo) o;
        if (!texture.equals(cacheInfo.texture)) return false;
        return orientation == cacheInfo.orientation;
    }

    @Override
    public boolean renderEquals(MiniCacheInfo other) {
        return equals(other);
    }

    @Override
    public int renderHashCode() {
        return 31 * texture.hashCode() + orientation.hashCode();
    }
}
