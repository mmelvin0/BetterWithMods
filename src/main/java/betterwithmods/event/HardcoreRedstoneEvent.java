package betterwithmods.event;

import betterwithmods.config.BWConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by blueyu2 on 11/28/16.
 */
public class HardcoreRedstoneEvent {
    //TODO This caused some pretty severe crashes when testing mcmultipart, keep an eye on it.
    @SubscribeEvent
    public void disableRedstone(BlockEvent.NeighborNotifyEvent event) {
        if (!BWConfig.hardcoreRedstone)
            return;

        event.setCanceled(true);
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        for (EnumFacing facing : event.getNotifiedSides()) {
            IBlockState state = world.getBlockState(pos.offset(facing));
            Block block = state.getBlock();
            if (block instanceof BlockDoor || block instanceof BlockFenceGate || block instanceof BlockTrapDoor) {
                if (!state.getMaterial().equals(Material.IRON))
                    continue;
            }

            world.neighborChanged(pos.offset(facing), event.getState().getBlock(), pos);
            /*
            for (EnumFacing f1 : EnumFacing.VALUES) {
                if (f1 != facing.getOpposite())
                    world.neighborChanged(pos.offset(facing).offset(f1), event.getState().getBlock(), pos.offset(facing));
            }*/
        }
        if (event.getForceRedstoneUpdate()) {
            event.getWorld().updateObservingBlocksAt(pos, event.getState().getBlock());
        } else {
            IBlockState state = world.getBlockState(pos);
            boolean hasFacingProp = false;
            IProperty<EnumFacing> dir = null;
            for (IProperty<?> prop : state.getProperties().keySet()) {
                if (prop.getName().equals("facing") && prop.getValueClass().equals(EnumFacing.class)) {
                    dir = ((IProperty<EnumFacing>)prop);
                    hasFacingProp = true;
                }
                if (hasFacingProp)
                    break;
            }
            if (hasFacingProp) {
                EnumFacing face = state.getValue(dir);
                world.notifyNeighborsOfStateChange(pos.offset(face.getOpposite()), state.getBlock(), false);
            }
        }
    }
}
