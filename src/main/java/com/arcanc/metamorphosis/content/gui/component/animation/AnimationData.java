/**
 * @author ArcAnc
 * Created at: 23.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.gui.component.animation;

import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record AnimationData(List<Pair<Integer, Integer>> frames, int totalLength)
{
    public AnimationData
    {
        if (frames == null || frames.isEmpty())
            throw new IllegalArgumentException("Frames cannot be null or empty");
        if (totalLength <= 0)
            throw new IllegalArgumentException("Total length must be positive");
    }

    public Pair<Integer, Integer> getFrameByTime(long time)
    {
        long remainder = time % totalLength;

        for (Pair<Integer, Integer> pair : frames)
        {
            if (remainder < pair.getSecond())
                return pair;
            remainder -= pair.getSecond();
        }
        return frames.getFirst();
    }

    public static @NotNull AnimationData construct(int texHeight, int patternHeight, int frameTime)
    {
        if (texHeight <= 0 || patternHeight <= 0 || frameTime <= 0)
            throw new IllegalArgumentException("Input values must be positive");

        int frameCount = texHeight / patternHeight;
        List<Pair<Integer, Integer>> frames = new ArrayList<>(frameCount);
        int totalLength = 0;

        for (int i = 0; i < frameCount; i++)
        {
            frames.add(Pair.of(i, frameTime));
            totalLength += frameTime;
        }

        return new AnimationData(List.copyOf(frames), totalLength);
    }
}

