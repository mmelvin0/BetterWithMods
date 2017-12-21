package betterwithmods.module.hardcore.crafting;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.blocks.BlockFurnace;
import betterwithmods.module.Feature;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.HashMap;

public class HCFurnace extends Feature {
    public HCFurnace() {
        enabledByDefault = false;
    }

    public static int DEFAULT_FURNACE_TIMING = 200;
    public static HashMap<Ingredient, Integer> FURNACE_TIMINGS = Maps.newHashMap();

    public static final Block FURNACE = new BlockFurnace(false).setRegistryName("minecraft:furnace");
    public static final Block LIT_FURNACE = new BlockFurnace(true).setRegistryName("minecraft:lit_furnace");

    @Override
    public void setupConfig()
    {
        DEFAULT_FURNACE_TIMING = loadPropInt("Default Furnace Timing", "Default number of ticks for an item to smelt in the furnace (vanilla is 200)", "", 200, 1, Integer.MAX_VALUE);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        BWMBlocks.registerBlock(FURNACE);
        BWMBlocks.registerBlock(LIT_FURNACE, null);
    }


    @Override
    public void init(FMLInitializationEvent event) {


        BWMRecipes.removeFurnaceRecipe(new ItemStack(Blocks.DIAMOND_ORE));
        BWMRecipes.removeFurnaceRecipe(new ItemStack(Blocks.COAL_ORE));
        BWMRecipes.removeFurnaceRecipe(new ItemStack(Blocks.EMERALD_ORE));
        BWMRecipes.removeFurnaceRecipe(new ItemStack(Blocks.REDSTONE_ORE));
        BWMRecipes.removeFurnaceRecipe(new ItemStack(Blocks.LAPIS_ORE));
        BWMRecipes.removeFurnaceRecipe(new ItemStack(Blocks.QUARTZ_BLOCK));

        //Remove Furnace Recyclcing

        BWMRecipes.removeFurnaceRecipe(Items.CHAINMAIL_HELMET);
        BWMRecipes.removeFurnaceRecipe(Items.CHAINMAIL_CHESTPLATE);
        BWMRecipes.removeFurnaceRecipe(Items.CHAINMAIL_LEGGINGS);
        BWMRecipes.removeFurnaceRecipe(Items.CHAINMAIL_BOOTS);
        BWMRecipes.removeFurnaceRecipe(Items.IRON_PICKAXE);
        BWMRecipes.removeFurnaceRecipe(Items.IRON_SHOVEL);
        BWMRecipes.removeFurnaceRecipe(Items.IRON_AXE);
        BWMRecipes.removeFurnaceRecipe(Items.IRON_HOE);
        BWMRecipes.removeFurnaceRecipe(Items.IRON_SWORD);
        BWMRecipes.removeFurnaceRecipe(Items.IRON_HELMET);
        BWMRecipes.removeFurnaceRecipe(Items.IRON_CHESTPLATE);
        BWMRecipes.removeFurnaceRecipe(Items.IRON_LEGGINGS);
        BWMRecipes.removeFurnaceRecipe(Items.IRON_BOOTS);
        BWMRecipes.removeFurnaceRecipe(Items.IRON_HORSE_ARMOR);
        BWMRecipes.removeFurnaceRecipe(Items.GOLDEN_PICKAXE);
        BWMRecipes.removeFurnaceRecipe(Items.GOLDEN_SHOVEL);
        BWMRecipes.removeFurnaceRecipe(Items.GOLDEN_AXE);
        BWMRecipes.removeFurnaceRecipe(Items.GOLDEN_HOE);
        BWMRecipes.removeFurnaceRecipe(Items.GOLDEN_SWORD);
        BWMRecipes.removeFurnaceRecipe(Items.GOLDEN_HELMET);
        BWMRecipes.removeFurnaceRecipe(Items.GOLDEN_CHESTPLATE);
        BWMRecipes.removeFurnaceRecipe(Items.GOLDEN_LEGGINGS);
        BWMRecipes.removeFurnaceRecipe(Items.GOLDEN_BOOTS);
        BWMRecipes.removeFurnaceRecipe(Items.GOLDEN_HORSE_ARMOR);

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        FURNACE_TIMINGS = loadItemStackIntMap("Furnace Timing Recipes", "example recipes  minecraft:iron_ore=1000  or ore:oreIron=1000", new String[]{
                "ore:oreIron=400",
                "ore:oreGold=400",
                "ore:cobblestone=800"
        });



        System.out.println(FURNACE_TIMINGS);
    }
}

