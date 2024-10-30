package am2.items;

import am2.api.items.BoundItemHandler;
import am2.api.items.IBoundItem;
import am2.api.items.ManaItemHandler;
import am2.api.spell.enums.SpellModifiers;
import am2.playerextensions.ExtendedProperties;
import am2.spell.SpellHelper;
import am2.spell.SpellUtils;
import am2.texture.ResourceManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;

public class ItemBoundSword extends ItemSword implements IBoundItem{

	public ItemBoundSword(ToolMaterial par2ToolMaterial){
		super(par2ToolMaterial);
		this.setMaxDamage(0);
	}

	public ItemBoundSword setUnlocalizedAndTextureName(String name){
		this.setUnlocalizedName(name);
		setTextureName(name);
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister){
		this.itemIcon = ResourceManager.RegisterTexture("bound_sword", par1IconRegister);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack par1ItemStack){
		return EnumRarity.rare;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack par1ItemStack){
		return true;
	}

	@Override
	public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack){
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
	public float maintainCost(ItemStack stack){
		ItemStack spellstack = BoundItemHandler.getSpellStack(stack);
		if(SpellUtils.instance.modifierIsPresent(SpellModifiers.DAMAGE,spellstack)){
			int count = SpellUtils.instance.countModifiers(SpellModifiers.DAMAGE, spellstack);
			return IBoundItem.normalMaintain - ((2*count)/10);
		}
		return IBoundItem.normalMaintain;
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
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity){

		if (!player.isSneaking() && stack.hasTagCompound() && entity instanceof EntityLivingBase){
			ItemStack castStack = BoundItemHandler.getApplicationStack(stack);
			SpellHelper.instance.applyStackStage(castStack, player, (EntityLivingBase)entity, entity.posX, entity.posY, entity.posZ, 0, player.worldObj, false, true, 0);
		}
		return super.onLeftClickEntity(stack, player, entity);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count){

		if (stack.hasTagCompound()){
			ItemStack castStack = BoundItemHandler.getApplicationStack(stack);
			SpellHelper.instance.applyStackStageOnUsing(castStack, player, null, player.posX, player.posY, player.posZ, player.worldObj, true, true, count);
		}
		super.onUsingTick(stack, player, count);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRotateAroundWhenRendering(){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D(){
		return true;
	}
}
