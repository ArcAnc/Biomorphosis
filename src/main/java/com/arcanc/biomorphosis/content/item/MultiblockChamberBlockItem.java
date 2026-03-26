/**
 * @author ArcAnc
 * Created at: 04.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item;

import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.animatable.PItemAnimatable;
import com.arcanc.pulselib.content.animatable.instance.PAnimationController;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.content.renderer.PItemRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.content.renderer.modelData.PModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Function;

public class MultiblockChamberBlockItem extends BioBaseBlockItem implements PItemAnimatable<MultiblockChamberBlockItem>
{
    private final PAnimationManager<MultiblockChamberBlockItem> manager = PLibHelper.createManager(this);
    private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();
    
    public MultiblockChamberBlockItem(Block block, Properties properties, boolean addToCreative)
    {
        super(block, properties, addToCreative);
    }
    
    @Override
    public IClientItemExtensions registerClientExtension()
    {
        return new IClientItemExtensions()
        {
            private final Renderer renderer = new Renderer(
                    RenderHelper.mc().getBlockEntityRenderDispatcher(),
                    RenderHelper.mc().getEntityModels()
            );
            
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                return this.renderer;
            }
        };
    }
    
    @Override
    public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<MultiblockChamberBlockItem> registrar)
    {
        registrar.add(new PAnimationController<>(state ->
        {
            state.controller().play(IDLE);
            return state.controller().getState();
        }));
    }
    
    @Override
    public PAnimationManager<MultiblockChamberBlockItem> getAnimationManager()
    {
        return this.manager;
    }
    
    private static class Renderer extends PItemRenderer<MultiblockChamberBlockItem>
    {
        private Renderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher,
                        EntityModelSet entityModelSet)
        {
            super(new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("chamber")).
                    addTexture(Database.rl("0")).build(), PRenderTypes.RenderTypeProvider :: trianglesTranslucent, blockEntityRenderDispatcher, entityModelSet);
        }
    }
}
