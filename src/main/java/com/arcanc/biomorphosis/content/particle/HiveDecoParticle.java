/**
 * @author ArcAnc
 * Created at: 12.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.particle;


import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class HiveDecoParticle extends TextureSheetParticle
{
	private final double originX;
	private final double originY;
	private final double originZ;
	private final double initialAngle;
	private final double angularSpeed;
	private final double radialSpeed;
	private final double verticalSpeed;

	protected HiveDecoParticle(
			ClientLevel level,
			double x,
			double y,
			double z,
			double initialAngle,
			double angularSpeed,
			double radialSpeed)
	{
		super(level, x, y, z);

		this.originX = x;
		this.originY = y;
		this.originZ = z;
		this.initialAngle = initialAngle;
		this.angularSpeed = angularSpeed;
		this.radialSpeed = radialSpeed;
		this.verticalSpeed = (this.random.nextDouble() - 0.5d) * 0.015d;

		this.lifetime = 250;
		this.quadSize = 0.2f;
		this.gravity = 0;
		this.hasPhysics = false;
	}
	
	@Override
	public void tick()
	{
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime)
		{
			this.remove();
			return;
		}

		double radius = this.radialSpeed * this.age;
		double angle = this.initialAngle + this.angularSpeed * this.age;
		this.setPos(
				this.originX + Math.cos(angle) * radius,
				this.originY + this.verticalSpeed * this.age,
				this.originZ + Math.sin(angle) * radius);
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
