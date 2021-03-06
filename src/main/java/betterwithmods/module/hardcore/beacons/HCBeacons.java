package betterwithmods.module.hardcore.beacons;

import betterwithmods.BWMod;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.BlockAesthetic;
import betterwithmods.common.blocks.BlockEnderchest;
import betterwithmods.common.blocks.BlockSteel;
import betterwithmods.common.blocks.tile.TileEnderchest;
import betterwithmods.common.items.tools.ItemSoulforgeArmor;
import betterwithmods.module.Feature;
import betterwithmods.util.player.PlayerHelper;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

import static betterwithmods.common.BWMBlocks.BEACON;

/**
 * Created by primetoxinz on 7/17/17.
 */
public class HCBeacons extends Feature {

    public static final HashMap<IBlockState, IBeaconEffect> BEACON_EFFECTS = Maps.newHashMap();

    public static final IBeaconEffect getBeaconEffect(IBlockState state) {
        if (BEACON_EFFECTS.containsKey(state))
            return BEACON_EFFECTS.get(state);
        return (world, pos, level) -> {
        };
    }

    public static boolean enderchestBeacon;

    @Override
    public void setupConfig() {
        enderchestBeacon = loadPropBool("Enderchest Beacon", "Rework how Enderchests work. Enderchests on their own work like normal chests. When placed on a beacon made of Ender Block the chest functions depending on level, more info in the Book of Single.", true);
    }

    public static final Block ENDERCHEST = new BlockEnderchest().setRegistryName("minecraft:ender_chest");

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        BWMBlocks.registerBlock(BEACON);
        if (enderchestBeacon) {
            BWMBlocks.registerBlock(ENDERCHEST);
            CapabilityManager.INSTANCE.register(Enderchest.class, new Storage(), Enderchest::new);
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {

        BEACON_EFFECTS.put(Blocks.GLASS.getDefaultState(), (world, pos, level) -> {
        });
        BEACON_EFFECTS.put(Blocks.IRON_BLOCK.getDefaultState(), (world, pos, level) -> {
            //TODO substitute ItemCompass.
        });
        BEACON_EFFECTS.put(Blocks.EMERALD_BLOCK.getDefaultState(), (world, pos, level) -> IBeaconEffect.forEachEntityAround(EntityLivingBase.class, world, pos, level, entity -> entity.addPotionEffect(new PotionEffect(BWRegistry.POTION_LOOTING, 125, level - 1, true, false))));
        BEACON_EFFECTS.put(Blocks.LAPIS_BLOCK.getDefaultState(), (world, pos, level) -> IBeaconEffect.forEachPlayersAround(world, pos, level, player -> player.addPotionEffect(new PotionEffect(BWRegistry.POTION_TRUESIGHT, 125, 1))));
        BEACON_EFFECTS.put(Blocks.DIAMOND_BLOCK.getDefaultState(), (world, pos, level) -> IBeaconEffect.forEachPlayersAround(world, pos, level, player -> player.addPotionEffect(new PotionEffect(BWRegistry.POTION_FORTUNE, 125, level - 1))));
        BEACON_EFFECTS.put(Blocks.GLOWSTONE.getDefaultState(), (world, pos, level) -> IBeaconEffect.forEachPlayersAround(world, pos, level, player -> player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 125, 1))));
        BEACON_EFFECTS.put(Blocks.GOLD_BLOCK.getDefaultState(), (world, pos, level) -> IBeaconEffect.forEachPlayersAround(world, pos, level, player -> player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 120, level))));
        BEACON_EFFECTS.put(Blocks.SLIME_BLOCK.getDefaultState(), (world, pos, level) -> IBeaconEffect.forEachPlayersAround(world, pos, level, player -> player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 120, level))));
        BEACON_EFFECTS.put(BlockAesthetic.getVariant(BlockAesthetic.EnumType.DUNG), (world, pos, level) -> IBeaconEffect.forEachPlayersAround(world, pos, level, player -> {
                    if (!PlayerHelper.hasFullSet((EntityPlayer) player, ItemSoulforgeArmor.class)) {
                        player.addPotionEffect(new PotionEffect(MobEffects.POISON, 120, level));
                        player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 120, level));
                    }
                }
        ));
        BEACON_EFFECTS.put(Blocks.COAL_BLOCK.getDefaultState(), (world, pos, level) -> IBeaconEffect.forEachPlayersAround(world, pos, level, player -> {
                    if (!PlayerHelper.hasPart(player, EntityEquipmentSlot.HEAD, ItemSoulforgeArmor.class)) {
                        player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 120, level));
                    }
                }
        ));
        BEACON_EFFECTS.put(BlockAesthetic.getVariant(BlockAesthetic.EnumType.HELLFIRE), (world, pos, level) -> IBeaconEffect.forEachPlayersAround(world, pos, level, player -> player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 120, level))));
        BEACON_EFFECTS.put(Blocks.PRISMARINE.getDefaultState(), (world, pos, level) -> IBeaconEffect.forEachPlayersAround(world, pos, level, player -> player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 120, level))));
        BEACON_EFFECTS.put(Blocks.SPONGE.getDefaultState(), (world, pos, level) -> IBeaconEffect.forEachEntityAround(EntityLivingBase.class, world, pos, level, entity -> {
            if (!(entity instanceof EntityWaterMob) && !(entity.isEntityUndead() && !PlayerHelper.hasPart(entity, EntityEquipmentSlot.HEAD, ItemSoulforgeArmor.class))) {
                entity.setAir(entity.getAir() - 1);
            }
        }));
        BEACON_EFFECTS.put(BWMBlocks.STEEL_BLOCK.getDefaultState().withProperty(BlockSteel.HEIGHT, 15), new SpawnBeaconEffect());

        if (enderchestBeacon) {
            BEACON_EFFECTS.put(BlockAesthetic.getVariant(BlockAesthetic.EnumType.ENDERBLOCK), new EnderBeaconEffect());
        }

    }

    @Override
    public String getFeatureDescription() {
        return "Overhauls the function of Beacons. Beacons have extended range, no longer have a GUI, and require the same material throughout the pyramid. The pyramid material determines the beacon effect, and additional tiers increase the range and strength of the effects. Some beacon types may also cause side effects to occur while a beacon is active.";
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    public static ResourceLocation WORLD1 = new ResourceLocation(BWMod.MODID, "world_enderchest");
    public static ResourceLocation WORLD2 = new ResourceLocation(BWMod.MODID, "world2_enderchest");
    public static ResourceLocation GLOBAL = new ResourceLocation(BWMod.MODID, "global_enderchest");


    @SubscribeEvent
    public void attachTileCapability(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getObject() instanceof TileEnderchest && !event.getObject().hasCapability(ENDERCHEST_CAPABILITY, EnumFacing.UP)) {
            event.addCapability(new ResourceLocation(BWMod.MODID, "enderchest"), new Enderchest(EnumFacing.UP));
        }
    }

    @SubscribeEvent
    public void attachWorldCapability(AttachCapabilitiesEvent<World> event) {
        World world = event.getObject();
        if (world.provider.getDimensionType() == DimensionType.OVERWORLD) {
            if (!world.hasCapability(ENDERCHEST_CAPABILITY, EnumFacing.DOWN)) {
                event.addCapability(GLOBAL, new Enderchest(EnumFacing.DOWN));
            }
        }
        if (!world.hasCapability(ENDERCHEST_CAPABILITY, EnumFacing.SOUTH)) {
            event.addCapability(WORLD1, new Enderchest(EnumFacing.SOUTH));
        }
        if (!world.hasCapability(ENDERCHEST_CAPABILITY, EnumFacing.NORTH)) {
            event.addCapability(WORLD2, new Enderchest(EnumFacing.NORTH));
        }
    }

    @CapabilityInject(Enderchest.class)
    public static Capability<Enderchest> ENDERCHEST_CAPABILITY = null;


    public class Enderchest implements ICapabilitySerializable<NBTTagCompound> {

        private EnumFacing facing;
        private InventoryEnderChest inventory;

        public Enderchest(EnumFacing facing, InventoryEnderChest inventory) {
            this.facing = facing;
            this.inventory = inventory;
        }

        public Enderchest(EnumFacing facing) {
            this(facing, new InventoryEnderChest());
        }

        public Enderchest() {
            this(EnumFacing.NORTH, new InventoryEnderChest());
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return facing == this.facing && capability.equals(ENDERCHEST_CAPABILITY);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (hasCapability(capability, facing))
                return ENDERCHEST_CAPABILITY.cast(this);
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("facing", facing.getIndex());
            if (inventory != null) {
                tag.setTag("inventory", inventory.saveInventoryToNBT());
            }
            return tag;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            if (nbt.hasKey("inventory")) {
                NBTTagList list = nbt.getTagList("inventory", 10);
                inventory = new InventoryEnderChest();
                inventory.loadInventoryFromNBT(list);
            }
            facing = EnumFacing.getFront(nbt.getInteger("facing"));
        }

        public InventoryEnderChest getInventory() {
            return inventory;
        }
    }

    public class Storage implements Capability.IStorage<Enderchest> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<Enderchest> capability, Enderchest instance, EnumFacing side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<Enderchest> capability, Enderchest instance, EnumFacing side, NBTBase nbt) {
            instance.deserializeNBT((NBTTagCompound) nbt);
        }
    }
}
