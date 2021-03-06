package betterwithmods.module.gameplay;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMItems;
import betterwithmods.common.blocks.BlockRawPastry;
import betterwithmods.common.blocks.mechanical.BlockCookingPot;
import betterwithmods.common.registry.blockmeta.managers.KilnManager;
import betterwithmods.module.Feature;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created by tyler on 5/16/17.
 */
public class KilnRecipes extends Feature {
    public KilnRecipes() {
        canDisable = false;
    }

    public static void addKilnRecipe(ItemStack inputBlock, ItemStack... output) {
        KilnManager.INSTANCE.addRecipe(inputBlock, output);
    }

    public static void addKilnRecipe(Block inputBlock, ItemStack... output) {
        KilnManager.INSTANCE.addRecipe(inputBlock, 0, output);
    }

    public static void addKilnRecipe(Block inputBlock, int inputMeta, ItemStack... output) {
        KilnManager.INSTANCE.addRecipe(inputBlock, inputMeta, output);
    }

       @Override
    public void init(FMLInitializationEvent event) {
        addKilnRecipe(BWMBlocks.UNFIRED_POTTERY, 0, new ItemStack(BWMBlocks.COOKING_POTS, 1, BlockCookingPot.EnumType.CRUCIBLE.getMeta()));
        addKilnRecipe(BWMBlocks.UNFIRED_POTTERY, 1, new ItemStack(BWMBlocks.PLANTER));
        addKilnRecipe(BWMBlocks.UNFIRED_POTTERY, 2, new ItemStack(BWMBlocks.URN));
        addKilnRecipe(BWMBlocks.UNFIRED_POTTERY, 3, new ItemStack(BWMBlocks.VASE));
        addKilnRecipe(Blocks.CLAY, 0, new ItemStack(Blocks.HARDENED_CLAY));
        addKilnRecipe(BlockRawPastry.getStack(BlockRawPastry.EnumType.CAKE), new ItemStack(Items.CAKE));
        addKilnRecipe(BlockRawPastry.getStack(BlockRawPastry.EnumType.BREAD), new ItemStack(Items.BREAD));
        addKilnRecipe(BlockRawPastry.getStack(BlockRawPastry.EnumType.APPLE), new ItemStack(BWMItems.APPLE_PIE));
        addKilnRecipe(BlockRawPastry.getStack(BlockRawPastry.EnumType.PUMPKIN), new ItemStack(Items.PUMPKIN_PIE));
        addKilnRecipe(BlockRawPastry.getStack(BlockRawPastry.EnumType.COOKIE), new ItemStack(Items.COOKIE, 16));
        addKilnRecipe(BWMBlocks.NETHER_CLAY, new ItemStack(BWMBlocks.AESTHETIC, 1, 2));
    }
}
