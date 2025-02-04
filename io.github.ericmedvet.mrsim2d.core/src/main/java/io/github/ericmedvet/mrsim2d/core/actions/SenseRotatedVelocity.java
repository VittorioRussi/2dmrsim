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
import io.github.ericmedvet.mrsim2d.core.bodies.Body;
import io.github.ericmedvet.mrsim2d.core.geometry.Point;
import java.util.function.DoubleUnaryOperator;

public record SenseRotatedVelocity(
    double direction, Body body
) implements XMirrorableSense<Body>, SelfDescribedAction<Double> {

  private static final DoubleRange RANGE = new DoubleRange(-10, 10);

  @Override
  public Double perform(ActionPerformer performer, Agent agent) {
    Point v = body.centerLinearVelocity();
    double a = v.direction() - direction - body.angle();
    return v.magnitude() * Math.cos(a);
  }

  @Override
  public DoubleRange range() {
    return RANGE;
  }

  @Override
  public Sense<Body> mirrored() {
    return new SenseRotatedVelocity(SenseAngle.mirrorAngle(direction), body);
  }

  @Override
  public DoubleUnaryOperator outcomeMirrorer() {
    return DoubleUnaryOperator.identity();
  }
}
