/*-
 * ========================LICENSE_START=================================
 * mrsim2d-core
 * %%
 * Copyright (C) 2020 - 2024 Eric Medvet
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package io.github.ericmedvet.mrsim2d.core.actions;

import io.github.ericmedvet.jnb.datastructure.DoubleRange;
import io.github.ericmedvet.mrsim2d.core.ActionPerformer;
import io.github.ericmedvet.mrsim2d.core.Agent;
import io.github.ericmedvet.mrsim2d.core.SelfDescribedAction;
import io.github.ericmedvet.mrsim2d.core.bodies.Voxel;
import java.util.function.DoubleUnaryOperator;

public record SenseSideCompression(
    Voxel.Side side, Voxel body
) implements XMirrorableSense<Voxel>, SelfDescribedAction<Double> {
  private static final DoubleRange RANGE = new DoubleRange(0.5, 1.5);

  @Override
  public Double perform(ActionPerformer performer, Agent agent) {
    double avgL = Math.sqrt(body.areaRatio() * body.restArea());
    return RANGE.clip(body.vertex(side.getVertex1()).distance(body.vertex(side.getVertex2())) / avgL);
  }

  @Override
  public DoubleRange range() {
    return RANGE;
  }

  @Override
  public Sense<Voxel> mirrored() {
    return new SenseSideCompression(
        switch (side) {
          case E -> Voxel.Side.W;
          case W -> Voxel.Side.E;
          default -> side;
        },
        body
    );
  }

  @Override
  public DoubleUnaryOperator outcomeMirrorer() {
    return DoubleUnaryOperator.identity();
  }
}
