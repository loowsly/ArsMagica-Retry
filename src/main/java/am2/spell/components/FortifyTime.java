package am2.spell.components;

import am2.AMChunkLoader;
import am2.AMCore;
import am2.api.spell.component.interfaces.ISpellComponent;
import am2.api.spell.enums.Affinity;
import am2.api.spell.enums.SpellModifiers;
import am2.buffs.BuffList;
import am2.items.ItemsCommonProxy;
import am2.particles.AMParticle;
import am2.particles.ParticleOrbitEntity;
import am2.playerextensions.ExtendedProperties;
import am2.spell.SpellUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Random;

public class FortifyTime implements ISpellComponent {

        @Override
        public boolean applyEffectBlock(ItemStack stack, World world, int blockx, int blocky, int blockz, int blockFace, double impactX, double impactY, double impactZ, EntityLivingBase caster){
            Block block = world.getBlock(blockx, blocky, blockz);
            if (block == Blocks.air){
                return false;
            }
            TileEntity te = world.getTileEntity(blockx, blocky, blockz);
            if (te == null) {
                return false;
            }
            int duration = SpellUtils.instance.getModifiedInt_Mul(BuffList.default_buff_duration, stack, caster, caster, world, 0, SpellModifiers.DURATION) * 2;
            duration = SpellUtils.instance.modifyDurationBasedOnArmor(caster, duration);
            ExtendedProperties ep = ExtendedProperties.For(caster);
            if (ep != null) {
                ep.addToExtraVariables("timefortified_tile_" + blockx + "_" + blocky + "_" + blockz + "_" + world.provider.dimensionId, String.valueOf(duration));
            }
            if (!world.isRemote){
                AMChunkLoader.INSTANCE.requestStaticChunkLoad(te.getClass(), te.xCoord, te.yCoord, te.zCoord, world);
            }
            return true;
        }

        @Override
        public boolean applyEffectEntity(ItemStack stack, World world, EntityLivingBase caster, Entity target){
            if (!(target instanceof EntityLiving)){
                return true;
            }
            EntityLiving targetLiving = (EntityLiving) target;
            targetLiving.func_110163_bv();
            return true;
        }

        @Override
        public float manaCost(EntityLivingBase caster){
            return 20;
        }

        @Override
        public float burnout(EntityLivingBase caster){
            return 10;
        }

	@Override
        public void spawnParticles(World world, double x, double y, double z, EntityLivingBase caster, Entity target, Random
        rand, int colorModifier){
            AMParticle particle = (AMParticle) AMCore.proxy.particleManager.spawn(world, "sparkle", x, y, z);
            if (particle != null){
                particle.AddParticleController(new ParticleOrbitEntity(particle, caster, 0.1f, 1, false).SetTargetDistance(rand.nextDouble() + 0.5));
                particle.setMaxAge(25 + rand.nextInt(10));
                if (colorModifier > -1){
                    particle.setRGBColorF(((colorModifier >> 16) & 0xFF) / 255.0f, ((colorModifier >> 8) & 0xFF) / 255.0f, (colorModifier & 0xFF) / 255.0f);
                }
            }
        }

        @Override
        public EnumSet<Affinity> getAffinity(){
            return EnumSet.of(Affinity.NONE);
        }

        @Override
        public int getID(){
            return 96;
        }

        @Override
        public Object[] getRecipeItems(){
            return new Object[]{
                    new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_YELLOW),
                    new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_CYAN),
                    new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_TEMPORALCLUSTER),
                    new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_COGNITIVEDUST),
                    Items.clock,
            };
        }

        @Override
        public float getAffinityShift(Affinity affinity){
            return 0;
        }
}
