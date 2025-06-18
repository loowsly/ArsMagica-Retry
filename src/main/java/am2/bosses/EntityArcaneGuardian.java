package am2.bosses;

import am2.bosses.ai.EntityAICastSpell;
import am2.bosses.ai.EntityAIDispel;
import am2.bosses.ai.ISpellCastCallback;
import am2.buffs.BuffList;
import am2.items.ItemEssence;
import am2.items.ItemRune;
import am2.items.ItemsCommonProxy;
import am2.network.AMNetHandler;
import am2.playerextensions.ExtendedProperties;
import am2.utility.NPCSpells;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import static am2.playerextensions.ExtendedProperties.UPD_CURRENT_MANA_FATIGUE;

public class EntityArcaneGuardian extends AM2Boss{

	private float runeRotationZ = 0;
	private float runeRotationY = 0;
	private int RetaliationCD = 0;

	private static final int DW_TARGET_ID = 20;

	public EntityArcaneGuardian(World par1World){
		super(par1World);
		this.setSize(1.0f, 3.0f);
	}

	@Override
	protected void entityInit(){
		super.entityInit();
		this.dataWatcher.addObject(DW_TARGET_ID, -1);
	}

	@Override
	public void onDeath(DamageSource src)
	{
		super.onDeath(src);
		if (src.getEntity() != null && src.getEntity() instanceof EntityPlayer) {
			EntityPlayer p = (EntityPlayer)src.getEntity();
			if (!worldObj.isRemote){
				ExtendedProperties.For(p).guardian4 = true;
				ExtendedProperties.For(p).setUpdateFlag(UPD_CURRENT_MANA_FATIGUE);
				ExtendedProperties.For(p).addMagicXP(40);
			}
		}
	}

	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(305D);
	}

	@Override
	public void onUpdate(){

		if (this.motionY < 0){
			this.motionY *= 0.7999999f;
		}
		if(RetaliationCD > 0){
			RetaliationCD--;
		}
		updateRotations();

		if (!worldObj.isRemote){
			int eid = this.dataWatcher.getWatchableObjectInt(DW_TARGET_ID);
			int tid = -1;
			if (this.getAttackTarget() != null){
				tid = this.getAttackTarget().getEntityId();
			}
			if (eid != tid){
				this.dataWatcher.updateObject(DW_TARGET_ID, tid);
			}

		}

		super.onUpdate();
	}

	private void updateRotations(){
		runeRotationZ = 0;
		float targetRuneRotationY = 0;
		float runeRotationSpeed = 0.3f;
		if (this.getTarget() != null){
			double deltaX = this.getTarget().posX - this.posX;
			double deltaZ = this.getTarget().posZ - this.posZ;

			double angle = Math.atan2(deltaZ, deltaX);

			angle -= Math.toRadians(MathHelper.wrapAngleTo180_float(this.rotationYaw + 90) + 180);

			targetRuneRotationY = (float)angle;
			runeRotationSpeed = 0.085f;
		}

		if (targetRuneRotationY > runeRotationY)
			runeRotationY += runeRotationSpeed;
		else if (targetRuneRotationY < runeRotationY)
			runeRotationY -= runeRotationSpeed;

		if (isWithin(runeRotationY, targetRuneRotationY, 0.25f)){
			runeRotationY = targetRuneRotationY;
		}

	}
	@Override
	public void addPotionEffect(PotionEffect effect){
		if ((effect.getPotionID() == BuffList.silence.id)){
			super.addPotionEffect(effect);
		}

	}
	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2){
		if (par1DamageSource.getSourceOfDamage() == null){
			return super.attackEntityFrom(par1DamageSource, par2);
		}

		if (checkRuneRetaliation(par1DamageSource, par2)){
			return super.attackEntityFrom(par1DamageSource, par2);
		}
		return super.attackEntityFrom(par1DamageSource, par2 * 0.8F);

	}

	private boolean checkRuneRetaliation(DamageSource damagesource, float par2){
		Entity source = damagesource.getSourceOfDamage();
		if (source instanceof EntityArcaneGuardian) {
			return true;
		}

		double deltaX = source.posX - this.posX;
		double deltaZ = source.posZ - this.posZ;

		double angle = Math.atan2(deltaZ, deltaX);

		angle -= Math.toRadians(MathHelper.wrapAngleTo180_float(this.rotationYaw + 90) + 180);

		float targetRuneRotationY = (float)angle;

		if (isWithin(runeRotationY, targetRuneRotationY, 0.5f)){
			if (this.getDistanceSqToEntity(source) < 9){
				double speed = 2.5;
				double vertSpeed = 0.325;

				deltaZ = source.posZ - this.posZ;
				deltaX = source.posX - this.posX;
				angle = Math.atan2(deltaZ, deltaX);

				double radians = angle;

				if (source instanceof EntityPlayer){
					AMNetHandler.INSTANCE.sendVelocityAddPacket(source.worldObj, (EntityLivingBase)source, speed * Math.cos(radians), vertSpeed, speed * Math.sin(radians));
				}
				source.motionX = (speed * Math.cos(radians));
				source.motionZ = (speed * Math.sin(radians));
				source.motionY = vertSpeed;
				if(RetaliationCD == 0){
					source.attackEntityFrom(DamageSource.causeMobDamage(this), Math.min(2,par2/10));
					RetaliationCD = 60;
					return false;
				}
			}
		}
		return true;
	}

	@Override
	protected float modifyDamageAmount(DamageSource source, float damageAmt){
		if (this.isPotionActive(BuffList.silence.id)){
			damageAmt *= 2;
		}
		return damageAmt;
	}
	@Override
	protected int modifyHurtTime(DamageSource source, int Hurttime){
		return Hurttime;
	}
	private boolean isWithin(float source, float target, float tolerance){
		return source + tolerance > target && source - tolerance < target;
	}

	public Entity getTarget(){
		int eid = this.dataWatcher.getWatchableObjectInt(DW_TARGET_ID);
		if (eid == -1) return null;
		return this.worldObj.getEntityByID(eid);
	}

	public float getRuneRotationZ(){
		return runeRotationZ;
	}

	public float getRuneRotationY(){
		return runeRotationY;
	}

	@Override
	protected void initSpecificAI(){
		this.tasks.addTask(1, new EntityAIDispel(this));
		this.tasks.addTask(1, new EntityAICastSpell(this, NPCSpells.instance.healSelf, 16, 23, 60, BossActions.CASTING, new ISpellCastCallback<EntityArcaneGuardian>(){
			@Override
			public boolean shouldCast(EntityArcaneGuardian host, ItemStack spell){
				return host.getHealth() < host.getMaxHealth();
			}
		}));
		this.tasks.addTask(2, new EntityAICastSpell(this, NPCSpells.instance.blink, 16, 23, 20, BossActions.CASTING));
		this.tasks.addTask(3, new EntityAICastSpell(this, NPCSpells.instance.arcaneBolt, 12, 23, 5, BossActions.CASTING));
	}

	@Override
	public void setCurrentAction(BossActions action){
		super.setCurrentAction(action);
		if (!worldObj.isRemote){
			AMNetHandler.INSTANCE.sendActionUpdateToAllAround(this);
		}
	}

	@Override
	public int getTotalArmorValue(){
		return 9;
	}

	@Override
	protected void dropFewItems(boolean par1, int par2){
		if (par1)
			this.entityDropItem(new ItemStack(ItemsCommonProxy.rune, 1, ItemRune.META_INF_ORB_GREEN), 0.0f);

		int i = rand.nextInt(4);

		for (int j = 0; j < i; j++){
			this.entityDropItem(new ItemStack(ItemsCommonProxy.essence, 1, ItemEssence.META_ARCANE), 0.0f);
		}

		i = rand.nextInt(10);

		if (i < 3 && par1){
			this.entityDropItem(ItemsCommonProxy.arcaneSpellBookEnchanted.copy(), 0.0f);
		}
	}

	@Override
	protected void fall(float par1){

	}

	@Override
	protected String getHurtSound(){
		return "arsmagica2:mob.arcaneguardian.hit";
	}

	@Override
	protected String getDeathSound(){
		return "arsmagica2:mob.arcaneguardian.death";
	}

	@Override
	protected String getLivingSound(){
		return "arsmagica2:mob.arcaneguardian.idle";
	}

	@Override
	public String getAttackSound(){
		return "arsmagica2:mob.arcaneguardian.spell";
	}
}
