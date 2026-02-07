/**
 * @author ArcAnc
 * Created at: 21.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;


import com.arcanc.biomorphosis.content.block.multiblock.MultiblockTurret;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DamageHelper
{
	public static boolean mutationDamage(int amount, @NotNull LivingEntity target)
	{
		Level level = target.level();
		DamageSource source = new DamageSource(
				level.registryAccess().
						lookupOrThrow(Registries.DAMAGE_TYPE).
						getOrThrow(Registration.DamageTypeReg.IMPOSSIBLE_MUTATION));
		return dealDamage(source, amount, target);
	}
	
	public static boolean turretDamage(@NotNull MultiblockTurret turret, int amount, @NotNull LivingEntity target)
	{
		Level level = target.level();
		DamageSource source = new DamageSource(
				level.registryAccess().
						lookupOrThrow(Registries.DAMAGE_TYPE).
						getOrThrow(Registration.DamageTypeReg.TURRET_DAMAGE),
				Vec3.atLowerCornerOf(turret.getBlockPos()));
		return dealDamage(source, amount, target);
	}
	
	private static boolean dealDamage(DamageSource source, int amount, @NotNull Entity target)
	{
		Level level = target.level();
		if (!(level instanceof ServerLevel serverLevel))
			return target.hurtClient(source);
		return target.hurtServer(serverLevel, source, amount);
	}
}
