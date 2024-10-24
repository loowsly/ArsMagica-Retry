package am2.api.events;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ManaUsageEvent extends Event{
	public final ItemStack item;
	public final EntityPlayer entity;
	public final float amount;

	public ManaUsageEvent(ItemStack item, EntityPlayer player, float amount){
		this.item = item;
		this.entity = player;
		this.amount = amount;
	}

}
