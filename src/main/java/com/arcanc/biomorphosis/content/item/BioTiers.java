/**
 * @author ArcAnc
 * Created at: 17.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioBlockTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class BioTiers
{
	public static final SimpleTier FLESH = new SimpleTier(BioBlockTags.INCORRECT_FOR_FLESH_TOOL,
			25,
			2.0f,
			1.0f,
			5,
			() -> Ingredient.of(Registration.ItemReg.FLESH_PIECE) );
}
