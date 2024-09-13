package am2.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class EnchantMagicResist extends Enchantment{

	public EnchantMagicResist(int effectID, int weight){
		super(effectID, weight, EnumEnchantmentType.armor);
		setName("magicresist");
	}

	@Override
	public int getMaxLevel(){
		return 5;
	}


}
