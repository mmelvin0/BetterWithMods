package betterwithmods.module.gameplay;

import betterwithmods.common.BWMBlocks;
import betterwithmods.module.Module;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class Gameplay extends Module {
    public static double crankExhaustion;
    public static boolean kidFriendly;


    @Override
    public void addFeatures() {
        registerFeature(new MechanicalBreakage());
        registerFeature(new MetalReclaming());
        registerFeature(new NuggetCompression());
        registerFeature(new HarderSteelRecipe());
        registerFeature(new AnvilRecipes());
        registerFeature(new CraftingRecipes());
        registerFeature(new CauldronRecipes());
        registerFeature(new CrucibleRecipes());
        registerFeature(new KilnRecipes());
        registerFeature(new MillRecipes());
        registerFeature(new SawRecipes());
        registerFeature(new TurntableRecipes());
        registerFeature(new HopperRecipes());
        registerFeature(new NetherGrowth());
//        registerFeature(new MiniBlocks());
    }

    @Override
    public void setupConfig() {
        crankExhaustion = loadPropDouble("Crank Exhaustion", "How much saturation turning the crank eats. Set to 0.0 to disable.", 6.0, 0.0, 6.0);
        kidFriendly = loadPropBool("Kid Friendly", "Makes some features more kid friendly", false);
        loadRecipeCondition("higheff", "High Efficiency Recipes", "Enables High Efficiency Recipes", true);
        super.setupConfig();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.addGrassSeed(new ItemStack(BWMBlocks.HEMP, 1), 5);
    }


}

