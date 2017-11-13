package betterwithmods.common.blocks.miniblocks;

import betterwithmods.common.blocks.tile.TileBasic;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileDynamicMini extends TileBasic {
    public ItemStack texture;
    public Material material;
    public Orientation orientation;


    public void loadFromStack(ItemStack is) {
        NBTTagCompound tag = is.getTagCompound();
        if (tag != null) {
            texture = new ItemStack(tag.getCompoundTag("texture"));
            material = Material.ROCK;
        }
    }

    public void onPlacedBy(EntityLivingBase placer, ItemStack stack) {
        loadFromStack(stack);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if(texture != null)
            compound.setTag("texture", texture.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        texture = new ItemStack((NBTTagCompound) compound.getTag("texture"));
        super.readFromNBT(compound);
    }
}
