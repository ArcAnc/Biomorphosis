/**
 * @author ArcAnc
 * Created at: 02.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.render.block_entity.model.render_state;

import com.arcanc.biomorphosis.util.model.BlockEntityRenderState;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.world.entity.AnimationState;

public class LureCampfireRenderState extends BlockEntityRenderState
{
    public static final AnimationDefinition SHAFT_ROTATION = AnimationDefinition.Builder.withLength(0.5F)
            .addAnimation("shaft", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(-180.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(-360.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

    public final AnimationState rotateShaftAnimationState = new AnimationState();
}
