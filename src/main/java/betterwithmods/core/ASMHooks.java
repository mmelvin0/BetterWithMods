package betterwithmods.core;

import betterwithmods.module.hardcore.world.HCBuoy;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ASMHooks {

    private final static byte BUOYANCY_MAX_ITERATIONS = 10;

    public static void updateBuoy(EntityItem entity) {
        double waterAccumulator = 0.0D;
        final double offset = 0.1D;


        for (int i = 0; i < BUOYANCY_MAX_ITERATIONS; ++i) {

            AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox();
            double low = entityBoundingBox.minY
                    + (entityBoundingBox.maxY - entityBoundingBox.minY) * (double) (i) * 0.375D + offset;
            double high = entityBoundingBox.minY
                    + (entityBoundingBox.maxY - entityBoundingBox.minY) * (double) (i + 1) * 0.375D + offset;
            AxisAlignedBB boundingBox = new AxisAlignedBB(entityBoundingBox.minX, low, entityBoundingBox.minZ,
                    entityBoundingBox.maxX, high, entityBoundingBox.maxZ);

            if (!isAABBInMaterial(entity.getEntityWorld(), boundingBox, Material.WATER)) {
                break;
            }

            waterAccumulator += 1.0D / (double) BUOYANCY_MAX_ITERATIONS;
        }

        if (waterAccumulator > 0.001D) {
            if (!isDrifted(entity.getEntityWorld(), entity.getEntityBoundingBox())) {
                float buoyancy = HCBuoy.getBuoyancy(entity.getItem()) + 1.0F;
                entity.motionY += 0.04D * (double) buoyancy * waterAccumulator;
            }

            entity.motionX *= 0.9;
            entity.motionY *= 0.9;
            entity.motionZ *= 0.9;
        }
    }

    /**
     * Checks if the given AABB is in the material given.
     * Was in {@link World} before 1.11 and then was deleted. No idea where it went so this is a copy.
     */
    private static boolean isAABBInMaterial(World world, AxisAlignedBB bb, Material materialIn) {
        int i = MathHelper.floor(bb.minX);
        int j = MathHelper.ceil(bb.maxX);
        int k = MathHelper.floor(bb.minY);
        int l = MathHelper.ceil(bb.maxY);
        int i1 = MathHelper.floor(bb.minZ);
        int j1 = MathHelper.ceil(bb.maxZ);
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    IBlockState iblockstate = world.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2));

                    Boolean result = iblockstate.getBlock().isAABBInsideMaterial(world, blockpos$pooledmutableblockpos, bb, materialIn);
                    if (result != null) return result;

                    if (iblockstate.getMaterial() == materialIn) {
                        int j2 = iblockstate.getValue(BlockLiquid.LEVEL);
                        double d0 = (double) (l1 + 1);

                        if (j2 < 8) {
                            d0 = (double) (l1 + 1) - (double) j2 / 8.0D;
                        }

                        if (d0 >= bb.minY) {
                            blockpos$pooledmutableblockpos.release();
                            return true;
                        }
                    }
                }
            }
        }

        blockpos$pooledmutableblockpos.release();
        return false;
    }

    /**
     * Check the non visible current between two water blocks for all blocks
     * nearby entity.
     */
    private static boolean isDrifted(World world, AxisAlignedBB box) {
        int minX = MathHelper.floor(box.minX);
        int maxX = MathHelper.floor(box.maxX + 1.0D);
        int minY = MathHelper.floor(box.minY);
        int maxY = MathHelper.floor(box.maxY + 1.0D);
        int minZ = MathHelper.floor(box.minZ);
        int maxZ = MathHelper.floor(box.maxZ + 1.0D);

        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                for (int z = minZ; z < maxZ; ++z) {
                    if (checkBlockDrifting(world, x, y, z))
                        return true;
                }
            }
        }

        return false;
    }

    /**
     * Check the non visible current between two water blocks.
     */
    private static boolean checkBlockDrifting(World world, int x, int y, int z) {
        for (int height = y - 1; height <= y + 1; height++) {
            IBlockState blockState = world.getBlockState(new BlockPos(x, height, z));
            if (blockState.getBlock() == Blocks.FLOWING_WATER || blockState.getBlock() == Blocks.WATER) {
                int meta = blockState.getBlock().getMetaFromState(blockState);
                if (meta >= 8)
                    return true;
            }
        }
        return false;
    }
}
