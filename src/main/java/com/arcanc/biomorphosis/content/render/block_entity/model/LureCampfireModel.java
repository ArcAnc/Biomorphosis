package com.arcanc.biomorphosis.content.render.block_entity.model;// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.arcanc.biomorphosis.content.render.block_entity.model.render_state.LureCampfireRenderState;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.model.BlockEntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LureCampfireModel extends BlockEntityModel<LureCampfireRenderState>
{

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Database.MOD_ID, "campfire"), "main");
	private final ModelPart base;
	private final ModelPart shaft;
	private final ModelPart fire;

	public LureCampfireModel(@NotNull ModelPart root)
	{
		super(root);
		this.base = root.getChild("base");
		this.shaft = root.getChild("shaft");
		this.fire = root.getChild("fire");
	}

	public static LayerDefinition createBodyLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(41, 41).addBox(-13.0F, -4.0F, -6.0F, 16.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(41, 32).addBox(-14.0F, -4.0F, 2.0F, 18.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-10.0F, -7.0F, -8.0F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(0, 21).addBox(-4.0F, -7.0F, -8.0F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(33, 50).addBox(1.0F, -18.0F, -1.0F, 2.0F, 18.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(42, 50).addBox(-13.0F, -18.0F, -1.0F, 2.0F, 18.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 42).addBox(-11.0F, -1.0F, -2.0F, 12.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 24.0F, 0.0F));

		PartDefinition shaft = partdefinition.addOrReplaceChild("shaft", CubeListBuilder.create().texOffs(0, 48).addBox(-6.0F, -0.5F, -0.5F, 15.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 42).addBox(9.0F, -0.5F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 51).addBox(10.0F, 3.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.5F, 0.0F));

		PartDefinition fire = partdefinition.addOrReplaceChild("fire", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r1 = fire.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(41, 16).addBox(-10.0F, -15.0F, -1.0F, 20.0F, 15.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition cube_r2 = fire.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(41, 0).addBox(-10.0F, -15.0F, -1.0F, 20.0F, 15.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, 0.0F, -0.7854F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(LureCampfireRenderState state)
	{
		super.setupAnim(state);
		this.animate(state.rotateShaftAnimationState, LureCampfireRenderState.SHAFT_ROTATION, state.ageInTicks);
	}
}