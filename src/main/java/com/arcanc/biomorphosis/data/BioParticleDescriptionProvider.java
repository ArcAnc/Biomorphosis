/**
 * @author ArcAnc
 * Created at: 12.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;

public class BioParticleDescriptionProvider extends ParticleDescriptionProvider
{
	public BioParticleDescriptionProvider(PackOutput output, ExistingFileHelper fileHelper)
	{
		super(output, fileHelper);
	}
	
	@Override
	protected void addDescriptions()
	{
		sprite(Registration.ParticleReg.HIVE_DECO.get(), Database.rl("hive_deco_fly"));
	}
}
