/**
 * @author ArcAnc
 * Created at: 06.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.organic_armor;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.model.baked.PMeshRenderContext;
import com.arcanc.pulselib.content.renderer.modelData.PModelData;
import com.arcanc.pulselib.util.attachments.PAttachmentBinding;
import com.arcanc.pulselib.util.attachments.PLivingAttachmentDefinition;
import com.arcanc.pulselib.util.attachments.PLivingAttachmentSources;
import com.arcanc.pulselib.util.attachments.PLivingMeshRenderResolver;
import com.arcanc.pulselib.util.attachments.PLivingMeshRenderResolvers;
import com.arcanc.pulselib.util.attachments.humanoid.PHumanoidBindings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class OrganicArmorRenderHandler
{
	public static final ResourceLocation LIFELESS_TEXTURE = Database.rl("entity/armor/lifeless/0");
	public static final PModelData LIFELESS_MODEL = new PModelData.Builder(Database.rl("armor/lifeless"), "entity").
			addTexture(LIFELESS_TEXTURE).
			build();

	public static final ResourceLocation ORGANIC_TEXTURE = Database.rl("entity/armor/organic/0");
	private static final PModelData ORGANIC_MODEL = new PModelData.Builder(Database.rl("armor/organic"), "entity").
			addTexture(ORGANIC_TEXTURE).
			build();

	public static void registerOrganicArmor(final PulseLibEvents.AttachmentRegistrationEvent event)
	{
		event.registration().registerGlobalLiving(
				organicDefinition(EquipmentSlot.HEAD, List.of(
						PHumanoidBindings.head("head"))));
		event.registration().registerGlobalLiving(
				organicDefinition(EquipmentSlot.CHEST, List.of(
						PHumanoidBindings.body("body"),
						PHumanoidBindings.rightArm("right_arm"),
						PHumanoidBindings.leftArm("left_arm"))));
		event.registration().registerGlobalLiving(
				organicDefinition(EquipmentSlot.LEGS, List.of(
						PHumanoidBindings.rightLeg("right_pants"),
						PHumanoidBindings.leftLeg("left_pants"))));
		event.registration().registerGlobalLiving(
				organicDefinition(EquipmentSlot.FEET, List.of(
						PHumanoidBindings.rightLeg("right_boot"),
						PHumanoidBindings.leftLeg("left_boot"))));
	}

	public static void registerLifelessArmor(final PulseLibEvents.AttachmentRegistrationEvent event)
	{
		event.registration().registerLiving(Registration.ItemReg.LIFELESS_HELMET.get(),
				new PLivingAttachmentDefinition(
						LIFELESS_MODEL,
						PLivingAttachmentSources.equipmentSlot(EquipmentSlot.HEAD),
						List.of(PHumanoidBindings.head("head")),
						PLivingMeshRenderResolvers.inherited(),
						true));
		event.registration().registerLiving(Registration.ItemReg.LIFELESS_CHESTPLATE.get(),
				new PLivingAttachmentDefinition(
						LIFELESS_MODEL,
						PLivingAttachmentSources.equipmentSlot(EquipmentSlot.CHEST),
						List.of(
								PHumanoidBindings.body("body"),
								PHumanoidBindings.rightArm("right_arm"),
								PHumanoidBindings.leftArm("left_arm")),
						PLivingMeshRenderResolvers.inherited(),
						true));

		event.registration().registerLiving(Registration.ItemReg.LIFELESS_LEGGINGS.get(),
				new PLivingAttachmentDefinition(
						LIFELESS_MODEL,
						PLivingAttachmentSources.equipmentSlot(EquipmentSlot.LEGS),
						List.of(
								PHumanoidBindings.rightLeg("right_pants"),
								PHumanoidBindings.leftLeg("left_pants")),
						PLivingMeshRenderResolvers.inherited(),
						true));

		event.registration().registerLiving(Registration.ItemReg.LIFELESS_BOOTS.get(),
				new PLivingAttachmentDefinition(
						LIFELESS_MODEL,
						PLivingAttachmentSources.equipmentSlot(EquipmentSlot.FEET),
						List.of(
								PHumanoidBindings.rightLeg("right_boot"),
								PHumanoidBindings.leftLeg("left_boot")),
						PLivingMeshRenderResolvers.inherited(),
						true));
	}

	public static void registerTextures(final PulseLibEvents.RegisterTextureEvent event)
	{
		event.addTextureLocation(ORGANIC_TEXTURE);
		event.addTextureLocation(LIFELESS_TEXTURE);
	}

	private static PLivingAttachmentDefinition organicDefinition(EquipmentSlot slot, List<PAttachmentBinding> bindings)
	{
		return new PLivingAttachmentDefinition(
				ORGANIC_MODEL,
				PLivingAttachmentSources.entityPredicate(entity -> OrganicArmorHelper.hasArmor(entity, slot)),
				bindings,
				organicArmorResolver(slot),
				false);
	}

	private static PLivingMeshRenderResolver organicArmorResolver(EquipmentSlot slot)
	{
		return (entity, stack, bone, mesh, inherited, partialTick) ->
		{
			if (entity == null || !isColoredBone(bone))
				return inherited;

			OrganicArmorState.Piece piece = OrganicArmorHelper.getPiece(entity, slot).orElse(null);
			if (piece == null)
				return inherited;

			return withColor(inherited, getFluidColor(piece.fluid()));
		};
	}

	private static boolean isColoredBone(PBakedBone bone)
	{
		for (PBakedBone current = bone; current != null; current = current.parent())
			if (current.name().endsWith("_colored"))
				return true;
		return false;
	}

	private static PMeshRenderContext withColor(PMeshRenderContext context, int color)
	{
		return new PMeshRenderContext(context.renderType(), color, context.packedLight(), context.packedOverlay());
	}

	private static int getFluidColor(FluidStack fluid)
	{
		if (fluid.isEmpty())
			return -1;
		return MathHelper.ColorHelper.opaque(IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor());
	}
}
