/**
 * @author ArcAnc
 * Created at: 12.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.particle;


import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.state.BlockState;

public class HiveDecoParticle extends TextureSheetParticle
{
	protected HiveDecoParticle(
			ClientLevel level,
			double x,
			double y,
			double z,
			double xSpeed,
			double ySpeed,
			double zSpeed)
	{
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
		
		this.xd = xSpeed;
		this.yd = ySpeed;
		this.zd = zSpeed;
		
		this.lifetime = 35;
		this.quadSize = 0.15f;
		this.gravity = 0;
		this.hasPhysics = false;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
		BlockState state = this.level.getBlockState(pos);
		if (state.is(Registration.BlockReg.HIVE_DECO))
			this.remove();
	}
	
	@Override
	public ParticleRenderType getRenderType()
	{
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	public static class Provider implements ParticleProvider.Sprite<SimpleParticleType>
	{
		@Override
		public TextureSheetParticle createParticle(
				SimpleParticleType type,
				ClientLevel level,
				double x,
				double y,
				double z,
				double xSpeed,
				double ySpeed,
				double zSpeed)
		{
			return new HiveDecoParticle(
					level,
					x,
					y,
					z,
					xSpeed,
					ySpeed,
					zSpeed
			);
		}
	}
}