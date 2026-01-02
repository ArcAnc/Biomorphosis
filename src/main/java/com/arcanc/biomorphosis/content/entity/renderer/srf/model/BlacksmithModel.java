/**
 * @author ArcAnc
 * Created at: 02.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer.srf.model;

import com.arcanc.biomorphosis.content.entity.renderer.srf.model.renderState.BlacksmithRenderState;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

public class BlacksmithModel extends HumanoidModel<BlacksmithRenderState>
{
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Database.rl("blacksmith"), "main");

	public BlacksmithModel(ModelPart root)
	{
		super(root, RenderType :: entityCutout);
		this.hat.visible = false;
	}

	public static @NotNull LayerDefinition createMesh()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition root = meshdefinition.getRoot();

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().
						texOffs(0, 0).
						addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).
						texOffs(0, 55).
						addBox(-4.0F, -9.0F, -5.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).
						texOffs(52, 54).
						addBox(-4.0F, -8.0F, 4.0F, 8.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)).
						texOffs(0, 16).
						addBox(-5.0F, -9.0F, -4.0F, 10.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)).
						texOffs(0, 41).
						addBox(4.0F, -8.0F, -4.0F, 1.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)).
						texOffs(28, 38).
						addBox(-5.0F, -8.0F, -4.0F, 1.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().
				texOffs(36, 16).
				addBox(-1.5F, -6.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.5F, -1.5F, 0.5F, 0.0F, -1.5708F, 0.0F));
		PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().
				texOffs(36, 16).
				addBox(-1.5F, -6.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.5F, -1.5F, 0.5F, 0.0F, 1.5708F, 0.0F));
		PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().
				texOffs(18, 41).
				addBox(-2.0F, -1.5F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, -7.0F, 0.5F, 0.0F, -1.5708F, 0.0F));
		PartDefinition cube_r4 = head.addOrReplaceChild("cube_r4", CubeListBuilder.create().
				texOffs(18, 41).
				addBox(-2.0F, -1.5F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -7.0F, 0.5F, 0.0F, -1.5708F, 0.0F));
		
		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().
				texOffs(32, 0).
				addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE.extend(0.5F)), PartPose.ZERO);

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().
						texOffs(0, 25).
						addBox(-5.0F, 0.0F, -2.0F, 10.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).
						texOffs(62, 24).
						addBox(-4.5F, 2.0F, -4.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).
						texOffs(62, 30).
						addBox(0.5F, 2.0F, -4.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).
						texOffs(32, 54).
						addBox(-4.0F, 8.0F, -3.5F, 8.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).
						texOffs(62, 36).
						addBox(0.2F, 8.0F, 2.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).
						texOffs(62, 42).
						addBox(-4.2F, 8.0F, 2.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).
						texOffs(18, 45).
						addBox(0.5F, 6.0F, -3.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).
						texOffs(18, 47).
						addBox(-4.5F, 6.0F, -3.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).
						texOffs(28, 25).
						addBox(-4.5F, 3.0F, -5.0F, 9.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)).
						texOffs(0, 59).
						addBox(-5.5F, 3.0F, -5.0F, 1.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)).
						texOffs(0, 59).
						addBox(4.5F, 3.0F, -5.0F, 1.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)).
						texOffs(32, 60).
						addBox(-4.6F, 6.0F, 2.0F, 9.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).
						texOffs(62, 22).
						addBox(-5.0F, 3.0F, 2.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightArm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().
				texOffs(48, 22).
				addBox(-3.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition leftArm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().
				texOffs(18, 52).
				addBox(0.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition rightLeg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().
						texOffs(46, 38).
						addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).
						texOffs(48, 11).
						addBox(-2.6F, 0.0F, -3.0F, 4.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition leftLeg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().
						texOffs(32, 0).
						addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).
						texOffs(48, 0).
						addBox(-1.4F, 0.0F, -3.0F, 4.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
	
	@Override
	public void setupAnim(@NotNull BlacksmithRenderState state)
	{
		super.setupAnim(state);
	}
}