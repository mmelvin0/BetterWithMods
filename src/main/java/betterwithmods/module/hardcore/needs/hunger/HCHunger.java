package betterwithmods.module.hardcore.needs.hunger;

import betterwithmods.client.gui.GuiHunger;
import betterwithmods.common.BWMItems;
import betterwithmods.common.items.ItemBlockEdible;
import betterwithmods.common.items.ItemEdibleSeeds;
import betterwithmods.module.CompatFeature;
import betterwithmods.network.MessageGuiShake;
import betterwithmods.network.NetworkHandler;
import betterwithmods.util.player.FatPenalty;
import betterwithmods.util.player.HungerPenalty;
import betterwithmods.util.player.PlayerHelper;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.applecore.api.AppleCoreAPI;
import squeek.applecore.api.food.FoodEvent;
import squeek.applecore.api.food.FoodValues;
import squeek.applecore.api.food.IEdibleBlock;
import squeek.applecore.api.hunger.ExhaustionEvent;
import squeek.applecore.api.hunger.HealthRegenEvent;
import squeek.applecore.api.hunger.HungerEvent;
import squeek.applecore.api.hunger.StarvationEvent;

import java.util.Set;

/**
 * Created by primetoxinz on 6/20/17.
 */
public class HCHunger extends CompatFeature {
    public HCHunger() {
        super("applecore");
    }

    public static float blockBreakExhaustion;
    public static float passiveExhaustion;
    public static int passiveExhaustionTick;
    public static boolean rawMeatDangerous;
//	public static boolean fat;

    public static boolean overridePumpkinSeeds;
    public static boolean overrideMushrooms;


    @Override
    public void setupConfig() {
        blockBreakExhaustion = (float) loadPropDouble("Block Breaking Exhaustion", "Set Exhaustion from breaking a block", 0.1);
        passiveExhaustion = (float) loadPropDouble("Passive Exhaustion", "Passive Exhaustion value", 4f);
        passiveExhaustionTick = loadPropInt("Passive Exhaustion Tick", "Passive exhaustion tick time", 900);
        rawMeatDangerous = loadPropBool("Raw Meat is Unhealthy", "Gives food poisoning", true);

        overrideMushrooms = loadPropBool("Edible Mushrooms", "Override Mushrooms to be edible, be careful with the red one ;)", true);
        overridePumpkinSeeds = loadPropBool("Edible Pumpkin Seeds", "Override Pumpkin Seeds to be edible", true);
//		fat = loadPropBool("Fat", "Fat replaces saturation and only decreases when hunger is depleted completely", true);
    }

    public static Item PUMPKIN_SEEDS = new ItemEdibleSeeds(Blocks.PUMPKIN_STEM, Blocks.FARMLAND, 1, 0).setRegistryName("minecraft:pumpkin_seeds").setUnlocalizedName("seeds_pumpkin");
    public static Item BROWN_MUSHROOM = new ItemBlockEdible(Blocks.BROWN_MUSHROOM, 1, 0, false).setRegistryName("minecraft:brown_mushroom");
    public static Item RED_MUSHROOM = new ItemBlockEdible(Blocks.RED_MUSHROOM, 1, 0, false)
            .setPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0), 1).setRegistryName("minecraft:red_mushroom");

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (overridePumpkinSeeds)
            BWMItems.registerItem(PUMPKIN_SEEDS);
        if (overrideMushrooms) {
            BWMItems.registerItem(BROWN_MUSHROOM);
            BWMItems.registerItem(RED_MUSHROOM);
        }

    }

    @Override
    public void init(FMLInitializationEvent event) {
        if (rawMeatDangerous) {
            Set<Item> RAW_FOOD = Sets.newHashSet(BWMItems.RAW_SCRAMBLED_EGG, BWMItems.RAW_EGG, BWMItems.RAW_OMELET, BWMItems.RAW_KEBAB, Items.FISH, BWMItems.WOLF_CHOP, Items.BEEF, Items.PORKCHOP, Items.RABBIT, Items.CHICKEN, Items.MUTTON, BWMItems.MYSTERY_MEAT);
            RAW_FOOD.stream().map(i -> (ItemFood) i).forEach(i -> i.setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.3F));
        }

        FoodHelper.registerFood(new ItemStack(Items.BEEF), 12);
        FoodHelper.registerFood(new ItemStack(Items.PORKCHOP), 12);
        FoodHelper.registerFood(new ItemStack(Items.RABBIT), 12);
        FoodHelper.registerFood(new ItemStack(Items.CHICKEN), 9);
        FoodHelper.registerFood(new ItemStack(Items.MUTTON), 9);
        FoodHelper.registerFood(new ItemStack(Items.FISH), 9);
        FoodHelper.registerFood(new ItemStack(Items.FISH, 1, 1), 9);
        FoodHelper.registerFood(new ItemStack(Items.COOKED_BEEF), 15);
        FoodHelper.registerFood(new ItemStack(Items.COOKED_PORKCHOP), 15);
        FoodHelper.registerFood(new ItemStack(Items.COOKED_RABBIT), 15);
        FoodHelper.registerFood(new ItemStack(Items.COOKED_CHICKEN), 12);
        FoodHelper.registerFood(new ItemStack(Items.COOKED_MUTTON), 12);
        FoodHelper.registerFood(new ItemStack(Items.COOKED_FISH), 12);
        FoodHelper.registerFood(new ItemStack(Items.COOKED_FISH, 1, 1), 12);
        FoodHelper.registerFood(new ItemStack(Items.SPIDER_EYE), 6);
        FoodHelper.registerFood(new ItemStack(Items.ROTTEN_FLESH), 9);
        FoodHelper.registerFood(new ItemStack(Items.MUSHROOM_STEW), 9);
        FoodHelper.registerFood(new ItemStack(Items.BEETROOT_SOUP), 9);
        FoodHelper.registerFood(new ItemStack(Items.RABBIT_STEW), 30);
        FoodHelper.registerFood(new ItemStack(Items.MELON), 2);
        FoodHelper.registerFood(new ItemStack(Items.APPLE), 3);
        FoodHelper.registerFood(new ItemStack(Items.POTATO), 3);
        FoodHelper.registerFood(new ItemStack(Items.CARROT), 3);
        FoodHelper.registerFood(new ItemStack(Items.BEETROOT), 3);
        FoodHelper.registerFood(new ItemStack(Items.BAKED_POTATO), 6);
        FoodHelper.registerFood(new ItemStack(Items.BREAD), 12);

        FoodHelper.registerFood(new ItemStack(Items.GOLDEN_APPLE), 3);
        FoodHelper.registerFood(new ItemStack(Items.GOLDEN_CARROT), 3);
        FoodHelper.registerFood(new ItemStack(BWMItems.BEEF_DINNER), 24);
        FoodHelper.registerFood(new ItemStack(BWMItems.BEEF_POTATOES), 18);
        FoodHelper.registerFood(new ItemStack(BWMItems.RAW_KEBAB), 18);
        FoodHelper.registerFood(new ItemStack(BWMItems.COOKED_KEBAB), 24);
        FoodHelper.registerFood(new ItemStack(BWMItems.CHICKEN_SOUP), 24);
        FoodHelper.registerFood(new ItemStack(BWMItems.CHOWDER), 15);
        FoodHelper.registerFood(new ItemStack(BWMItems.HEARTY_STEW), 30);
        FoodHelper.registerFood(new ItemStack(BWMItems.PORK_DINNER), 24);
        FoodHelper.registerFood(new ItemStack(BWMItems.RAW_EGG), 6);
        FoodHelper.registerFood(new ItemStack(BWMItems.COOKED_EGG), 9);
        FoodHelper.registerFood(new ItemStack(BWMItems.RAW_SCRAMBLED_EGG), 12);
        FoodHelper.registerFood(new ItemStack(BWMItems.COOKED_SCRAMBLED_EGG), 15);
        FoodHelper.registerFood(new ItemStack(BWMItems.RAW_OMELET), 9);
        FoodHelper.registerFood(new ItemStack(BWMItems.COOKED_OMELET), 12);
        FoodHelper.registerFood(new ItemStack(BWMItems.HAM_AND_EGGS), 18);
        FoodHelper.registerFood(new ItemStack(BWMItems.TASTY_SANDWICH), 18);
        FoodHelper.registerFood(new ItemStack(BWMItems.CREEPER_OYSTER), 6);
        FoodHelper.registerFood(new ItemStack(BWMItems.KIBBLE), 9);
        FoodHelper.registerFood(new ItemStack(BWMItems.WOLF_CHOP), 12);
        FoodHelper.registerFood(new ItemStack(BWMItems.COOKED_WOLF_CHOP), 15);
        FoodHelper.registerFood(new ItemStack(BWMItems.MYSTERY_MEAT), 9);
        FoodHelper.registerFood(new ItemStack(BWMItems.COOKED_MYSTERY_MEAT), 12);

        FoodHelper.registerFood(new ItemStack(BWMItems.DONUT), 3, 0.5f, true);
        FoodHelper.registerFood(new ItemStack(BWMItems.APPLE_PIE), 9, 1.6f, true);
        FoodHelper.registerFood(new ItemStack(BWMItems.CHOCOLATE), 6, 3, true);
        FoodHelper.registerFood(new ItemStack(Items.COOKIE), 3, 1, true);
        FoodHelper.registerFood(new ItemStack(Items.PUMPKIN_PIE), 9, 1.6f, true);
        FoodHelper.registerFood(new ItemStack(Items.CAKE), 4, 3, true);

        ((IEdibleBlock) Blocks.CAKE).setEdibleAtMaxHunger(true);
    }

    @Override
    public void preInitClient(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(ClientSide.class);
        super.preInitClient(event);
    }

    //Changes food to correct value.
    @SubscribeEvent
    public void modifyFoodValues(FoodEvent.GetFoodValues event) {
        event.foodValues = FoodHelper.getFoodValue(event.food).orElseGet(() -> new FoodValues(event.foodValues.hunger * 3, event.foodValues.saturationModifier * 3));
    }

    //Stops Eating if Hunger Effect is active
    @SubscribeEvent
    public void onFood(LivingEntityUseItemEvent.Start event) {
        if (event.getItem().getItem() instanceof ItemFood && event.getEntityLiving() instanceof EntityPlayer && PlayerHelper.isSurvival((EntityPlayer) event.getEntityLiving())) {
            if (event.getEntityLiving().isPotionActive(MobEffects.HUNGER)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void getPlayerFoodValue(FoodEvent.GetPlayerFoodValues event) {
        FoodStats stats = event.player.getFoodStats();
        int playerFoodLevel = stats.getFoodLevel();
        int foodLevel = event.foodValues.hunger;
        int max = AppleCoreAPI.accessor.getMaxHunger(event.player);
        int newFood = (foodLevel + playerFoodLevel);
        if (newFood <= max) {
            event.foodValues = new FoodValues(foodLevel, 0);
        } else {
            float modifier = event.foodValues.saturationModifier == 0 ? 0.5f : event.foodValues.saturationModifier;
            float fat = modifier / 2f;
            event.foodValues = new FoodValues(foodLevel, fat);
        }
    }

    //Changes exhaustion to reduce food first, then fat.
    @SubscribeEvent
    public void exhaust(ExhaustionEvent.Exhausted event) {
        FoodStats stats = event.player.getFoodStats();
        int saturation = (int) ((stats.getSaturationLevel() - 1) / 6);
        int hunger = stats.getFoodLevel() / 6;
        if (hunger >= saturation) {
            event.deltaSaturation = 0;
            event.deltaHunger = -1;
        } else {
            event.deltaSaturation = -1;
            event.deltaHunger = 0;
        }
    }

    //Adds Exhaustion when Jumping and cancels Jump if too exhausted
    @SubscribeEvent
    public void onJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (!PlayerHelper.canJump(player)) {
                event.getEntityLiving().motionX = 0;
                event.getEntityLiving().motionY = 0;
                event.getEntityLiving().motionZ = 0;
            }
            player.addExhaustion(0.5f);
        }
    }

    @SubscribeEvent
    public void setMaxFood(HungerEvent.GetMaxHunger event) {
        event.maxHunger = 60;
    }

    //Chaneg speed based on Hunger
    @SubscribeEvent
    public void givePenalties(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            PlayerHelper.changeSpeed(player, "Hunger Speed Modifier", PlayerHelper.getHungerPenalty(player).getModifier(), PlayerHelper.PENALTY_SPEED_UUID);
        }
    }

    //Max Health only regen when above Peckish
    @SubscribeEvent
    public void allowHealthRegen(HealthRegenEvent.AllowRegen event) {
        if (!event.player.world.getGameRules().getBoolean("naturalRegeneration")) return;
        event.setResult(PlayerHelper.getHungerPenalty(event.player) == HungerPenalty.NO_PENALTY ? Event.Result.ALLOW : Event.Result.DENY);
    }

    //Change Health Regen speed to take 30 seconds
    @SubscribeEvent
    public void healthRegenSpeed(HealthRegenEvent.GetRegenTickPeriod event) {
        event.regenTickPeriod = 600;
    }

    //Stop regen from Fat value.
    @SubscribeEvent
    public void denyFatRegen(HealthRegenEvent.AllowSaturatedRegen event) {
        event.setResult(Event.Result.DENY);
    }

    private static final DataParameter<Integer> EXHAUSTION_TICK = EntityDataManager.createKey(EntityPlayer.class, DataSerializers.VARINT);

    @SubscribeEvent
    public void entityConstruct(EntityEvent.EntityConstructing e) {
        if (e.getEntity() instanceof EntityPlayer) {
            e.getEntity().getDataManager().register(EXHAUSTION_TICK, 0);
        }
    }

    public int getExhaustionTick(EntityPlayer player) {
        return player.getDataManager().get(EXHAUSTION_TICK);
    }

    public void setExhaustionTick(EntityPlayer player, int tick) {
        player.getDataManager().set(EXHAUSTION_TICK, tick);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.world.isRemote && event.phase == TickEvent.Phase.START) {
            EntityPlayer player = event.player;
            if (player.isCreative() || player.world.getDifficulty() == EnumDifficulty.PEACEFUL)
                return;
            if (!PlayerHelper.getHungerPenalty(player).canSprint())
                player.setSprinting(false);
            int tick = getExhaustionTick(player);
            if (tick > passiveExhaustionTick) {
                player.addExhaustion(passiveExhaustion);
                setExhaustionTick(player, 0);
            } else {
                setExhaustionTick(player, getExhaustionTick(player) + 1);
            }
        }
    }

    //Shake Hunger bar whenever any exhaustion is given?
    @SubscribeEvent
    public void onExhaustAdd(ExhaustionEvent.ExhaustionAddition event) {
        if (event.player.world.getTotalWorldTime() % 20 == 0 && event.deltaExhaustion > 0.05) {
            if (event.player instanceof EntityPlayerMP)
                NetworkHandler.INSTANCE.sendTo(new MessageGuiShake(), (EntityPlayerMP) event.player);
            else
                GuiHunger.INSTANCE.shake();
        }
    }

    @SubscribeEvent
    public void onStarve(StarvationEvent.AllowStarvation event) {
        if (event.player.getFoodStats().getFoodLevel() <= 0)
            event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public void onStarve(StarvationEvent.Starve event) {
        event.setCanceled(true);
        event.player.attackEntityFrom(DamageSource.STARVE, 1);
    }

    @SubscribeEvent
    public void onHarvest(BlockEvent.BreakEvent event) {
        event.getPlayer().addExhaustion(blockBreakExhaustion - 0.005f);
    }

    //TODO fix Hunger starting as vanilla 20.

    public String getFeatureDescription() {
        return "This Feature REQUIRES AppleCore!!!.\n" +
                "Completely revamps the hunger system of Minecraft. \n" +
                "The Saturation value is replaced with Fat. \n" +
                "Fat will accumulate if too much food is consumed then need to fill the bar.\n" +
                "Fat will only be burned once the entire hunger bar is emptied \n" +
                "The more fat the slower you will walk.\n" +
                "Food Items values are also changed, while a ton of new foods are add.";
    }

    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public static class ClientSide {

        //Replaces Hunger Gui with HCHunger
        @SubscribeEvent
        public static void replaceHungerGui(RenderGameOverlayEvent.Pre event) {
            if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD) {
                event.setCanceled(true);
                GuiHunger.INSTANCE.draw();
            }
        }

        private static RenderPlayer getRenderPlayer(AbstractClientPlayer player) {
            Minecraft mc = Minecraft.getMinecraft();
            RenderManager manager = mc.getRenderManager();
            return manager.getSkinMap().get(player.getSkinType());
        }

        private static ModelBiped getPlayerModel(AbstractClientPlayer player) {
            return getRenderPlayer(player).getMainModel();
        }

        public static void putFat(AbstractClientPlayer player, FatPenalty fat) {
            ModelBiped model = getPlayerModel(player);
            float scale = fat != FatPenalty.NO_PENALTY ? Math.max(0, fat.ordinal() / 4f) : 0.0f;
            model.bipedBody = new ModelRenderer(model, 16, 16);
            model.bipedBody.addBox(-4.0F, 0, -2.0F, 8, 12, 4, scale);
        }

        public static void doFat(String playerName) {
            World world = Minecraft.getMinecraft().world;
            EntityPlayer player = world.getPlayerEntityByName(playerName);
            FatPenalty fat = PlayerHelper.getFatPenalty(player);
            if (player != null && player instanceof AbstractClientPlayer)
                putFat((AbstractClientPlayer) player, fat);
        }
    }


}




