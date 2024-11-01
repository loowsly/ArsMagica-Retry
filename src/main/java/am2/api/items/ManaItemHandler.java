package am2.api.items;

import am2.api.events.ManaUsageEvent;
import am2.playerextensions.ExtendedProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

public class ManaItemHandler {

	/**
	 * @param item			the item using mana.
	 * @param player    	the entity using the item.
	 * @param manaAmount    the mana used.
	 * @return the amount of mana it uses.
	 * return the amount of mana used (Still WIP!).
	 */
	public static float UseMana(ItemStack item, EntityPlayer player,float manaAmount){
		if(item == null || player == null || player.capabilities.isCreativeMode){
			return 0;
		}
		ExtendedProperties Eplayer = ExtendedProperties.For(player);
		float currentmana = Eplayer.getCurrentMana() + Eplayer.getBonusCurrentMana() + Eplayer.getStoredItemMana();
		if(manaAmount > currentmana){
			return 0;
		}else{
			ManaUsageEvent mue = new ManaUsageEvent(item, player, manaAmount);
			MinecraftForge.EVENT_BUS.post(mue);

			manaAmount = mue.amount;
			return manaAmount;
		}
	}
	/**
	 * @param item			the item using mana.
	 * @param player    	the entity using the item.
	 * @param manaAmount    the mana it should use.
	 * @return true if it can extract mana.
	 * Used to check if item can use mana.
	 */
	public static boolean canExtractMana(ItemStack item, EntityPlayer player,float manaAmount){
		if(!(item.getItem() instanceof IManaItem))
			return false;
		if(player.capabilities.isCreativeMode || manaAmount == 0)
			return true;
		return UseMana(item, player, manaAmount) != 0;
	}
	/**
	 *
	 * @param item			the item using mana.
	 *
	 * @param manaAmount    the mana being added.
	 * <br>
	 * Use this to add mana to a {@link IManaContainerItem}
	 */
	public static void AddMana(ItemStack item, float manaAmount){
		if(item.getItem() instanceof IManaContainerItem){
			if (!item.hasTagCompound())
				item.stackTagCompound = new NBTTagCompound();
			IManaContainerItem itemMana = (IManaContainerItem)item.getItem();
			if (!ManaItemHandler.isFull(item)){
				float amt = itemMana.getMana(item) + manaAmount;
				ManaItemHandler.SetMana(item, amt);
			}
		}
	}
	/**
	 *
	 * @param item			the item using mana.
	 *
	 * @param manaAmount    the mana being removed.
	 * <br>
	 * Use this to remove mana from a {@link IManaContainerItem}
	 */
	public static void RemoveMana(ItemStack item, float manaAmount){
		if(item.getItem() instanceof IManaContainerItem){
			if (item.hasTagCompound()){
				IManaContainerItem itemMana = (IManaContainerItem)item.getItem();
				if(itemMana.getMana(item) > 0){
					float newmana = itemMana.getMana(item) - manaAmount;
					if (newmana > 0)
						ManaItemHandler.SetMana(item, newmana);
				}else
					ManaItemHandler.SetMana(item, 0);

			}
		}
	}

	/**
	 *
	 * @param item			the item using mana.
	 *
	 * @param amount    the mana being removed.
	 * <br>
	 * Use this to set the mana of a {@link IManaContainerItem}
	 */
	public static void SetMana(ItemStack item,float amount){
		if(item.getItem() instanceof IManaContainerItem){
			if (!item.hasTagCompound())
				item.stackTagCompound = new NBTTagCompound();
			float amt = Math.min(((IManaContainerItem)item.getItem()).getMaxMana(), amount);
			item.stackTagCompound.setFloat("Stored_Mana", amt);
		}
	}
	/**
	 *
	 * @param item			the item using mana.
	 * @return return true if the container is full.
	 * <br>
	 * Use this to check is the mana container is full.
	 */
	public static boolean isFull(ItemStack item){
		if(item.getItem() instanceof IManaContainerItem){
			return ((IManaContainerItem)item.getItem()).getMana(item) == ((IManaContainerItem)item.getItem()).getMaxMana();
		}
		return false;
	}
}
