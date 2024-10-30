package am2.items;

import akka.io.Tcp;
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
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import java.util.HashSet;
import java.util.Set;

public class ItemBoundPickaxe extends ItemPickaxe implements IBoundItem{

	public ItemBoundPickaxe(ToolMaterial par2ToolMaterial){
		super(par2ToolMaterial);
		this.setMaxDamage(0);
	}

	public ItemBoundPickaxe setUnlocalizedAndTextureName(String name){
		this.setUnlocalizedName(name);
		setTextureName(name);
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister){
		this.itemIcon = ResourceManager.RegisterTexture("bound_pickaxe", par1IconRegister);
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
		if(SpellUtils.instance.modifierIsPresent(SpellModifiers.MINING_POWER,spellstack)){
			int count = SpellUtils.instance.countModifiers(SpellModifiers.MINING_POWER, spellstack);
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
				props.deductMana(maintainCost(par1ItemStack));
			}else{
				BoundItemHandler.UnbindItem(par1ItemStack, (EntityPlayer)par3Entity, slotIndex);
			}
		}
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player){

		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(player.worldObj, player, false);

		if (mop != null){
			if (!itemstack.hasTagCompound())
				itemstack.setTagCompound(new NBTTagCompound());

			itemstack.stackTagCompound.setInteger("block_break_face", mop.sideHit);
		}else{
			//default to top
			itemstack.stackTagCompound.setInteger("block_break_face", 0);
		}

		return super.onBlockStartBreak(itemstack, X, Y, Z, player);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase living){

		if (!living.isSneaking() && stack.hasTagCompound() && this.getDigSpeed(stack, block, world.getBlockMetadata(x, y, z)) == this.efficiencyOnProperMaterial){
			ItemStack castStack = BoundItemHandler.getApplicationStack(stack);
			SpellHelper.instance.applyStackStage(castStack, living, null, x, y, z, stack.stackTagCompound.getInteger("block_break_face"), world, false, true, 0);
		}

		return super.onBlockDestroyed(stack, world, block, x, y, z, living);
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
	@SideOnly(Side.CLIENT)
	public boolean shouldRotateAroundWhenRendering(){
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D(){
		return true;
	}

	@Override
	public Set<String> getToolClasses(ItemStack stack){
		HashSet<String> set = new HashSet<String>();
		set.add("pickaxe");
		return set;
	}
}
