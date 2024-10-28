package am2.api.items;

import am2.api.spell.enums.SpellModifiers;
import am2.items.ItemsCommonProxy;
import am2.spell.SpellUtils;
import am2.utility.InventoryUtilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class BoundItemHandler extends Item{

	public static void UnbindItem(ItemStack itemstack, EntityPlayer player, int inventoryIndex){
		if(!(itemstack.getItem() instanceof IBoundItem)){
			return;
		}
		itemstack = InventoryUtilities.replaceBoundItem(itemstack);
		player.inventory.setInventorySlotContents(inventoryIndex, itemstack);
	}

	public static ItemStack getApplicationStack(ItemStack boundStack){
		ItemStack castStack = SpellUtils.instance.constructSpellStack(boundStack.copy());
		castStack = SpellUtils.instance.popStackStage(castStack);
		castStack = InventoryUtilities.replaceItem(castStack, ItemsCommonProxy.spell);

		return castStack;
	}
}
