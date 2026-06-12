/**
 * @author ArcAnc
 * Created at: 17.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class BioSoundsProvider extends SoundDefinitionsProvider
{
 	public BioSoundsProvider(PackOutput output, ExistingFileHelper existingFileHelper)
	{
		super(output, Database.MOD_ID, existingFileHelper);
	}

	@Override
	public void registerSounds()
	{
		add(Registration.SoundReg.BLOCK_MORPH_COMPLETE, SoundDefinition.definition().
				with(sound(Database.rl("block/morph_complete"), SoundDefinition.SoundType.SOUND)).
				subtitle(Database.GUI.Sounds.MULTIBLOCK_MORPH_COMPLETE));
		
		add(Registration.SoundReg.ADVANCEMENT, SoundDefinition.definition().
				with(sound(Database.rl("advancement/0"), SoundDefinition.SoundType.SOUND)).
				subtitle(Database.GUI.Sounds.ADVANCEMENT_RECEIVE));
		
		add(Registration.SoundReg.BLOCK_DESTROY, SoundDefinition.definition().
				with(
						sound(Database.rl("block/destroy_0"), SoundDefinition.SoundType.SOUND)).
				with(
						sound(Database.rl("block/destroy_1"), SoundDefinition.SoundType.SOUND)).
				subtitle(Database.GUI.Sounds.BLOCK_DESTROYED));

		add(Registration.SoundReg.BLOCK_PLACE, SoundDefinition.definition().
				with(
						sound(Database.rl("block/place_0"), SoundDefinition.SoundType.SOUND)).
				with(
						sound(Database.rl("block/place_1"), SoundDefinition.SoundType.SOUND)).
				subtitle(Database.GUI.Sounds.BLOCK_PLACED));
		add(Registration.SoundReg.BLOCK_STEP_NORMAL, SoundDefinition.definition().
				with(
						sound(Database.rl("block/step_0"), SoundDefinition.SoundType.SOUND)).
				with(
						sound(Database.rl("block/step_1"), SoundDefinition.SoundType.SOUND)).
				subtitle(Database.GUI.Sounds.BLOCK_STEP_NORMAL));
		add(Registration.SoundReg.BLOCK_STEP_TRAMPLED, SoundDefinition.definition().
				with(
						sound(Database.rl("block/step_trampled_0"), SoundDefinition.SoundType.SOUND)).
				with(
						sound(Database.rl("block/step_trampled_1"), SoundDefinition.SoundType.SOUND)).
				with(
						sound(Database.rl("block/step_trampled_2"), SoundDefinition.SoundType.SOUND)).
				with(
						sound(Database.rl("block/step_trampled_3"), SoundDefinition.SoundType.SOUND)).
				subtitle(Database.GUI.Sounds.BLOCK_STEP_TRAMPLED));
		add(Registration.SoundReg.BLOCK_STEP_LEAF, SoundDefinition.definition().
				with(
						sound(Database.rl("block/step_leaf_0"), SoundDefinition.SoundType.SOUND)).
				with(
						sound(Database.rl("block/step_leaf_1"), SoundDefinition.SoundType.SOUND)).
				with(
						sound(Database.rl("block/step_leaf_2"), SoundDefinition.SoundType.SOUND)).
				with(
						sound(Database.rl("block/step_leaf_3"), SoundDefinition.SoundType.SOUND)).
				subtitle(Database.GUI.Sounds.BLOCK_STEP_LEAF));

		add(Registration.SoundReg.BLOCK_CHEST_OPEN, SoundDefinition.definition().
				with(
						sound(Database.rl("block/chest_open"), SoundDefinition.SoundType.SOUND)).
				subtitle(Database.GUI.Sounds.BLOCK_CHEST_OPEN));
		add(Registration.SoundReg.BLOCK_HIVE, SoundDefinition.definition().
				with(
						sound(Database.rl("block/hive_deco"), SoundDefinition.SoundType.SOUND)).
				subtitle(Database.GUI.Sounds.BLOCK_HIVE_DECO));

		add(Registration.SoundReg.BLOCK_CHEST_CLOSE, SoundDefinition.definition().
				with(
						sound(Database.rl("block/chest_close"), SoundDefinition.SoundType.SOUND)).
				subtitle(Database.GUI.Sounds.BLOCK_CHEST_CLOSE));

		addEntitySound(Registration.EntityReg.MOB_QUEEN.getSounds(), Database.GUI.Sounds.QUEEN);
		addEntitySound(Registration.EntityReg.MOB_KSIGG.getSounds(), Database.GUI.Sounds.KSIGG);
		addEntitySound(Registration.EntityReg.MOB_LARVA.getSounds(), Database.GUI.Sounds.LARVA, 3, 1, 1);
		addEntitySound(Registration.EntityReg.MOB_ZIRIS.getSounds(), Database.GUI.Sounds.ZIRIS);
		addEntitySound(Registration.EntityReg.MOB_INFESTOR.getSounds(), Database.GUI.Sounds.INFESTOR);
		addEntitySound(Registration.EntityReg.MOB_SWARMLING.getSounds(), Database.GUI.Sounds.SWARMLING, 2, 1, 1);
		addEntitySound(Registration.EntityReg.MOB_QUEEN_GUARD.getSounds(), Database.GUI.Sounds.GUARD);
		addEntitySound(Registration.EntityReg.MOB_WORKER.getSounds(), Database.GUI.Sounds.WORKER);
		addEntitySound(Registration.EntityReg.MOB_MELON_MAW.getSounds(), Database.GUI.Sounds.MELONMAW, 3, 1, 3);
		
		add(Registration.EntityReg.MOB_BASE_CAPTAIN.getSounds().getDeathSound(), SoundDefinition.definition().
				with(sound(Database.rl("captain/death"), SoundDefinition.SoundType.SOUND)).
				subtitle(Database.GUI.Sounds.CAPTAIN.getDeath()));
		add(Registration.EntityReg.MOB_BASE_CAPTAIN.getSounds().getHurtSound(), SoundDefinition.definition().
				with(sound(Database.rl("captain/hurt_0"), SoundDefinition.SoundType.SOUND)).
				with(sound(Database.rl("captain/hurt_1"), SoundDefinition.SoundType.SOUND)).
				with(sound(Database.rl("captain/hurt_2"), SoundDefinition.SoundType.SOUND)).
				with(sound(Database.rl("captain/hurt_3"), SoundDefinition.SoundType.SOUND)).
				subtitle(Database.GUI.Sounds.CAPTAIN.getHurt()));
		add(Registration.EntityReg.MOB_BASE_CAPTAIN.getSounds().getIdleSound(), SoundDefinition.definition().
				with(sound(Database.rl("captain/idle_0"), SoundDefinition.SoundType.SOUND).weight(200)).
				with(sound(Database.rl("captain/idle_1"), SoundDefinition.SoundType.SOUND).weight(200)).
				with(sound(Database.rl("captain/idle_2"), SoundDefinition.SoundType.SOUND).weight(200)).
				with(sound(Database.rl("captain/idle_3"), SoundDefinition.SoundType.SOUND).weight(200)).
				with(sound(Database.rl("captain/idle_4"), SoundDefinition.SoundType.SOUND).weight(40)).
				with(sound(Database.rl("captain/idle_5"), SoundDefinition.SoundType.SOUND).weight(40)).
				with(sound(Database.rl("captain/idle_6"), SoundDefinition.SoundType.SOUND).weight(40)).
				with(sound(Database.rl("captain/idle_7"), SoundDefinition.SoundType.SOUND).weight(200)).
				with(sound(Database.rl("captain/idle_8"), SoundDefinition.SoundType.SOUND).weight(200)).
				with(sound(Database.rl("captain/idle_rare"), SoundDefinition.SoundType.SOUND).weight(1)).
				subtitle(Database.GUI.Sounds.CAPTAIN.getIdle()));
	}

	private void addEntitySound(Registration.EntityReg.EntitySoundEntry entry, Database.GUI.Sounds.EntitySoundSubtitle subtitle)
	{
		this.addEntitySound(entry, subtitle, 1,1,1);
	}

	private void addEntitySound(Registration.EntityReg.EntitySoundEntry entry, Database.GUI.Sounds.EntitySoundSubtitle subtitle, int idleCount, int deathCount, int hurtCount)
	{
		SoundDefinition definition = SoundDefinition.definition().subtitle(subtitle.getIdle());
		if (idleCount == 1)
			definition.with(sound(Database.rl(entry.getName() + "/idle")));
		else
			for (int q = 0; q < idleCount; q++)
				definition.with(sound(Database.rl(entry.getName() + "/idle_" + q)));

		add(entry.getIdleSound(), definition);

		definition = SoundDefinition.definition().subtitle(subtitle.getDeath());
		if (deathCount == 1)
			definition.with(sound(Database.rl(entry.getName() + "/death")));
		else
			for (int q = 0; q < deathCount; q++)
				definition.with(sound(Database.rl(entry.getName() + "/death_" + q)));
		add(entry.getDeathSound(), definition);

		definition = SoundDefinition.definition().subtitle(subtitle.getHurt());
		if (hurtCount == 1)
			definition.with(sound(Database.rl(entry.getName() + "/hurt")));
		else
			for (int q = 0; q < hurtCount; q++)
				definition.with(sound(Database.rl(entry.getName() + "/hurt_" + q)));
		add(entry.getHurtSound(), definition);
	}
}
