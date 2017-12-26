package betterwithmods.module.hardcore.world;

import betterwithmods.module.Feature;
import betterwithmods.util.player.PlayerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;

public class HCBoating extends Feature {

    public static HashMap<Ingredient, Integer> SPEED_ITEMS;
    public static int defaultSpeed;

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        SPEED_ITEMS = loadItemStackIntMap("Speed Items", "Items which speed up a boat when held, value is a percentage of the vanilla speed", new String[]{
                "betterwithmods:material:11=100"
        });
        defaultSpeed = loadPropInt("Default Speed modifier", "Speed modifier when not holding any sail type item", 50);
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.world.isRemote)
            return;
        EntityPlayer player = event.player;
        Entity riding = player.getRidingEntity();
        if (riding instanceof EntityBoat) {

            ItemStack stack = PlayerHelper.getHolding(player);
            int speed = defaultSpeed;
            if (!stack.isEmpty())
                speed = SPEED_ITEMS.entrySet().stream().filter(e -> e.getKey().apply(stack)).mapToInt(Map.Entry::getValue).findAny().orElse(defaultSpeed);
            riding.motionX *= (speed / 100f);
            riding.motionZ *= (speed / 100f);
        }
    }
}
