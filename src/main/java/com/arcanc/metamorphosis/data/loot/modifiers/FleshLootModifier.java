/**
 * @author ArcAnc
 * Created at: 14.10.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.data.loot.modifiers;


import com.arcanc.metamorphosis.content.entity.MetaEntityType;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class FleshLootModifier extends LootModifier
{
	public static final MapCodec<FleshLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
			LootModifier.codecStart(instance).apply(instance, FleshLootModifier::new));
	
	public FleshLootModifier(LootItemCondition[] conditions)
	{
		super(conditions);
	}
	
	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, @NotNull LootContext context)
	{
		if (!context.hasParameter(LootContextParams.THIS_ENTITY) || !context.hasParameter(LootContextParams.LAST_DAMAGE_PLAYER))
			return generatedLoot;
		
		Entity killed = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
		
		RandomSource random = context.getRandom();
		ItemStack flesh = new ItemStack(Registration.ItemReg.FLESH_PIECE.get(), random.nextInt(3));
		if (killed instanceof LivingEntity && !(killed.getType() instanceof MetaEntityType<?>))
			generatedLoot.add(flesh);
		return generatedLoot;
	}
	
	@Override
	public @NotNull MapCodec<? extends IGlobalLootModifier> codec()
	{
		return CODEC;
	}
}
