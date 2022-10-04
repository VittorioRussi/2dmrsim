/*
 * Copyright 2022 eric
 *
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

package it.units.erallab.mrsim2d.core;

import it.units.erallab.mrsim2d.builder.NamedBuilder;
import it.units.erallab.mrsim2d.core.agents.gridvsr.CentralizedNumGridVSR;
import it.units.erallab.mrsim2d.core.agents.gridvsr.NumGridVSR;
import it.units.erallab.mrsim2d.core.agents.independentvoxel.NumIndependentVoxel;
import it.units.erallab.mrsim2d.core.agents.legged.AbstractLeggedHybridModularRobot;
import it.units.erallab.mrsim2d.core.agents.legged.NumLeggedHybridModularRobot;
import it.units.erallab.mrsim2d.core.builders.*;
import it.units.erallab.mrsim2d.core.tasks.locomotion.Locomotion;
import it.units.erallab.mrsim2d.core.tasks.piling.FallPiling;
import it.units.erallab.mrsim2d.core.tasks.piling.StandPiling;
import it.units.erallab.mrsim2d.core.util.DoubleRange;

import java.util.List;

public class PreparedNamedBuilder {

  private final static NamedBuilder<Object> NB = NamedBuilder.empty()
      .and(List.of("sim", "s"), NamedBuilder.empty()
          .and(NamedBuilder.fromUtilityClass(DoubleRange.class))
          .and(List.of("terrain", "t"), NamedBuilder.fromUtilityClass(TerrainBuilder.class))
          .and(List.of("task"), NamedBuilder.empty()
              .and(NamedBuilder.fromClass(Locomotion.class))
              .and(NamedBuilder.fromClass(FallPiling.class))
              .and(NamedBuilder.fromClass(StandPiling.class))
          )
          .and(List.of("agent", "a"), NamedBuilder.empty()
              .and(NamedBuilder.fromClass(NumGridVSR.class))
              .and(NamedBuilder.fromClass(CentralizedNumGridVSR.class))
              .and(NamedBuilder.fromClass(NumIndependentVoxel.class))
              .and(NamedBuilder.fromClass(NumLeggedHybridModularRobot.class))
              .and(List.of("legged", "l"), NamedBuilder.empty()
                  .and(NamedBuilder.fromClass(AbstractLeggedHybridModularRobot.Module.class))
                  .and(NamedBuilder.fromClass(AbstractLeggedHybridModularRobot.LegChunk.class))
              )
          )
          .and(List.of("sensor", "s"), NamedBuilder.fromUtilityClass(SensorBuilder.class))
          .and(List.of("function", "f"), NamedBuilder.fromUtilityClass(TimedRealFunctionBuilder.class))
          .and(List.of("vsr"), NamedBuilder.empty()
              .and(NamedBuilder.fromClass(NumGridVSR.Body.class))
              .and(List.of("shape", "s"), NamedBuilder.fromUtilityClass(GridShapeBuilder.class))
              .and(
                  List.of("sensorizingFunction", "sf"),
                  NamedBuilder.fromUtilityClass(VSRSensorizingFunctionBuilder.class)
              )
          ));

  public static NamedBuilder<Object> get() {
    return NB;
  }
}
