package betterwithmods.client;

import betterwithmods.BWMItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BWCreativeTabs {
    public static final CreativeTabs BWTAB = new CreativeTabs("bwm:bwTab") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(BWMItems.WINDMILL);
        }
    };
}
