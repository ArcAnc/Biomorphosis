/**
 * @author ArcAnc
 * Created at: 02.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability;

import net.minecraft.world.entity.LivingEntity;

public record MobAbilityCastContext(LivingEntity caster, LivingEntity target, IAbility ability)
{
}
