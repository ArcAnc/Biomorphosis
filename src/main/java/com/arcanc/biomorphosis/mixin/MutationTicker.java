/**
 * @author ArcAnc
 * Created at: 23.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin;


import com.arcanc.biomorphosis.content.mutations.GenomeEffectsHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MutationTicker
{
	@Inject(method = "tickEffects", at = @At("TAIL"))
	private void mutationTicker(CallbackInfo ci)
	{
		LivingEntity entity = (LivingEntity) (Object)this;
		if (!(entity.level() instanceof ServerLevel serverLevel))
			return;

		if (!(entity instanceof GenomeEffectsHolder holder))
			return;
		
		if (holder.biomorphosis$getGeneEffects().isEmpty())
			return;
		holder.biomorphosis$getGeneEffects().
				forEach(entry -> entry.type().tick(entity, entry.params()));
	}
}
