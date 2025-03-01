/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.operator.aggregation;

import io.trino.spi.block.Block;
import io.trino.spi.block.MapBlockBuilder;
import io.trino.spi.block.SingleMapBlock;
import io.trino.spi.function.AccumulatorState;
import io.trino.spi.function.AccumulatorStateMetadata;

@AccumulatorStateMetadata(
        stateFactoryClass = MapAggregationStateFactory.class,
        stateSerializerClass = MapAggregationStateSerializer.class,
        typeParameters = {"K", "V"},
        serializedType = "map(K, V)")
public interface MapAggregationState
        extends AccumulatorState
{
    void add(Block keyBlock, int keyPosition, Block valueBlock, int valuePosition);

    default void merge(MapAggregationState other)
    {
        SingleMapBlock serializedState = ((SingleMapAggregationState) other).removeTempSerializedState();
        for (int i = 0; i < serializedState.getPositionCount(); i += 2) {
            add(serializedState, i, serializedState, i + 1);
        }
    }

    void writeAll(MapBlockBuilder out);
}
