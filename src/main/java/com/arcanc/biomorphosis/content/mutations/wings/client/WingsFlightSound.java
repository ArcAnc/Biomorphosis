/**
 * @author ArcAnc
 * Created at: 21.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations.wings.client;

import com.arcanc.biomorphosis.content.mutations.types.WingsEffectType;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class WingsFlightSound extends AbstractTickableSoundInstance
{
	private final Player player;

	public WingsFlightSound(Player player)
	{
		super(Registration.SoundReg.WINGS_FLYING.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
		this.player = player;
		this.looping = true;
		this.delay = 0;
		this.volume = Math.nextAfter(0.0F, 1.0D);
	}

	@Override
	public void tick()
	{
		if (!this.player.isAlive() || !WingsEffectType.isFlying(this.player) || this.player.isInWaterOrBubble())
		{
			this.stop();
			return;
		}

		this.x = (float) this.player.getX();
		this.y = (float) this.player.getY();
		this.z = (float) this.player.getZ();
		float velocity = (float) this.player.getDeltaMovement().length();
		this.volume = Mth.clamp(0.15F + velocity * 0.35F, 0.12F, 0.85F);
		this.pitch = 1.0F + Mth.clamp(velocity * 0.08F, 0.0F, 0.25F);
	}

	public void stopFlight()
	{
		this.stop();
	}
}
