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

package io.github.ericmedvet.mrsim2d.core.agents.legged;

import io.github.ericmedvet.jnb.datastructure.DoubleRange;
import io.github.ericmedvet.jsdynsym.core.numerical.NumericalDynamicalSystem;
import io.github.ericmedvet.mrsim2d.core.Action;
import io.github.ericmedvet.mrsim2d.core.ActionOutcome;
import io.github.ericmedvet.mrsim2d.core.NumBrained;
import io.github.ericmedvet.mrsim2d.core.Sensor;
import io.github.ericmedvet.mrsim2d.core.actions.ActuateRotationalJoint;
import io.github.ericmedvet.mrsim2d.core.actions.Sense;
import io.github.ericmedvet.mrsim2d.core.bodies.Body;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class NumLeggedHybridRobot extends AbstractLeggedHybridRobot implements NumBrained {

  private static final DoubleRange ANGLE_RANGE = new DoubleRange(Math.toRadians(-90), Math.toRadians(90));
  private static final DoubleRange INPUT_RANGE = DoubleRange.SYMMETRIC_UNIT;
  private static final DoubleRange OUTPUT_RANGE = DoubleRange.SYMMETRIC_UNIT;

  private final NumericalDynamicalSystem<?> numericalDynamicalSystem;
  private final List<Sensor<?>> headSensors;

  private double[] inputs;
  private double[] outputs;

  public NumLeggedHybridRobot(
      List<Leg> legs,
      double trunkLength,
      double trunkWidth,
      double trunkMass,
      double headMass,
      List<Sensor<?>> headSensors,
      NumericalDynamicalSystem<?> numericalDynamicalSystem
  ) {
    super(legs, trunkLength, trunkWidth, trunkMass, headMass);
    this.numericalDynamicalSystem = numericalDynamicalSystem;
    this.headSensors = headSensors;
  }

  public static int nOfInputs(List<Leg> legs, List<Sensor<?>> headSensors) {
    return headSensors.size() + legs.stream()
        .mapToInt(
            l -> l.legChunks()
                .stream()
                .mapToInt(lc -> lc.jointSensors().size())
                .sum() + l.downConnectorSensors().size()
        )
        .sum();
  }

  public static int nOfOutputs(List<Leg> legs) {
    return legs.stream().mapToInt(m -> m.legChunks().size()).sum();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<? extends Action<?>> act(double t, List<ActionOutcome<?, ?>> previousActionOutcomes) {
    // read inputs from last request
    inputs = previousActionOutcomes.stream()
        .filter(ao -> ao.action() instanceof Sense)
        .mapToDouble(ao -> {
          @SuppressWarnings("unchecked") ActionOutcome<Sense<?>, Double> so = (ActionOutcome<Sense<?>, Double>) ao;
          return INPUT_RANGE.denormalize(
              so.action().range().normalize(so.outcome().orElse(0d))
          );
        })
        .toArray();
    if (inputs.length == 0) {
      inputs = new double[numericalDynamicalSystem.nOfInputs()];
    }
    // compute actuation
    outputs = Arrays.stream(numericalDynamicalSystem.step(t, inputs))
        .map(OUTPUT_RANGE::clip)
        .toArray();
    // generate next sense actions
    List<Action<?>> actions = new ArrayList<>();
    for (int il = 0; il < legs.size(); il = il + 1) {
      Leg leg = legs.get(il);
      LegBody legBody = legBodies.get(il);
      leg.downConnectorSensors().forEach(s -> actions.add(((Sensor<Body>) s).apply(legBody.downConnector())));
      for (int ic = 0; ic < leg.legChunks().size(); ic = ic + 1) {
        LegChunk legChunk = leg.legChunks().get(ic);
        LegChunkBody legChunkBody = legBody.legChunks().get(ic);
        legChunk.jointSensors().forEach(s -> actions.add(((Sensor<Body>) s).apply(legChunkBody.joint())));
      }
    }
    headSensors.forEach(s -> actions.add(((Sensor<Body>) s).apply(head)));
    // generate actuation actions
    IntStream.range(0, outputs.length)
        .forEach(
            i -> actions.add(
                new ActuateRotationalJoint(
                    rotationalJoints.get(i),
                    ANGLE_RANGE.denormalize(OUTPUT_RANGE.normalize(outputs[i]))
                )
            )
        );
    return actions;
  }

  @Override
  public NumericalDynamicalSystem<?> brain() {
    return numericalDynamicalSystem;
  }

  @Override
  public BrainIO brainIO() {
    return new BrainIO(new RangedValues(inputs, INPUT_RANGE), new RangedValues(outputs, OUTPUT_RANGE));
  }
}
