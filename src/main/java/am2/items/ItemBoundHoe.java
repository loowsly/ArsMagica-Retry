package am2.items;

import am2.api.items.BoundItemHandler;
import am2.api.items.IBoundItem;
import am2.api.items.ManaItemHandler;
import am2.api.spell.enums.SpellModifiers;
import am2.playerextensions.ExtendedProperties;
import am2.spell.SpellHelper;
import am2.spell.SpellUtils;
import am2.texture.ResourceManager;
import am2.utility.InventoryUtilities;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemBoundHoe extends ItemHoe implements IBoundItem{

	public ItemBoundHoe(ToolMaterial par2ToolMaterial){
		super(par2ToolMaterial);
		this.setMaxDamage(0);
	}

	public ItemBoundHoe setUnlocalizedAndTextureName(String name){
		this.setUnlocalizedName(name);
		setTextureName(name);
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister){
		this.itemIcon = ResourceManager.RegisterTexture("bound_hoe", par1IconRegister);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack par1ItemStack){
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack par1ItemStack){
		return EnumRarity.rare;
	}

	@Override
	public boolean getIsRepairable(ItemStack par1ItemStack,
								   ItemStack par2ItemStack){
		return false;
	}

	@Override
	public int getItemEnchantability(){
		return 0;
	}

	@Override
	public boolean isItemTool(ItemStack par1ItemStack){
		return true;
	}

	@Override
	public boolean isRepairable(){
		return false;
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player){
		BoundItemHandler.UnbindItem(item, player, player.inventory.currentItem);
		return false;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int slotIndex, boolean par5){
		if (par3Entity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)par3Entity;
			if(player.getHeldItem() == null || player.getHeldItem().getItem() != this || player.capabilities.isCreativeMode) return;
			ExtendedProperties props = ExtendedProperties.For(player);
			if (ManaItemHandler.canExtractMana(par1ItemStack, player,maintainCost(par1ItemStack))){
				props.deductMana(this.maintainCost(par1ItemStack));
			}else{

				BoundItemHandler.UnbindItem(par1ItemStack, (EntityPlayer)par3Entity, slotIndex);

			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
		MovingObjectPosition mop = ItemsCommonProxy.spell.getMovingObjectPosition(player, world, 4.0f, true, false);

		if (mop != null && stack.hasTagCompound()){
			ItemStack castStack = BoundItemHandler.getApplicationStack(stack);
			if (mop.typeOfHit == MovingObjectType.BLOCK)
				SpellHelper.instance.applyStackStage(castStack, player, null, mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord, mop.sideHit, world, true, true, 0);
			else if (mop.typeOfHit == MovingObjectType.ENTITY && mop.entityHit instanceof EntityLivingBase)
				SpellHelper.instance.applyStackStage(castStack, player, (EntityLivingBase)mop.entityHit, mop.entityHit.posX, mop.entityHit.posY, mop.entityHit.posZ, mop.sideHit, world, true, true, 0);
		}

		return super.onItemRightClick(stack, world, player);
	}

	@Override
	public float maintainCost(ItemStack stack){
		ItemStack spellstack = BoundItemHandler.getSpellStack(stack);
		if(SpellUtils.instance.modifierIsPresent(SpellModifiers.MINING_POWER,spellstack)){
			int count = SpellUtils.instance.countModifiers(SpellModifiers.MINING_POWER, spellstack);
			return IBoundItem.normalMaintain - ((2*count)/10);
		}
		return IBoundItem.normalMaintain;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face, float hitX, float hitY, float hitZ){
		boolean b = super.onItemUse(stack, player, world, x, y, z, face, hitX, hitY, hitZ);

		if (!player.isSneaking() && b){
			ItemStack castStack = BoundItemHandler.getApplicationStack(stack);
			SpellHelper.instance.applyStackStage(castStack, player, null, x, y, z, face, world, false, true, 0);
		}

		return b;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRotateAroundWhenRendering(){
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D(){
		return true;
	}
}
