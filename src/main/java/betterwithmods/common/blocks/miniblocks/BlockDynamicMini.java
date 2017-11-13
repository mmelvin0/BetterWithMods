package betterwithmods.common.blocks.miniblocks;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BWMBlock;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;

public class BlockDynamicMini extends BWMBlock {

    public static final IUnlistedProperty<MiniCacheInfo> MINI_CACHE_INFO = new UnlistedPropertyGeneric<>("info", MiniCacheInfo.class);

    public BlockDynamicMini() {
        super(Material.WOOD);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{MINI_CACHE_INFO});
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileDynamicMini tile = (TileDynamicMini) world.getTileEntity(pos);
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) super.getExtendedState(state, world, pos);
        if (tile != null) {
            return extendedBlockState.withProperty(MINI_CACHE_INFO, MiniCacheInfo.from(tile));
        }
        return extendedBlockState;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.SOLID;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileDynamicMini();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public Material getMaterial(IBlockState state) {
        if (state instanceof IExtendedBlockState) {
            MiniCacheInfo info = ((IExtendedBlockState) state).getValue(MINI_CACHE_INFO);
            if(info != null) {
                Material material = info.material;
                if (material != null)
                    return material;
            }
        }
        return super.getMaterial(state);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        ItemStack test = new ItemStack(BWMBlocks.DYNAMIC_MINI);
        NBTTagCompound tag = test.getTagCompound();
        if (tag == null)
            tag = new NBTTagCompound();
        tag.setTag("texture", new ItemStack(Blocks.PLANKS).serializeNBT());
        test.setTagCompound(tag);
        builder.add(test);
        items.addAll(builder.build());
        super.getSubBlocks(itemIn, items);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileDynamicMini) {
            ((TileDynamicMini) tile).onPlacedBy(placer, stack);
        }
    }
}