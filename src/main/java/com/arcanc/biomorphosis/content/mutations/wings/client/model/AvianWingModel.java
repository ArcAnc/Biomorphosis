/**
 * @author ArcAnc
 * Created at: 23.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations.wings.client.model;

import com.arcanc.biomorphosis.content.mutations.wings.client.animation.WingAnimator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.List;

/**
 * Wings Model was taken from {@link <a href="https://github.com/Fuzss/fantastic-wings">Fantastic Wings</a>}
 * <p>
 * Permission was obtained directly from @Fuzs
 */

public final class AvianWingModel extends WingModel
{
	private static final float ROOT_Y_OFFSET = 3.5F;
	private final ModelPart root;
	private final List<ModelPart> leftBones;
	private final List<ModelPart> rightBones;
	private final List<ModelPart> leftFeathers;
	private final List<ModelPart> rightFeathers;

	public AvianWingModel(ModelPart root)
	{
		this.root = root;
		ModelPart leftCoracoid = root.getChild("left_coracoid");
		ModelPart rightCoracoid = root.getChild("right_coracoid");
		ModelPart leftHumerus = leftCoracoid.getChild("left_humerus");
		ModelPart rightHumerus = rightCoracoid.getChild("right_humerus");
		ModelPart leftUlna = leftHumerus.getChild("left_ulna");
		ModelPart rightUlna = rightHumerus.getChild("right_ulna");
		ModelPart leftCarpals = leftUlna.getChild("left_carpals");
		ModelPart rightCarpals = rightUlna.getChild("right_carpals");
		ModelPart leftCoracoidFeathers = leftCoracoid.getChild("left_coracoid_feathers");
		ModelPart rightCoracoidFeathers = rightCoracoid.getChild("right_coracoid_feathers");
		ModelPart leftTertiaryFeathers = leftHumerus.getChild("left_tertiary_feathers");
		ModelPart rightTertiaryFeathers = rightHumerus.getChild("right_tertiary_feathers");
		ModelPart leftSecondaryFeathers = leftUlna.getChild("left_secondary_feathers");
		ModelPart rightSecondaryFeathers = rightUlna.getChild("right_secondary_feathers");
		ModelPart leftPrimaryFeathers = leftCarpals.getChild("left_primary_feathers");
		ModelPart rightPrimaryFeathers = rightCarpals.getChild("right_primary_feathers");
		addTexture(leftCoracoidFeathers, 6, 40, 0, 0, -1, 6, 8);
		addTexture(rightCoracoidFeathers, 0, 40, -6, 0, -1, 6, 8);
		addTexture(leftTertiaryFeathers, 10, 14, 0, 0, -0.5F, 10, 14);
		addTexture(rightTertiaryFeathers, 0, 14, -10, 0, -0.5F, 10, 14);
		addTexture(leftSecondaryFeathers, 31, 14, -2, 0, -0.5F, 11, 12);
		addTexture(rightSecondaryFeathers, 20, 14, -9, 0, -0.5F, 11, 12);
		addTexture(leftPrimaryFeathers, 53, 14, 0, -2.1F, -0.5F, 11, 11);
		addTexture(rightPrimaryFeathers, 42, 14, -11, -2.1F, -0.5F, 11, 11);
		this.leftBones = List.of(leftCoracoid, leftHumerus, leftUlna, leftCarpals);
		this.rightBones = List.of(rightCoracoid, rightHumerus, rightUlna, rightCarpals);
		this.leftFeathers = List.of(leftCoracoidFeathers, leftTertiaryFeathers, leftSecondaryFeathers, leftPrimaryFeathers);
		this.rightFeathers = List.of(rightCoracoidFeathers, rightTertiaryFeathers, rightSecondaryFeathers, rightPrimaryFeathers);
	}

	public static LayerDefinition createLayer()
	{
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition leftCoracoid = root.addOrReplaceChild("left_coracoid", CubeListBuilder.create().texOffs(0, 28).addBox(0, -1.5F, -1.5F, 5, 3, 3), PartPose.offset(1.5F, ROOT_Y_OFFSET, 2.5F));
		PartDefinition rightCoracoid = root.addOrReplaceChild("right_coracoid", CubeListBuilder.create().texOffs(0, 34).addBox(-5, -1.5F, -1.5F, 5, 3, 3), PartPose.offset(-1.5F, ROOT_Y_OFFSET, 2.5F));
		PartDefinition leftHumerus = leftCoracoid.addOrReplaceChild("left_humerus", CubeListBuilder.create().texOffs(0, 0).addBox(-0.1F, -1.1F, -2, 7, 3, 4), PartPose.offset(4.7F, -0.6F, 0.1F));
		PartDefinition rightHumerus = rightCoracoid.addOrReplaceChild("right_humerus", CubeListBuilder.create().texOffs(0, 7).addBox(-6.9F, -1.1F, -2, 7, 3, 4), PartPose.offset(-4.7F, -0.6F, 0.1F));
		PartDefinition leftUlna = leftHumerus.addOrReplaceChild("left_ulna", CubeListBuilder.create().texOffs(22, 0).addBox(0, -1.5F, -1.5F, 9, 3, 3), PartPose.offset(6.5F, 0.2F, 0.1F));
		PartDefinition rightUlna = rightHumerus.addOrReplaceChild("right_ulna", CubeListBuilder.create().texOffs(22, 6).addBox(-9, -1.5F, -1.5F, 9, 3, 3), PartPose.offset(-6.5F, 0.2F, 0.1F));
		PartDefinition leftCarpals = leftUlna.addOrReplaceChild("left_carpals", CubeListBuilder.create().texOffs(46, 0).addBox(0, -1, -1, 5, 2, 2), PartPose.offset(8.5F, 0, 0));
		PartDefinition rightCarpals = rightUlna.addOrReplaceChild("right_carpals", CubeListBuilder.create().texOffs(46, 4).addBox(-5, -1, -1, 5, 2, 2), PartPose.offset(-8.5F, 0, 0));
		leftCoracoid.addOrReplaceChild("left_coracoid_feathers", CubeListBuilder.create(), PartPose.offset(0.4F, 0, 1));
		rightCoracoid.addOrReplaceChild("right_coracoid_feathers", CubeListBuilder.create(), PartPose.offset(-0.4F, 0, 1));
		leftHumerus.addOrReplaceChild("left_tertiary_feathers", CubeListBuilder.create(), PartPose.offset(0, 1.5F, 1));
		rightHumerus.addOrReplaceChild("right_tertiary_feathers", CubeListBuilder.create(), PartPose.offset(0, 1.5F, 1));
		leftUlna.addOrReplaceChild("left_secondary_feathers", CubeListBuilder.create(), PartPose.offset(0, 1, 0));
		rightUlna.addOrReplaceChild("right_secondary_feathers", CubeListBuilder.create(), PartPose.offset(0, 1, 0));
		leftCarpals.addOrReplaceChild("left_primary_feathers", CubeListBuilder.create(), PartPose.ZERO);
		rightCarpals.addOrReplaceChild("right_primary_feathers", CubeListBuilder.create(), PartPose.ZERO);
		return LayerDefinition.create(mesh, 64, 64);
	}

	@Override
	public void render(WingAnimator animator, float partialTick, PoseStack poseStack, VertexConsumer consumer, int light, int overlay, int color)
	{
		for (int index = 0; index < this.leftBones.size(); index++)
			setAngles(this.leftBones.get(index), this.rightBones.get(index), animator.getWingRotation(index, partialTick));
		for (int index = 0; index < this.leftFeathers.size(); index++)
			setAngles(this.leftFeathers.get(index), this.rightFeathers.get(index), animator.getFeatherRotation(index, partialTick));
		this.root.render(poseStack, consumer, light, overlay, color);
	}

	private static void addTexture(ModelPart part, int u, int v, float x, float y, float z, int width, int height)
	{
		part.cubes = List.of(Model3DTexture.create(x, y, z, width, height, u, v, 64, 64));
	}
}
