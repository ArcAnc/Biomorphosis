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
import com.arcanc.pulselib.content.animatable.AnimManagerKey;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.animatable.PItemAnimatable;
import com.arcanc.pulselib.content.animatable.singleton.SingletonAnimationManager;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.content.renderer.PItemRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class MultiblockChamberBlockItem extends BioBaseBlockItem implements PItemAnimatable<MultiblockChamberBlockItem>
{
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
        registrar.add(() -> state ->
        {
            state.controller().play(IDLE);
            return state.controller().getState();
        });
    }
    
    @Override
    public PAnimationManager<MultiblockChamberBlockItem> getAnimationManager(AnimManagerKey key)
    {
        return SingletonAnimationManager.getManager(key, this);
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
