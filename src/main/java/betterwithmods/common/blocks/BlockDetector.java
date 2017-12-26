package betterwithmods.common.blocks;

import betterwithmods.common.BWMBlocks;
import betterwithmods.util.DirUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockDetector extends BlockRotate {
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockDetector() {
        super(Material.ROCK);
        this.setHardness(3.5F);
        this.setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(DirUtils.FACING, EnumFacing.NORTH).withProperty(ACTIVE, false));
        this.setTickRandomly(true);
        this.setHarvestLevel("pickaxe", 0);
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public int tickRate(World world) {
        return 4;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float flX, float flY, float flZ, int meta, EntityLivingBase entity, EnumHand hand) {
        IBlockState state = super.getStateForPlacement(world, pos, side, flX, flY, flZ, meta, entity, hand);
        return setFacingInBlock(state, DirUtils.convertEntityOrientationToFacing(entity, side));
    }


    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack, @Nullable EnumFacing face, float hitX, float hitY, float hitZ) {
        super.onBlockPlacedBy(world, pos, state, placer, stack, face, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world,pos,state,entity,stack);
        EnumFacing facing = DirUtils.convertEntityOrientationToFacing(entity, EnumFacing.NORTH);
        setFacingInBlock(state, facing);

    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        setBlockOn(world, pos, false);
        world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos other) {
        world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        boolean blockDetection = detectBlock(world, pos);
        boolean detected = checkDetection(state, world, pos);

        EnumFacing facing = getFacing(world.getBlockState(pos));
        BlockPos offset = pos.offset(facing);

        if (world.isAirBlock(offset)) {
            if (!detected) {
                if (world.canBlockSeeSky(offset) && (world.isRaining() || world.isThundering())) {
                    if (world.getBiomeForCoordsBody(offset).canRain() || state.getValue(DirUtils.FACING) == EnumFacing.UP)
                        detected = true;
                }
            }
            world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
        } else if (blockDetection)
            detected = true;

        if (detected) {
            if (!isBlockOn(world, pos))
                setBlockOn(world, pos, true);
        } else if (isBlockOn(world, pos)) {
            if (!blockDetection)
                setBlockOn(world, pos, false);
            else
                world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
        }
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return state.getValue(ACTIVE);
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        if (isBlockOn(world, pos))
            return 15;
        return 0;
    }

    public EnumFacing getFacing(IBlockState state) {
        return state.getValue(DirUtils.FACING);
    }

    public IBlockState setFacingInBlock(IBlockState state, EnumFacing facing) {
        return state.withProperty(DirUtils.FACING, facing);
    }

    public boolean isBlockOn(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getValue(ACTIVE);
    }

    public void setBlockOn(World world, BlockPos pos, boolean on) {
        if (on != isBlockOn(world, pos)) {
            IBlockState state = world.getBlockState(pos).withProperty(ACTIVE, on);

            if (on) {
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.7F);
            }

            world.setBlockState(pos, state);

            for (int i = 0; i < 6; i++) {
                world.neighborChanged(pos.offset(EnumFacing.getFront(i)), this, pos);
            }
        }
    }

    public boolean detectBlock(World world, BlockPos pos) {
        BlockPos offset = pos.offset(getFacing(world.getBlockState(pos)));
        Block target = world.getBlockState(offset).getBlock();

        if (world.isAirBlock(offset) && (world.getBiomeForCoordsBody(offset).canRain() && world.canBlockSeeSky(offset) && (world.isRaining() || world.isThundering()))) {
            return true;
        }
        if (target == BWMBlocks.LENS) {
            BlockLens lens = (BlockLens) target;
            if (lens.getFacing(world.getBlockState(offset)) == DirUtils.getOpposite(getFacing(world.getBlockState(pos))) && lens.isLit(world, offset))
                return true;
        } else if (world.getBlockState(offset).getMaterial().isSolid())
            return true;
        else if (!world.getBlockState(offset).isOpaqueCube() && !world.isAirBlock(offset)) {
            int x = offset.getX();
            int y = offset.getY();
            int z = offset.getZ();
            AxisAlignedBB collisionBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
            List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, collisionBox);
            world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
            if (entityList.size() > 0)
                return true;
        }
        return false;
    }

    public boolean checkDetection(IBlockState state, World world, BlockPos pos) {
        BlockPos offset = pos.offset(getFacing(world.getBlockState(pos)));

        if (world.isAirBlock(offset)) {
            int x = offset.getX();
            int y = offset.getY();
            int z = offset.getZ();
            AxisAlignedBB collisionBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
            List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, collisionBox);
            entityList.removeIf(e -> e.isDead); // remove dead entities, prevents dead moving pulley blocks from triggering it
            entityList.removeIf(e -> !(e instanceof EntityItem) && !(e instanceof EntityLivingBase)); // remove anything that isn't an item or a mob/player
            if (entityList.size() > 0)
                return true;
            BlockPos below = offset.offset(EnumFacing.DOWN);
            if (world.getBlockState(below).getBlock() instanceof BlockCrops && !(world.getBlockState(below).getBlock() instanceof BlockHemp) && world.getBlockState(below).getBlock().getMetaFromState(world.getBlockState(below)) >= ((BlockCrops) world.getBlockState(below).getBlock()).getMaxAge()) {
                return true;
            } else if (world.getBlockState(offset).getBlock() == BWMBlocks.LIGHT_SOURCE && ((BlockInvisibleLight) world.getBlockState(offset).getBlock()).getFacing(world.getBlockState(offset)) == getFacing(world.getBlockState(pos))) {
                return true;
            }
        } else {
            if (world.getBlockState(offset).getBlock() instanceof BlockCrops && world.getBlockState(offset).getBlock().getMetaFromState(world.getBlockState(offset)) >= ((BlockCrops) world.getBlockState(offset).getBlock()).getMaxAge()) {
                return true;
            } else if (world.getBlockState(offset).getBlock() == Blocks.NETHER_WART && world.getBlockState(offset).getBlock().getMetaFromState(world.getBlockState(offset)) >= 3)
                return true;
            else if (world.getBlockState(offset).getBlock() == Blocks.REEDS)
                return true;
            else if (world.getBlockState(offset).getBlock() instanceof BlockVine) {
                if (state.getValue(DirUtils.FACING) == EnumFacing.UP)
                    return true;
                else {
                    IBlockState vState = world.getBlockState(offset);
                    return vState.getValue(BlockVine.getPropertyFor(state.getValue(DirUtils.FACING).getOpposite()));
                }
            }
        }
        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean isActive = false;
        if (meta > 7) {
            isActive = true;
            meta -= 8;
        }
        return this.getDefaultState().withProperty(ACTIVE, isActive).withProperty(DirUtils.FACING, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(ACTIVE) ? 8 : 0;
        return meta + state.getValue(DirUtils.FACING).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DirUtils.FACING, ACTIVE);
    }

    @Override
    public void nextState(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state.withProperty(ACTIVE, false).cycleProperty(DirUtils.FACING));
    }
}
