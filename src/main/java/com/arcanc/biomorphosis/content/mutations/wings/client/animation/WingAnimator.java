/**
 * @author ArcAnc
 * Created at: 23.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations.wings.client.animation;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Wing Animator was taken from {@link <a href="https://github.com/Fuzss/fantastic-wings">Fantastic Wings</a>}
 * <p>
 * Permission was obtained directly from @Fuzs
 */

public final class WingAnimator
{
	private static final Vec3[] REST_WINGS = pose(
			new Vec3(0, -23.5, -16), new Vec3(0, 13, 29), new Vec3(0, 12, -28), new Vec3(0, 4, 18.3));
	private static final Vec3[] REST_FEATHERS = pose(new Vec3(0, 0, 23.48));
	private Movement movement = new IdleMovement();
	private float previousFlapCycle;
	private float flapCycle;

	public void beginLand() { this.begin(new LandMovement(), 2); }
	public void beginGlide() { this.begin(new GlideMovement(), 60); }
	public void beginIdle() { this.begin(new IdleMovement(), 18); }
	public void beginLift() { this.begin(new LiftMovement(), 20); }
	public void beginFall() { this.begin(new FallMovement(), 8); }

	public void update(float speed)
	{
		this.previousFlapCycle = this.flapCycle;
		this.flapCycle += this.movement.update(speed);
	}

	public Vec3 getWingRotation(int index, float partialTick)
	{
		return this.movement.wing(index, partialTick);
	}

	public Vec3 getFeatherRotation(int index, float partialTick)
	{
		return this.movement.feather(index, partialTick);
	}

	private void begin(Movement next, int duration)
	{
		this.movement.cancel();
		this.movement = new Transition(this.movement, next, duration);
	}

	private float flap(float partialTick)
	{
		return Mth.lerp(partialTick, this.previousFlapCycle, this.flapCycle);
	}

	private static float weight(int index)
	{
		return Math.min(Math.abs(index - 1), 2) / 2.0F;
	}

	private static Vec3 at(Vec3[] values, int index)
	{
		return index < values.length ? values[index] : Vec3.ZERO;
	}

	private static Vec3[] pose(Vec3... values)
	{
		return values;
	}

	private interface Movement
	{
		Vec3 wing(int index, float partialTick);
		Vec3 feather(int index, float partialTick);
		float update(float speed);
		default void cancel() { }
	}

	private final class IdleMovement implements Movement
	{
		private static final float RATE = 0.035F;
		private static final Vec3[] WINGS = pose(
				new Vec3(40, -60, -50), new Vec3(72, 10, 100), new Vec3(0, -10, -120), new Vec3(10, 0, 100));
		private static final Vec3[] FEATHERS = pose(
				new Vec3(10, 20, 23.48), new Vec3(0, 20, -70), new Vec3(0, 10, 40), new Vec3(-20, 0, 20));
		@Override public Vec3 wing(int index, float delta) { return at(WINGS, index).add(0, Mth.sin(flap(delta)) * 3 * weight(index), 0); }
		@Override public Vec3 feather(int index, float delta) { return at(FEATHERS, index).add(0, -Mth.sin(flap(delta)) * 5 * weight(index), 0); }
		@Override public float update(float speed) { return RATE * speed; }
	}

	private final class GlideMovement implements Movement
	{
		private float time;
		@Override public Vec3 wing(int index, float delta) { return at(REST_WINGS, index).add(0, (Mth.sin(flap(delta)) * 5 - 14) * weight(index), 0); }
		@Override public Vec3 feather(int index, float delta) { return at(REST_FEATHERS, index).add(Mth.sin((this.time + delta) * .17F + index) * 1.25F, 0, 0); }
		@Override public float update(float speed) { this.time += speed; return .045F * speed; }
	}

	private final class LiftMovement implements Movement
	{
		private static final int RAMP_DURATION = 100;
		private float time;
		@Override public Vec3 wing(int index, float delta)
		{
			float position = weight(index);
			float cycle = flap(delta) - position * 1.2F;
			double x = (Math.sin(cycle + Math.PI / 2) - 1) / 2 * 16 + 8;
			double y = (Math.sin(cycle) * 26 + 12) * (1 - position * (Math.min(Math.sin(cycle + Math.PI), 0) / 2 + 1) * Math.sin(flap(delta)));
			return at(REST_WINGS, index).add(x, y, 0);
		}
		@Override public Vec3 feather(int index, float delta) { return at(REST_FEATHERS, index); }
		@Override public float update(float speed) { if (this.time < RAMP_DURATION) this.time += speed; return Mth.lerp(this.time / RAMP_DURATION, .375F, .225F) * speed; }
	}

	private final class LandMovement implements Movement
	{
		@Override public Vec3 wing(int index, float delta)
		{
			float position = weight(index + 1);
			float cycle = flap(delta) - position * 1.2F;
			double x = (Math.sin(cycle + Math.PI / 2) - 1) / 2 * 20 + (1 - position) * 50;
			double y = (Math.sin(cycle) * 20 + (1 - position) * 14) * (1 - position * (Math.min(Math.sin(cycle + Math.PI), 0) / 2 + 1) * Math.sin(flap(delta)));
			return at(REST_WINGS, index).add(x, y, 4);
		}
		@Override public Vec3 feather(int index, float delta) { return at(REST_FEATHERS, index); }
		@Override public float update(float speed) { return .67F * speed; }
	}

	private static final class FallMovement implements Movement
	{
		private static final Vec3[] WINGS = pose(new Vec3(30, -23, -50), new Vec3(-10, 5, -10), new Vec3(-30, -20, -20), new Vec3(-20, 0, 20));
		private float time;
		@Override public Vec3 wing(int index, float delta) { double n = Math.sin((this.time + delta) * .18 + index * .13) * .92 * (index + 1); return at(WINGS, index).add(n, 0, n); }
		@Override public Vec3 feather(int index, float delta) { double n = Math.sin((this.time + delta) * .2 + index * .13) * 1.75; return new Vec3(-n, n * 4, 0); }
		@Override public float update(float speed) { this.time += speed; return 0; }
	}

	private final class Transition implements Movement
	{
		private final Movement from;
		private final Movement to;
		private final int duration;
		private float previousTime;
		private float time;
		private boolean active = true;
		private Transition(Movement from, Movement to, int duration) { this.from = from; this.to = to; this.duration = duration; }
		@Override public Vec3 wing(int index, float delta) { return this.interpolate(this.from.wing(index, delta), this.to.wing(index, delta), delta); }
		@Override public Vec3 feather(int index, float delta) { return this.interpolate(this.from.feather(index, delta), this.to.feather(index, delta), delta); }
		private Vec3 interpolate(Vec3 from, Vec3 to, float delta)
		{
			float amount = -(Mth.cos(Mth.PI * Mth.lerp(delta, this.previousTime, this.time) / this.duration) - 1) / 2;
			return new Vec3(Mth.rotLerp(amount, (float) from.x, (float) to.x), Mth.rotLerp(amount, (float) from.y, (float) to.y), Mth.rotLerp(amount, (float) from.z, (float) to.z));
		}
		@Override public float update(float speed)
		{
			this.previousTime = this.time;
			float rate = Mth.lerp(this.time / this.duration, this.from.update(speed), this.to.update(speed));
			if ((this.time += speed) >= this.duration && this.active) WingAnimator.this.movement = this.to;
			return rate;
		}
		@Override public void cancel() { this.active = false; }
	}
}
