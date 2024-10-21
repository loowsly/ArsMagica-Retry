package am2.api.items;

import net.minecraft.item.ItemStack;

public interface IManaContainerItem{


	//Max amount of mana in item
	float getMaxMana();

	//current amount of mana in item
	public float getMana(ItemStack item);
}
