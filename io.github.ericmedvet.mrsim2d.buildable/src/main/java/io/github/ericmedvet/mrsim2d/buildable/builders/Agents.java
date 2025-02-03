/*-
 * ========================LICENSE_START=================================
 * mrsim2d-buildable
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

package io.github.ericmedvet.mrsim2d.buildable.builders;

import io.github.ericmedvet.jnb.core.Discoverable;
import io.github.ericmedvet.jnb.core.Param;
import io.github.ericmedvet.jnb.datastructure.Grid;
import io.github.ericmedvet.jsdynsym.buildable.builders.NumericalDynamicalSystems;
import io.github.ericmedvet.jsdynsym.core.numerical.MultivariateRealFunction;
import io.github.ericmedvet.mrsim2d.core.Sensor;
import io.github.ericmedvet.mrsim2d.core.agents.gridvsr.CentralizedNumGridVSR;
import io.github.ericmedvet.mrsim2d.core.agents.gridvsr.DistributedNumGridVSR;
import io.github.ericmedvet.mrsim2d.core.agents.gridvsr.GridBody;
import io.github.ericmedvet.mrsim2d.core.agents.gridvsr.ReactiveGridVSR;
import io.github.ericmedvet.mrsim2d.core.agents.gridvsr.ReactiveGridVSR.ReactiveVoxel;
import io.github.ericmedvet.mrsim2d.core.agents.independentvoxel.NumIndependentVoxel;
import io.github.ericmedvet.mrsim2d.core.agents.legged.AbstractLeggedHybridModularRobot;
import io.github.ericmedvet.mrsim2d.core.agents.legged.AbstractLeggedHybridRobot;
import io.github.ericmedvet.mrsim2d.core.agents.legged.NumLeggedHybridModularRobot;
import io.github.ericmedvet.mrsim2d.core.agents.legged.NumLeggedHybridRobot;
import io.github.ericmedvet.mrsim2d.core.bodies.Voxel;
import java.util.List;

@Discoverable(prefixTemplate = "sim|s.agent|a")
public class Agents {

  private Agents() {
  }

  @SuppressWarnings("unused")
  public static CentralizedNumGridVSR centralizedNumGridVSR(
      @Param("body") GridBody body,
      @Param("function") NumericalDynamicalSystems.Builder<?, ?> numericalDynamicalSystemBuilder
  ) {
    return new CentralizedNumGridVSR(
        body,
        numericalDynamicalSystemBuilder.apply(
            MultivariateRealFunction.varNames("x", CentralizedNumGridVSR.nOfInputs(body)),
            MultivariateRealFunction.varNames("y", CentralizedNumGridVSR.nOfOutputs(body))
        )
    );
  }

  @SuppressWarnings("unused")
  public static DistributedNumGridVSR distributedNumGridVSR(
      @Param("body") GridBody body,
      @Param("function") NumericalDynamicalSystems.Builder<?, ?> numericalDynamicalSystemBuilder,
      @Param("nOfSignals") int nOfSignals,
      @Param("directional") boolean directional
  ) {
    return new DistributedNumGridVSR(
        body,
        Grid.create(
            body.grid().w(),
            body.grid().h(),
            k -> body.grid()
                .get(k)
                .element()
                .type()
                .equals(GridBody.VoxelType.NONE) ? null : numericalDynamicalSystemBuilder.apply(
                    MultivariateRealFunction.varNames(
                        "x",
                        DistributedNumGridVSR.nOfInputs(body, k, nOfSignals, directional)
                    ),
                    MultivariateRealFunction.varNames(
                        "y",
                        DistributedNumGridVSR.nOfOutputs(body, k, nOfSignals, directional)
                    )
                )
        ),
        nOfSignals,
        directional
    );
  }

  @SuppressWarnings("unused")
  public static NumIndependentVoxel numIndependentVoxel(
      @Param("sensors") List<Sensor<? super Voxel>> sensors,
      @Param(value = "areaActuation", dS = "sides") NumIndependentVoxel.AreaActuation areaActuation,
      @Param(value = "attachActuation", dB = true) boolean attachActuation,
      @Param(value = "nOfNFCChannels", dI = 1) int nOfNFCChannels,
      @Param("function") NumericalDynamicalSystems.Builder<?, ?> numericalDynamicalSystemBuilder
  ) {
    return new NumIndependentVoxel(
        sensors,
        areaActuation,
        attachActuation,
        nOfNFCChannels,
        numericalDynamicalSystemBuilder.apply(
            MultivariateRealFunction.varNames("x", NumIndependentVoxel.nOfInputs(sensors, nOfNFCChannels)),
            MultivariateRealFunction.varNames(
                "y",
                NumIndependentVoxel.nOfOutputs(areaActuation, attachActuation, nOfNFCChannels)
            )
        )
    );
  }

  @SuppressWarnings("unused")
  public static NumLeggedHybridModularRobot numLeggedHybridModularRobot(
      @Param("modules") List<AbstractLeggedHybridModularRobot.Module> modules,
      @Param("function") NumericalDynamicalSystems.Builder<?, ?> numericalDynamicalSystemBuilder
  ) {
    return new NumLeggedHybridModularRobot(
        modules,
        numericalDynamicalSystemBuilder.apply(
            MultivariateRealFunction.varNames("x", NumLeggedHybridModularRobot.nOfInputs(modules)),
            MultivariateRealFunction.varNames("y", NumLeggedHybridModularRobot.nOfOutputs(modules))
        )
    );
  }

  @SuppressWarnings("unused")
  public static NumLeggedHybridRobot numLeggedHybridRobot(
      @Param("legs") List<AbstractLeggedHybridRobot.Leg> legs,
      @Param(value = "trunkLength", dD = 4 * LeggedMisc.TRUNK_LENGTH) double trunkLength,
      @Param(value = "trunkWidth", dD = LeggedMisc.TRUNK_WIDTH) double trunkWidth,
      @Param(value = "trunkMass", dD = 4 * LeggedMisc.TRUNK_MASS) double trunkMass,
      @Param(value = "headMass", dD = LeggedMisc.TRUNK_WIDTH * LeggedMisc.TRUNK_WIDTH * LeggedMisc.RIGID_DENSITY) double headMass,
      @Param("headSensors") List<Sensor<?>> headSensors,
      @Param("function") NumericalDynamicalSystems.Builder<?, ?> numericalDynamicalSystemBuilder
  ) {
    return new NumLeggedHybridRobot(
        legs,
        trunkLength,
        trunkWidth,
        trunkMass,
        headMass,
        headSensors,
        numericalDynamicalSystemBuilder.apply(
            MultivariateRealFunction.varNames("x", NumLeggedHybridRobot.nOfInputs(legs, headSensors)),
            MultivariateRealFunction.varNames("y", NumLeggedHybridRobot.nOfOutputs(legs))
        )
    );
  }

  @SuppressWarnings("unused")
  public static ReactiveGridVSR reactiveGridVSR(@Param("body") Grid<ReactiveVoxel> body) {
    return new ReactiveGridVSR(body);
  }
}
