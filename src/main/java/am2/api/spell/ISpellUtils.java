package am2.api.spell;

import am2.api.spell.enums.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ISpellUtils{

	double getModifiedDouble_Mul(double defaultValue, ItemStack stack, EntityLivingBase caster, Entity target, World world, SpellModifiers check);

	int getModifiedInt_Mul(int defaultValue, ItemStack stack, EntityLivingBase caster, Entity target, World world, SpellModifiers check);

	double getModifiedDouble_Mul(SpellModifiers check, ItemStack stack, EntityLivingBase caster, Entity target, World world);

	int getModifiedInt_Mul(SpellModifiers check, ItemStack stack, EntityLivingBase caster, Entity target, World world);

	double getModifiedDouble_Add(double defaultValue, ItemStack stack, EntityLivingBase caster, Entity target, World world, SpellModifiers check);

	int getModifiedInt_Add(int defaultValue, ItemStack stack, EntityLivingBase caster, Entity target, World world, SpellModifiers check);

	double getModifiedDouble_Add(SpellModifiers check, ItemStack stack, EntityLivingBase caster, Entity target, World world);

	int getModifiedInt_Add(SpellModifiers check, ItemStack stack, EntityLivingBase caster, Entity target, World world);

	boolean modifierIsPresent(SpellModifiers check, ItemStack stack);

	int countModifiers(SpellModifiers check, ItemStack stack);

}
