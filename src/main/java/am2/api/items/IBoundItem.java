package am2.api.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IBoundItem extends IManaItem{

	float diminishedMaintain = 0.1f;
	float normalMaintain = 0.4f;
	float augmentedMaintain = 1.0f;
}
