package betterwithmods.proxy;

import betterwithmods.BWMod;
import betterwithmods.client.BWStateMapper;
import betterwithmods.client.ClientEventHandler;
import betterwithmods.client.ColorHandlers;
import betterwithmods.client.ResourceProxy;
import betterwithmods.client.model.*;
import betterwithmods.client.model.render.RenderUtils;
import betterwithmods.client.render.*;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMItems;
import betterwithmods.common.blocks.mechanical.tile.*;
import betterwithmods.common.blocks.tile.TileEntityBeacon;
import betterwithmods.common.entity.*;
import betterwithmods.manual.api.ManualAPI;
import betterwithmods.manual.api.prefab.manual.ResourceContentProvider;
import betterwithmods.manual.api.prefab.manual.TextureTabIconRenderer;
import betterwithmods.manual.client.manual.provider.*;
import betterwithmods.module.ModuleLoader;
import betterwithmods.module.hardcore.creatures.EntityTentacle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = BWMod.MODID, value = Side.CLIENT)

public class ClientProxy implements IProxy {
    // Minecraft
    public static final String[] DEFAULT_RESOURCE_PACKS = new String[]{"field_110449_ao", "defaultResourcePacks"};
    private static ResourceProxy resourceProxy;

    static {
        List<IResourcePack> packs = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), DEFAULT_RESOURCE_PACKS);
        resourceProxy = new ResourceProxy();
        packs.add(resourceProxy);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {


        ModuleLoader.preInitClient(event);
        registerRenderInformation();
        initRenderers();
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        List<IResourcePack> packs = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), DEFAULT_RESOURCE_PACKS);
        System.out.println(packs.stream().map(IResourcePack::getPackName).toString());

        ModuleLoader.initClient(event);
        registerColors();
        ManualAPI.addProvider(new DefinitionPathProvider());
        ManualAPI.addProvider(new ResourceContentProvider("betterwithmods", "docs/"));
        ManualAPI.addProvider("", new TextureImageProvider());
        ManualAPI.addProvider("item", new ItemImageProvider());
        ManualAPI.addProvider("block", new BlockImageProvider());
        ManualAPI.addProvider("oredict", new OreDictImageProvider());
        ManualAPI.addTab(new TextureTabIconRenderer(new ResourceLocation(BWMod.MODID, "textures/gui/manual_home.png")), "bwm.manual.home", "%LANGUAGE%/index.md");
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ModuleLoader.postInitClient(event);
        RenderUtils.registerFilters();
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        BWMItems.getItems().forEach(BWMItems::setInventoryModel);
        ModelLoader.setCustomStateMapper(BWMBlocks.STOKED_FLAME, new BWStateMapper(BWMBlocks.STOKED_FLAME.getRegistryName().toString()));
        ModelLoader.setCustomStateMapper(BWMBlocks.WINDMILL, new BWStateMapper(BWMBlocks.WINDMILL.getRegistryName().toString()));
        ModelLoader.setCustomStateMapper(BWMBlocks.WATERWHEEL, new BWStateMapper(BWMBlocks.WATERWHEEL.getRegistryName().toString()));
        ModelLoaderRegistry.registerLoader(new ModelKiln.Loader());
    }

    private void registerRenderInformation() {

        OBJLoader.INSTANCE.addDomain(BWMod.MODID);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWindmillHorizontal.class, new TESRWindmill());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWindmillVertical.class, new TESRVerticalWindmill());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWaterwheel.class, new TESRWaterwheel());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFilteredHopper.class, new TESRFilteredHopper());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTurntable.class, new TESRTurntable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCookingPot.class, new TESRCookingPot());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBeacon.class, new TESRBeacon());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSteelSaw.class, new TESRSteelSaw());
    }

    private void registerColors() {
        final BlockColors col = Minecraft.getMinecraft().getBlockColors();
        col.registerBlockColorHandler(ColorHandlers.BlockPlanterColor, BWMBlocks.PLANTER);
        col.registerBlockColorHandler(ColorHandlers.BlockFoliageColor, BWMBlocks.VINE_TRAP);
        col.registerBlockColorHandler(ColorHandlers.BlockBloodLeafColor, BWMBlocks.BLOOD_LEAVES);
        final ItemColors itCol = Minecraft.getMinecraft().getItemColors();
        itCol.registerItemColorHandler(ColorHandlers.ItemPlanterColor, BWMBlocks.PLANTER);
        itCol.registerItemColorHandler(ColorHandlers.ItemFoliageColor, BWMBlocks.VINE_TRAP);
        itCol.registerItemColorHandler(ColorHandlers.ItemBloodLeafColor, BWMBlocks.BLOOD_LEAVES);
        col.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D), BWMBlocks.DIRT_SLAB);
        itCol.registerItemColorHandler((stack, tintIndex) -> {
            IBlockState iblockstate = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
            return col.colorMultiplier(iblockstate, null, null, tintIndex);
        }, BWMBlocks.DIRT_SLAB);
    }

    private void initRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityDynamite.class, manager -> new RenderSnowball<>(manager, BWMItems.DYNAMITE, Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(EntityUrn.class, RenderUrn::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityMiningCharge.class, RenderMiningCharge::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityExtendingRope.class, RenderExtendingRope::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityShearedCreeper.class, RenderShearedCreeper::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityCow.class, RenderCowHarness::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityPig.class, RenderPigHarness::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySheep.class, RenderSheepHarness::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBroadheadArrow.class, RenderBroadheadArrow::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySpiderWeb.class, manager -> new RenderSnowball<>(manager, Item.getItemFromBlock(Blocks.WEB), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(EntityJungleSpider.class, RenderJungleSpider::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTentacle.class, RenderTentacle::new);
    }


    public static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {

        public final Fluid fluid;
        public final ModelResourceLocation location;

        public FluidStateMapper(Fluid fluid) {
            this.fluid = fluid;
            // have each block hold its fluid per nbt? hm
            this.location = new ModelResourceLocation(new ResourceLocation("betterwithmods", "fluid_block"), fluid.getName());
        }

        @Nonnull
        @Override
        protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
            return location;
        }

        @Nonnull
        @Override
        public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
            return location;
        }
    }


    @Override
    public void addResourceOverride(String space, String dir, String file, String ext) {
        resourceProxy.addResource(space, dir, file, ext);
    }

    @Override
    public void addResourceOverride(String space, String domain, String dir, String file, String ext) {
        resourceProxy.addResource(space, domain, dir, file, ext);
    }


}
