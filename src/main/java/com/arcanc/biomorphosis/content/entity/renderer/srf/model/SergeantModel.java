/**
 * @author ArcAnc
 * Created at: 02.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer.srf.model;

import com.arcanc.biomorphosis.content.entity.srf.Sergeant;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

public class SergeantModel extends HumanoidModel<Sergeant>
{
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Database.rl("sergeant"), "main");
	
	public SergeantModel(ModelPart root)
	{
		super(root, RenderType :: entityCutout);
		this.hat.visible = false;
	}

	public static @NotNull LayerDefinition createMesh()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition rootBone = meshdefinition.getRoot();

		PartDefinition headBone = rootBone.addOrReplaceChild("head", CubeListBuilder.create().
						texOffs(28, 25).
						addBox(-5.0F, -8.0F, -4.0F, 1.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)).
						texOffs(32, 0).
						addBox(4.0F, -8.0F, -4.0F, 1.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)).
						texOffs(0, 16).
						addBox(-5.0F, -9.0F, -4.0F, 10.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)).
						texOffs(0, 41).
						addBox(-4.0F, -8.0F, 4.0F, 8.0F, 13.0F, 1.0F, new CubeDeformation(0.0F)).
						texOffs(46, 62).
						addBox(-4.0F, -9.0F, -5.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).
						texOffs(0, 0).
						addBox(-4.0F, -7.5F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));
		
		PartDefinition hatBone = headBone.addOrReplaceChild("hat", CubeListBuilder.create().
				texOffs(32, 0).
				addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE.extend(0.5F)), PartPose.ZERO);
		
		PartDefinition bodyBone = rootBone.addOrReplaceChild("body", CubeListBuilder.create().
						texOffs(28, 40).
						addBox(-2.0F, -2.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).
						texOffs(0, 25).
						addBox(-5.0F, 0.0F, -2.0F, 10.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).
						texOffs(50, 10).
						addBox(-4.6F, 6.0F, 1.25F, 9.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).
						texOffs(60, 37).
						addBox(-4.5F, 2.0F, -3.75F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).
						texOffs(60, 45).
						addBox(0.5F, 2.0F, -3.75F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).
						texOffs(46, 56).
						addBox(-4.0F, 8.0F, -2.25F, 8.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).
						texOffs(64, 62).
						addBox(0.2F, 8.0F, 0.75F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).
						texOffs(66, 13).
						addBox(-4.2F, 8.0F, 0.75F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).
						texOffs(66, 19).
						addBox(0.5F, 6.0F, -3.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).
						texOffs(66, 23).
						addBox(-4.5F, 6.0F, -3.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightArmBone = rootBone.addOrReplaceChild("right_arm", CubeListBuilder.create().
						texOffs(0, 55).
						addBox(-3.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).
						texOffs(50, 0).
						addBox(-3.6F, -3.0F, -3.0F, 3.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition leftArmBone = rootBone.addOrReplaceChild("left_arm", CubeListBuilder.create().
						texOffs(14, 55).
						addBox(0.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).
						texOffs(28, 56).
						addBox(0.4F, -3.0F, -3.0F, 3.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition rightLegBone = rootBone.addOrReplaceChild("right_leg", CubeListBuilder.create().
				texOffs(44, 40).
				addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition leftLegBone = rootBone.addOrReplaceChild("left_leg", CubeListBuilder.create().
				texOffs(28, 40).
				addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
	
	@Override
	public void setupAnim(@NotNull Sergeant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
	}
}