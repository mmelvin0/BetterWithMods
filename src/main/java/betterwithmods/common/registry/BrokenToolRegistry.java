package betterwithmods.common.registry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * HCLumber and HCPiles try to give appropriate output if the appropriate tool is used
 * They do this by checking the tool in your main hand on HarvestDropsEvent.
 * Unfortunately by the time HarvestDropsEvent occurs, the "appropriate tool" has been destroyed.
 *
 * This registry keeps track of the last destroyed item in each player's main hand until the next server tick.
 * HCLumber and HCPiles have been modified accordingly to check this registry if the main hand appears empty.
 *
 * It tries to be efficient about it by only registering for a tick event as needed.
 */
public class BrokenToolRegistry
{

    public static final Map<UUID, ItemStack> destroyed = new HashMap<>();
    private static final Ticker ticker = new Ticker();

    public static void init()
    {
        MinecraftForge.EVENT_BUS.register(BrokenToolRegistry.class);
    }

    public static ItemStack getDestroyedItem(EntityPlayer player)
    {
        return destroyed.getOrDefault(player.getUniqueID(), ItemStack.EMPTY);
    }

    @SubscribeEvent
    public static void onPlayerDestroyItem(PlayerDestroyItemEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || player.getEntityWorld().isRemote || event.getHand() != EnumHand.MAIN_HAND) {
            return;
        }
        ItemStack item = event.getOriginal();
        if (item.isEmpty()) {
            return;
        }
        destroyed.put(player.getUniqueID(), item);
        MinecraftForge.EVENT_BUS.register(ticker);
    }

    public static class Ticker
    {

        @SubscribeEvent
        public void onTick(TickEvent.ServerTickEvent event)
        {
            destroyed.clear();
            MinecraftForge.EVENT_BUS.unregister(this);
        }

    }

}
