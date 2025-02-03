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

package io.github.ericmedvet.mrsim2d.core.tasks.jumping;

import io.github.ericmedvet.mrsim2d.core.EmbodiedAgent;
import io.github.ericmedvet.mrsim2d.core.Snapshot;
import io.github.ericmedvet.mrsim2d.core.actions.AddAgent;
import io.github.ericmedvet.mrsim2d.core.actions.CreateUnmovableBody;
import io.github.ericmedvet.mrsim2d.core.actions.TranslateAgent;
import io.github.ericmedvet.mrsim2d.core.bodies.Body;
import io.github.ericmedvet.mrsim2d.core.engine.Engine;
import io.github.ericmedvet.mrsim2d.core.geometry.BoundingBox;
import io.github.ericmedvet.mrsim2d.core.geometry.Path;
import io.github.ericmedvet.mrsim2d.core.geometry.Point;
import io.github.ericmedvet.mrsim2d.core.geometry.Terrain;
import io.github.ericmedvet.mrsim2d.core.tasks.AgentsObservation;
import io.github.ericmedvet.mrsim2d.core.tasks.AgentsOutcome;
import io.github.ericmedvet.mrsim2d.core.tasks.Task;
import io.github.ericmedvet.mrsim2d.core.util.PolyUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Jumping implements Task<Supplier<EmbodiedAgent>, AgentsObservation, AgentsOutcome<AgentsObservation>> {

  public static final double TERRAIN_BORDER_W = 10d;
  public static final double TERRAIN_BORDER_H = 100d;
  public static final double TERRAIN_W = 100d;
  public static final double TERRAIN_H = 25d;
  private static final double INITIAL_Y_GAP = 0.25;
  private final double duration;
  private final double initialYGap;

  public Jumping(double duration, double initialYGap) {
    this.duration = duration;
    this.initialYGap = initialYGap;
  }

  public Jumping(double duration) {
    this(duration, INITIAL_Y_GAP);
  }

  @Override
  public AgentsOutcome<AgentsObservation> run(
      Supplier<EmbodiedAgent> embodiedAgentSupplier,
      Engine engine,
      Consumer<Snapshot> snapshotConsumer
  ) {
    // create agent
    EmbodiedAgent embodiedAgent = embodiedAgentSupplier.get();
    // build world
    Terrain terrain = Terrain.fromPath(
        new Path(new Point(TERRAIN_W, 0)),
        TERRAIN_H,
        TERRAIN_BORDER_W,
        TERRAIN_BORDER_H
    );
    engine.perform(new CreateUnmovableBody(terrain.poly()));
    // place agent
    engine.perform(new AddAgent(embodiedAgent));
    BoundingBox agentBB = embodiedAgent.boundingBox();
    engine.perform(
        new TranslateAgent(
            embodiedAgent,
            new Point(
                terrain.withinBordersXRange().min() + terrain.withinBordersXRange().extent() / 2d - agentBB.min()
                    .x() + agentBB.xRange().extent() / 2d,
                0
            )
        )
    );
    agentBB = embodiedAgent.boundingBox();
    double maxY = terrain.maxHeightAt(agentBB.xRange());
    engine.perform(
        new TranslateAgent(
            embodiedAgent,
            new Point(0, maxY + initialYGap - agentBB.min().y())
        )
    );
    // run for defined time
    Map<Double, AgentsObservation> observations = new HashMap<>();
    while (engine.t() < duration) {
      Snapshot snapshot = engine.tick();
      snapshotConsumer.accept(snapshot);
      observations.put(
          engine.t(),
          new AgentsObservation(
              List.of(
                  new AgentsObservation.Agent(
                      embodiedAgent.bodyParts().stream().map(Body::poly).toList(),
                      PolyUtils.maxYAtX(
                          terrain.poly(),
                          embodiedAgent.boundingBox().center().x()
                      )
                  )
              )
          )
      );
    }
    // return
    return new AgentsOutcome<>(new TreeMap<>(observations));
  }
}
