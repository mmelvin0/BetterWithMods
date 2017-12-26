package betterwithmods.module.gameplay.miniblocks;

import betterwithmods.common.blocks.tile.TileBasic;
import betterwithmods.util.SpaceUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TileMini extends TileBasic {

    public ItemStack texture;
    public Orientation orientation;

    public TileMini() {
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = super.writeToNBT(compound);
        tag.setTag("texture", texture.serializeNBT());
        tag.setInteger("orientation", orientation.ordinal());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        texture = new ItemStack((NBTTagCompound) compound.getTag("texture"));
        orientation = Orientation.getOrientation(compound.getInteger("orientation"));
        super.readFromNBT(compound);
    }

    @Override
    public void onPlacedBy(EntityLivingBase placer, @Nullable EnumFacing face, ItemStack stack, float hitX, float hitY, float hitZ) {
        loadFromStack(stack);

        orientation = SpaceUtils.getOrientation(getWorld(), getPos(), placer, face, 0,0,0);
    }

    public void loadFromStack(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        texture = new ItemStack(tag.getCompoundTag("texture"));
    }

    private boolean changeOrientation(Orientation newOrientation, boolean simulate) {
        if (orientation != newOrientation) {
            if (!simulate) {
                orientation = newOrientation;
                markBlockForUpdate();
                getWorld().notifyNeighborsRespectDebug(pos, getBlockType(), true);
            }
            return true;
        } else {
            return false;
        }
    }

    public void markBlockForRenderUpdate() {
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    public void markBlockForUpdate() {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
    }
}
