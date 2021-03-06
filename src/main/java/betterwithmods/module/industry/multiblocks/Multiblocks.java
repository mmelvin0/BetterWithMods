package betterwithmods.module.industry.multiblocks;

import betterwithmods.api.tile.multiblock.IMultiblock;
import betterwithmods.common.blocks.BlockSteel;
import betterwithmods.module.Feature;
import com.google.common.collect.HashMultimap;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Multiblocks extends Feature {


    public static HashMultimap<IBlockState, IMultiblock> MULTIBLOCKS = HashMultimap.create();

    public Multiblocks() {
        canDisable = false;
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MULTIBLOCKS.put(BlockSteel.getBlock(8), new Lathe());
    }

    @SubscribeEvent
    public void onPlayerClick(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntityPlayer().isSneaking())
            return;
        IBlockState state = event.getWorld().getBlockState(event.getPos());
        if (MULTIBLOCKS.containsKey(state)) {
            for (IMultiblock m : MULTIBLOCKS.get(state)) {
                if (m.isValidStructure(event.getWorld(), event.getPos(), event.getFace())) {
                    m.createMultiBlock(event.getWorld(), event.getPos(), event.getFace());
                    return;
                }
            }
        }
    }
}
