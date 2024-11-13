package am2.buffs;

import net.minecraft.entity.EntityLivingBase;

public class BuffEffectTemporalAnchorBurnout extends BuffEffect{

	public BuffEffectTemporalAnchorBurnout( int duration, int amplifier){
		super(BuffList.temporalAnchorBurnout.id, duration, amplifier);
	}

	@Override
	public void applyEffect(EntityLivingBase entityliving){

	}

	@Override
	public void stopEffect(EntityLivingBase entityliving){

	}

	@Override
	protected String spellBuffName(){
		return "Temporal Anchor Burnout";
	}
}
