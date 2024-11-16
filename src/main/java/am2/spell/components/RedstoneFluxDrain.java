package am2.spell.components;

import am2.AMCore;
import am2.api.ArsMagicaApi;
import am2.api.spell.component.interfaces.ISpellComponent;
import am2.api.spell.enums.Affinity;
import am2.api.spell.enums.SpellModifiers;
import am2.items.ItemsCommonProxy;
import am2.particles.AMParticle;
import am2.particles.ParticleArcToEntity;
import am2.spell.SpellUtils;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;
import java.util.Random;

public class RedstoneFluxDrain implements ISpellComponent{

	@Override
	public boolean applyEffectBlock(ItemStack stack, World world, int blockx, int blocky, int blockz, int blockFace, double impactX, double impactY, double impactZ, EntityLivingBase caster){
		if (!(caster instanceof EntityPlayer)) return false;
		TileEntity te = world.getTileEntity(blockx, blocky, blockz);
		if (te != null && te instanceof IEnergyHandler) {
			int rfToSteal = SpellUtils.instance.getModifiedInt_Add(1000, stack, caster, caster, world, 0, SpellModifiers.DAMAGE);
			int rfToGiveBack = ((IEnergyHandler)te).extractEnergy(ForgeDirection.UNKNOWN, rfToSteal, false);
			giveBackEnergy((EntityPlayer) caster, rfToGiveBack);
			return true;
		}
		return false;
	}

	private void giveBackEnergy(EntityPlayer caster, int rfToGiveBack) {
		EntityPlayer cPlayer = caster;
		for (int i = 0; i < cPlayer.inventory.mainInventory.length; i++) {
			if (rfToGiveBack <= 0) {
				return;
			}
			if (cPlayer.inventory.mainInventory[i] != null && cPlayer.inventory.mainInventory[i].getItem() instanceof IEnergyContainerItem) {
				int inserted = ((IEnergyContainerItem)cPlayer.inventory.mainInventory[i].getItem()).receiveEnergy(cPlayer.inventory.mainInventory[i], rfToGiveBack, false);
				rfToGiveBack -= inserted;
			}
		}
		for (int i = 0; i < cPlayer.inventory.armorInventory.length; i++) {
			if (rfToGiveBack <= 0) {
				return;
			}
			if (cPlayer.inventory.armorInventory[i] != null && cPlayer.inventory.armorInventory[i].getItem() instanceof IEnergyContainerItem) {
				int inserted = ((IEnergyContainerItem)cPlayer.inventory.armorInventory[i].getItem()).receiveEnergy(cPlayer.inventory.armorInventory[i], rfToGiveBack, false);
				rfToGiveBack -= inserted;
			}
		}
		return;
	}

	@Override
	public boolean applyEffectEntity(ItemStack stack, World world, EntityLivingBase caster, Entity target){
		if (!(target instanceof EntityPlayer) || !(caster instanceof EntityPlayer)) return false;

		int rfToSteal = SpellUtils.instance.getModifiedInt_Add(1000, stack, caster, target, world, 0, SpellModifiers.DAMAGE);
		int rfToGiveBack = SpellUtils.instance.getModifiedInt_Add(1000, stack, caster, target, world, 0, SpellModifiers.DAMAGE);

		EntityPlayer player = (EntityPlayer)target;
		for (int i = 0; i < player.inventory.mainInventory.length; i++) {
			if (rfToSteal <= 0) {
				break;
			}
			if (player.inventory.mainInventory[i] != null && player.inventory.mainInventory[i].getItem() instanceof IEnergyContainerItem) {
				int extracted = ((IEnergyContainerItem)player.inventory.mainInventory[i].getItem()).extractEnergy(player.inventory.mainInventory[i], rfToSteal, false);
				rfToSteal -= extracted;
			}
		}
		for (int i = 0; i < player.inventory.armorInventory.length; i++) {
			if (rfToSteal <= 0) {
				break;
			}
			if (player.inventory.armorInventory[i] != null && player.inventory.armorInventory[i].getItem() instanceof IEnergyContainerItem) {
				int extracted = ((IEnergyContainerItem)player.inventory.armorInventory[i].getItem()).extractEnergy(player.inventory.armorInventory[i], rfToSteal, false);
				rfToSteal -= extracted;
			}
		}

		if (rfToSteal > 0) rfToGiveBack -= rfToSteal;

		giveBackEnergy((EntityPlayer) caster, rfToGiveBack);
		return true;
	}

	@Override
	public float manaCost(EntityLivingBase caster){
		return 20;
	}

	@Override
	public float burnout(EntityLivingBase caster){
		return ArsMagicaApi.getBurnoutFromMana(manaCost(caster));
	}

	@Override
	public void spawnParticles(World world, double x, double y, double z, EntityLivingBase caster, Entity target, Random rand, int colorModifier){
		for (int i = 0; i < 15; ++i){
			AMParticle particle = (AMParticle)AMCore.proxy.particleManager.spawn(world, "sparkle2", x, y, z);
			if (particle != null){
				particle.addRandomOffset(1, 1, 1);
				particle.setIgnoreMaxAge(true);
				particle.AddParticleController(new ParticleArcToEntity(particle, 1, caster, false).SetSpeed(0.03f).generateControlPoints());
				particle.setRGBColorF(0, 0.4f, 1);
				if (colorModifier > -1){
					particle.setRGBColorF(((colorModifier >> 16) & 0xFF) / 255.0f, ((colorModifier >> 8) & 0xFF) / 255.0f, (colorModifier & 0xFF) / 255.0f);
				}
			}
		}
	}

	@Override
	public EnumSet<Affinity> getAffinity(){
		return EnumSet.of(Affinity.LIGHTNING, Affinity.WATER);
	}

	@Override
	public int getID(){
		return 81;
	}

	@Override
	public Object[] getRecipeItems(){
		return new Object[]{
				new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_CYAN),
				new ItemStack(Items.water_bucket),
				new ItemStack(Items.diamond),
				new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_VINTEUMDUST),
		};
	}

	@Override
	public float getAffinityShift(Affinity affinity){
		return 0.01f;
	}
}
