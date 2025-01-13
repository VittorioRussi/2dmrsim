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
package io.github.ericmedvet.mrsim2d.core.tasks.sumo;

import io.github.ericmedvet.mrsim2d.core.EmbodiedAgent;
import io.github.ericmedvet.mrsim2d.core.Snapshot;
import io.github.ericmedvet.mrsim2d.core.XMirrorer;
import io.github.ericmedvet.mrsim2d.core.actions.AddAgent;
import io.github.ericmedvet.mrsim2d.core.actions.CreateUnmovableBody;
import io.github.ericmedvet.mrsim2d.core.actions.TranslateAgent;
import io.github.ericmedvet.mrsim2d.core.bodies.Body;
import io.github.ericmedvet.mrsim2d.core.engine.Engine;
import io.github.ericmedvet.mrsim2d.core.geometry.BoundingBox;
import io.github.ericmedvet.mrsim2d.core.geometry.Point;
import io.github.ericmedvet.mrsim2d.core.geometry.Terrain;
import io.github.ericmedvet.mrsim2d.core.tasks.AgentsObservation;
import io.github.ericmedvet.mrsim2d.core.tasks.AgentsOutcome;
import io.github.ericmedvet.mrsim2d.core.tasks.BiTask;
import io.github.ericmedvet.mrsim2d.core.util.PolyUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Sumo
    implements BiTask<
        Supplier<EmbodiedAgent>, Supplier<EmbodiedAgent>, AgentsObservation, AgentsOutcome<AgentsObservation>> {

  // TODO parameterize AGENT1_INITIAL_X and AGENT1_INITIAL_X in function of sumoArena central platform
  private static final double AGENT1_INITIAL_X = 6;
  private static final double AGENT2_INITIAL_X = 14.5;
  private static final double INITIAL_Y_GAP = 0.25;
  private final double duration;
  private final Terrain terrain;
  private final double agent1InitialX;
  private final double agent2InitialX;
  private final double initialYGap;

  public Sumo(double duration, Terrain terrain, double agent1InitialX, double agent2InitialX, double initialYGap) {
    this.duration = duration;
    this.terrain = terrain;
    this.agent1InitialX = agent1InitialX;
    this.agent2InitialX = agent2InitialX;
    this.initialYGap = initialYGap;
  }

  public Sumo(double duration, Terrain terrain) {
    this(duration, terrain, AGENT1_INITIAL_X, AGENT2_INITIAL_X, INITIAL_Y_GAP);
  }

  @Override
  public SumoAgentsOutcome run(
      Supplier<EmbodiedAgent> embodiedAgentSupplier1,
      Supplier<EmbodiedAgent> embodiedAgentSupplier2,
      Engine engine,
      Consumer<Snapshot> snapshotConsumer) {
    // create agents
    EmbodiedAgent agent1 = embodiedAgentSupplier1.get();
    EmbodiedAgent agent2 = embodiedAgentSupplier2.get();
    engine.registerActionsFilter(agent2, new XMirrorer<>());

    // build world
    engine.perform(new CreateUnmovableBody(terrain.poly()));
    engine.perform(new AddAgent(agent1));
    engine.perform(new AddAgent(agent2));

    // place first agent
    BoundingBox agent1BB = agent1.boundingBox();
    engine.perform(new TranslateAgent(
        agent1,
        new Point(
            terrain.withinBordersXRange().min()
                + agent1InitialX
                - agent1BB.min().x(),
            0)));
    agent1BB = agent1.boundingBox();
    double maxY1 = terrain.maxHeightAt(agent1BB.xRange());
    double y1 = maxY1 + initialYGap - agent1BB.min().y();
    engine.perform(new TranslateAgent(agent1, new Point(0, y1)));

    // place second agent
    BoundingBox agent2BB = agent2.boundingBox();
    engine.perform(new TranslateAgent(
        agent2,
        new Point(
            terrain.withinBordersXRange().min()
                + agent2InitialX
                - agent2BB.min().x(),
            0)));
    agent2BB = agent2.boundingBox();
    double maxY3 = terrain.maxHeightAt(agent2BB.xRange());
    double y3 = maxY3 + initialYGap - agent2BB.min().y();
    engine.perform(new TranslateAgent(agent2, new Point(0, y3)));

    // run for defined time
    Map<Double, AgentsObservation> observations = new HashMap<>();
    while (engine.t() < duration) {
      Snapshot snapshot = engine.tick();
      snapshotConsumer.accept(snapshot);

      // TODO: add check if agent/agents fall outside the platform to stop simulation

      observations.put(
          engine.t(),
          new AgentsObservation(List.of(
              new AgentsObservation.Agent(
                  agent1.bodyParts().stream().map(Body::poly).toList(),
                  PolyUtils.maxYAtX(
                      terrain.poly(),
                      agent1.boundingBox().center().x())),
              new AgentsObservation.Agent(
                  agent2.bodyParts().stream().map(Body::poly).toList(),
                  PolyUtils.maxYAtX(
                      terrain.poly(),
                      agent2.boundingBox().center().x())))));
    }

    // return
    return new SumoAgentsOutcome(new TreeMap<>(observations));
  }
}
