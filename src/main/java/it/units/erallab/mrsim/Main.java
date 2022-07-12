/*
 * Copyright 2022 Eric Medvet <eric.medvet@gmail.com> (as eric)
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

package it.units.erallab.mrsim;

import it.units.erallab.mrsim.agents.gridvsr.AbstractGridVSR;
import it.units.erallab.mrsim.agents.gridvsr.NumGridVSR;
import it.units.erallab.mrsim.agents.gridvsr.ShapeUtils;
import it.units.erallab.mrsim.core.Snapshot;
import it.units.erallab.mrsim.core.actions.*;
import it.units.erallab.mrsim.core.bodies.*;
import it.units.erallab.mrsim.core.geometry.Point;
import it.units.erallab.mrsim.core.geometry.Poly;
import it.units.erallab.mrsim.engine.Engine;
import it.units.erallab.mrsim.engine.dyn4j.Dyn4JEngine;
import it.units.erallab.mrsim.util.Grid;
import it.units.erallab.mrsim.util.PolyUtils;
import it.units.erallab.mrsim.util.Profiled;
import it.units.erallab.mrsim.viewer.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;

/**
 * @author "Eric Medvet" on 2022/07/06 for 2dmrsim
 */
public class Main {
  public static void main(String[] args) throws IOException {
    Poly ball = Poly.regular(1, 20);
    double ballInterval = 5d;
    double lastBallT = 0d;
    Engine engine = new Dyn4JEngine();
    Grid<Boolean> shape = ShapeUtils.buildShape("biped-4x4");
    AbstractGridVSR vsr = new NumGridVSR(
        shape.map(b -> b ? new Voxel.Material() : null),
        shape.map(b -> List.of()),
        (t, iG) -> Grid.create(shape.w(), shape.h(), (x, y) -> Math.sin(-2d * Math.PI * t + Math.PI * x / shape.w()))
    );
    Voxel v1 = engine.perform(new CreateAndTranslateVoxel(1, 1, new Point(8, 14))).outcome().orElseThrow();
    Voxel v2 = engine.perform(new CreateAndTranslateVoxel(1, 1, new Point(8, 15))).outcome().orElseThrow();
    Voxel v3 = engine.perform(new CreateAndTranslateVoxel(1, 1, new Point(9, 15))).outcome().orElseThrow();
    engine.perform(new AddAndTranslateAgent(vsr, new Point(3d, 4d)));
    engine.perform(new AttachClosestAnchors(2, v1, v2, Anchor.Link.Type.RIGID)).outcome().orElseThrow();
    engine.perform(new AttachClosestAnchors(2, v2, v3, Anchor.Link.Type.SOFT)).outcome().orElseThrow();
    Poly terrain = PolyUtils.createTerrain(
        "hilly-0.25-2-0",
        //"downhill-10",
        //"uphill-30",
        //"steppy-0.25-2-0",
        //"flat",
        100, 1, 1, 3
    );
    engine.perform(new CreateUnmovableBody(terrain));
    Drawer drawer = Drawers.basic().profiled();
    FramesImageBuilder imageBuilder = new FramesImageBuilder(
        400,
        200,
        20,
        0.25,
        FramesImageBuilder.Direction.VERTICAL,
        Drawers.basic()
    );
    VideoBuilder videoBuilder = new VideoBuilder(
        600,
        400,
        0,
        20,
        24,
        VideoUtils.EncoderFacility.FFMPEG_SMALL,
        new File("/home/eric/experiments/balls.mp4"),
        Drawers.basic()
    );
    RandomGenerator rg = new Random();
    RealtimeViewer viewer = new RealtimeViewer(30, drawer);
    Consumer<Snapshot> consumer = viewer;
    while (engine.t() < 100) {
      Snapshot snapshot = engine.tick();
      consumer.accept(snapshot);
      if (engine.t() > lastBallT + ballInterval) {
        lastBallT = engine.t();
        engine.perform(new CreateAndTranslateRigidBody(
            ball,
            2,
            new Point(rg.nextDouble() * 10 + 2, snapshot.bodies()
                .stream()
                .mapToDouble(b -> b.poly().boundingBox().max().y())
                .max()
                .orElse(0) + Math.max(1d, 1d + rg.nextGaussian() * 1.1d))
        ));
      }
      if (engine.t() > 4 && engine.t() < 5) {
        engine.perform(new DetachAllAnchorsFromAnchorable(v1, v2));
      }
      for (Body body : snapshot.bodies()) {
        if (!(body instanceof UnmovableBody) && (body.poly().center().y() < -12)) {
          engine.perform(new RemoveBody(body));
        }
      }
      if (snapshot.bodies().contains(v1)) {
        engine.perform(new ActuateVoxel(v1, Math.sin(2d * Math.PI * engine.t())));
      }
      if (snapshot.bodies().contains(v2)) {
        double v = Math.sin(2d * Math.PI * engine.t());
        engine.perform(new ActuateVoxel(v2, v, 0, 0, -1));
      }
    }
    //ImageIO.write(imageBuilder.get(), "png", new File("/home/eric/experiments/simple.png"));
    //videoBuilder.get();
    if (drawer instanceof Profiled profiled) {
      System.out.println(profiled.values());
    }
  }
}
