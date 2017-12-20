package betterwithmods.module.hardcore.crafting;

import betterwithmods.common.BWMRecipes;
import betterwithmods.common.BWOreDictionary;
import betterwithmods.common.registry.BrokenToolRegistry;
import betterwithmods.common.registry.ChoppingRecipe;
import betterwithmods.module.Feature;
import betterwithmods.util.InvUtils;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by tyler on 4/20/17.
 */
public class HCLumber extends Feature {
    public static int plankAmount, barkAmount, sawDustAmount;

    public static int axePlankAmount, axeBarkAmount, axeSawDustAmount;

    public static boolean hasAxe(BlockEvent.HarvestDropsEvent event) {
        if (!event.getWorld().isRemote && !event.isSilkTouching()) {
            EntityPlayer player = event.getHarvester();
            if (player != null) {
                ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
                if (stack.isEmpty()) {
                    // if the tool broke while harvesting this block...
                    // it's not in the main hand anymore by the time HarvestDropsEvent happens
                    stack = BrokenToolRegistry.getDestroyedItem(player);
                }
                return stack.getItem().getHarvestLevel(stack, "axe", player, event.getState()) >= 0 || stack.getItem().getToolClasses(stack).contains("axe");
            }
        }
        return event.isSilkTouching();
    }

    @Override
    public void setupConfig() {
        plankAmount = loadPropInt("Plank Amount", "Amount of Planks dropped when Punching Wood", 2);
        barkAmount = loadPropInt("Bark Amount", "Amount of Bark dropped when Punching Wood", 1);
        sawDustAmount = loadPropInt("Sawdust Amount", "Amount of Sawdust dropped when Punching Wood", 2);

        axePlankAmount = loadPropInt("Axe Plank Amount", "Amount of Planks dropped when crafted with an axe", 3);
        axeBarkAmount = loadPropInt("Axe Bark Amount", "Amount of Bark dropped when crafted with an axe", 1);
        axeSawDustAmount = loadPropInt("Axe Sawdust Amount", "Amount of Sawdust dropped when crafted with an axe", 2);
    }

    @Override
    public String getFeatureDescription() {
        return "Makes Punching Wood return a single plank and secondary drops instead of a log, to get a log an axe must be used.";
    }

    @Override
    public void init(FMLInitializationEvent event) {
        BrokenToolRegistry.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        if (!Loader.isModLoaded("primal")) {
            for (IRecipe recipe : BWOreDictionary.logRecipes) {
                ItemStack plank = recipe.getRecipeOutput();
                BWOreDictionary.woods.stream().filter(w -> w.getPlank(axePlankAmount).isItemEqual(plank) && hasLog(recipe, w.getLog(1))).forEach(wood -> {
                    if (wood != null) {
                        addHardcoreRecipe(new ChoppingRecipe(wood, axePlankAmount).setRegistryName(recipe.getRegistryName()));
                    }
                });
            }
        }
    }

    @Override
    public void disabledPostInit(FMLPostInitializationEvent event) {
        if (!Loader.isModLoaded("primal")) {
            for (IRecipe recipe : BWOreDictionary.logRecipes) {
                ItemStack plank = recipe.getRecipeOutput();
                BWOreDictionary.woods.stream().filter(w -> w.getPlank(4).isItemEqual(plank) && hasLog(recipe, w.getLog(1))).forEach(wood -> {
                    if (wood != null) {
                        addHardcoreRecipe(new ChoppingRecipe(wood, 4).setRegistryName(recipe.getRegistryName()));
                    }
                });
            }
        }
    }

    private boolean hasLog(IRecipe recipe, ItemStack log) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getMatchingStacks() != null && ingredient.getMatchingStacks().length > 0) {
                for (ItemStack stack : ingredient.getMatchingStacks()) {
                    if (stack.isItemEqual(log))
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public void disabledInit(FMLInitializationEvent event) {
    }

    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }

    @SubscribeEvent
    public void harvestLog(BlockEvent.HarvestDropsEvent evt) {
        if (!evt.getWorld().isRemote) {
            if (hasAxe(evt) || Loader.isModLoaded("primal"))
                return;
            ItemStack stack = BWMRecipes.getStackFromState(evt.getState());

            BWOreDictionary.Wood wood = BWOreDictionary.woods.stream().filter(w -> InvUtils.matches(w.getLog(1),stack)).findFirst().orElse(null);
            if (wood != null) {
                evt.getDrops().clear();
                evt.getDrops().addAll(Lists.newArrayList(wood.getPlank(plankAmount), wood.getSawdust(sawDustAmount), wood.getBark(barkAmount)));
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
