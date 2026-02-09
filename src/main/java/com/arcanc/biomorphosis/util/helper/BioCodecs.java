/**
 * @author ArcAnc
 * Created at: 07.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;

import com.mojang.datafixers.util.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BioCodecs
{
    public static final Codec<BlockPos> BLOCK_POS_JSON_CODEC = Codec.STRING.xmap(
            str ->
            {
                String[] parts = str.split(",");
                return new BlockPos(
                        Integer.parseInt(parts[0].trim()),
                        Integer.parseInt(parts[1].trim()),
                        Integer.parseInt(parts[2].trim()));
            },
            Vec3i::toShortString);
	
	public static final Codec<DoubleArrayList> DOUBLE_LIST_CODEC = Codec.DOUBLE.listOf().
			xmap(DoubleArrayList :: new, Function.identity());
	
	public static final Codec<VoxelShape> VOXEL_SHAPE_CODEC = DOUBLE_LIST_CODEC.flatXmap(doubles ->
			{
				if (doubles.size() % 6  != 0)
					return DataResult.error(() -> "Wrong data size for VoxelShape. VoxelShape data size must be divisible by 6, got " + doubles.size());
		
				if (doubles.isEmpty())
					return DataResult.success(Shapes.empty());
		
				VoxelShape shape = Shapes.empty();
		
				for (int q = 0; q < doubles.size(); q += 6)
				{
					double minX = doubles.getDouble(q);
					double minY = doubles.getDouble(q + 1);
					double minZ = doubles.getDouble(q + 2);
					double maxX = doubles.getDouble(q + 3);
					double maxY = doubles.getDouble(q + 4);
					double maxZ = doubles.getDouble(q + 5);
			
					shape = Shapes.joinUnoptimized(shape, Shapes.box(minX, minY, minZ, maxX, maxY, maxZ), BooleanOp.OR);
				}
		
				return DataResult.success(shape.optimize());
			},
			voxelShape ->
			{
				DoubleArrayList list = DoubleArrayList.of();
				
				voxelShape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
				{
					list.add(minX);
					list.add(minY);
					list.add(minZ);
					list.add(maxX);
					list.add(maxY);
					list.add(maxZ);
				});
				
				return DataResult.success(list);
			});
	
	//FIXME: replace with Vec3#STREAM_CODEC in new version
	public static final StreamCodec<ByteBuf, Vec3> VEC_3_STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.DOUBLE,
			Vec3 :: x,
			ByteBufCodecs.DOUBLE,
			Vec3 :: y,
			ByteBufCodecs.DOUBLE,
			Vec3 :: z,
			Vec3 :: new);
	
	public static class StreamCodecs
	{
		public static <B, C, T1, T2, T3, T4, T5, T6, T7> @NotNull StreamCodec<B, C> composite(final StreamCodec<? super B, T1> codec1, final Function<C, T1> getter1, final StreamCodec<? super B, T2> codec2, final Function<C, T2> getter2, final StreamCodec<? super B, T3> codec3, final Function<C, T3> getter3, final StreamCodec<? super B, T4> codec4, final Function<C, T4> getter4, final StreamCodec<? super B, T5> codec5, final Function<C, T5> getter5, final StreamCodec<? super B, T6> codec6, final Function<C, T6> getter6, final StreamCodec<? super B, T7> codec7, final Function<C, T7> getter7, final Function7<T1, T2, T3, T4, T5, T6, T7, C> factory)
		{
			return new StreamCodec<B, C>()
			{
				@Override
				public @NotNull C decode(@NotNull B buffer)
				{
					T1 t1 = codec1.decode(buffer);
					T2 t2 = codec2.decode(buffer);
					T3 t3 = codec3.decode(buffer);
					T4 t4 = codec4.decode(buffer);
					T5 t5 = codec5.decode(buffer);
					T6 t6 = codec6.decode(buffer);
					T7 t7 = codec7.decode(buffer);
					return factory.apply(t1, t2, t3, t4, t5, t6, t7);
				}
				
				@Override
				public void encode(@NotNull B buffer, @NotNull C data)
				{
					codec1.encode(buffer, getter1.apply(data));
					codec2.encode(buffer, getter2.apply(data));
					codec3.encode(buffer, getter3.apply(data));
					codec4.encode(buffer, getter4.apply(data));
					codec5.encode(buffer, getter5.apply(data));
					codec6.encode(buffer, getter6.apply(data));
					codec7.encode(buffer, getter7.apply(data));
				}
			};
		}
		
		public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> @NotNull StreamCodec<B, C> composite(
				final StreamCodec<? super B, T1> codec1,
				final Function<C, T1> getter1,
				final StreamCodec<? super B, T2> codec2,
				final Function<C, T2> getter2,
				final StreamCodec<? super B, T3> codec3,
				final Function<C, T3> getter3,
				final StreamCodec<? super B, T4> codec4,
				final Function<C, T4> getter4,
				final StreamCodec<? super B, T5> codec5,
				final Function<C, T5> getter5,
				final StreamCodec<? super B, T6> codec6,
				final Function<C, T6> getter6,
				final StreamCodec<? super B, T7> codec7,
				final Function<C, T7> getter7,
				final StreamCodec<? super B, T8> codec8,
				final Function<C, T8> getter8,
				final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> factory
		)
		{
			return new StreamCodec<B, C>()
			{
				@Override
				public @NotNull C decode(@NotNull B buffer)
				{
					T1 t1 = codec1.decode(buffer);
					T2 t2 = codec2.decode(buffer);
					T3 t3 = codec3.decode(buffer);
					T4 t4 = codec4.decode(buffer);
					T5 t5 = codec5.decode(buffer);
					T6 t6 = codec6.decode(buffer);
					T7 t7 = codec7.decode(buffer);
					T8 t8 = codec8.decode(buffer);
					return factory.apply(t1, t2, t3, t4, t5, t6, t7, t8);
				}
				
				@Override
				public void encode(@NotNull B buffer, @NotNull C data)
				{
					codec1.encode(buffer, getter1.apply(data));
					codec2.encode(buffer, getter2.apply(data));
					codec3.encode(buffer, getter3.apply(data));
					codec4.encode(buffer, getter4.apply(data));
					codec5.encode(buffer, getter5.apply(data));
					codec6.encode(buffer, getter6.apply(data));
					codec7.encode(buffer, getter7.apply(data));
					codec8.encode(buffer, getter8.apply(data));
				}
			};
		}
	}
}
