/*-
 * ========================LICENSE_START=================================
 * mrsim2d-engine-dyn4j
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

package io.github.ericmedvet.mrsim2d.engine.dyn4j;

import io.github.ericmedvet.jnb.datastructure.Pair;
import io.github.ericmedvet.mrsim2d.core.bodies.Anchor;
import io.github.ericmedvet.mrsim2d.core.geometry.Point;
import io.github.ericmedvet.mrsim2d.core.geometry.Poly;
import io.github.ericmedvet.mrsim2d.core.geometry.Segment;
import java.util.*;
import org.dyn4j.dynamics.AbstractPhysicsBody;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.MassType;

public class UnmovableBody implements io.github.ericmedvet.mrsim2d.core.bodies.UnmovableBody, MultipartBody {

  private final Poly poly;
  private final List<Body> bodies;
  private final List<Anchor> anchors;

  private final Point initialCenter;

  public UnmovableBody(
      Poly poly,
      double anchorsDensity,
      double friction,
      double restitution,
      double anchorSideDistance
  ) {
    this.poly = poly;
    List<Poly> parts = (poly.vertexes().length > 3) ? Utils.decompose(poly) : List.of(poly);
    List<Pair<Body, Poly>> bodyPairs = parts.stream()
        .map(c -> {
          Convex convex = Utils.poly(c);
          Body body = new Body();
          body.addFixture(convex, 1d, friction, restitution);
          body.setMass(MassType.INFINITE);
          return new Pair<>(body, c);
        })
        .toList();
    bodies = bodyPairs.stream().map(Pair::first).toList();
    initialCenter = center(bodies);
    bodies.forEach(b -> b.setUserData(this));
    if (Double.isFinite(anchorsDensity)) {
      List<Anchor> localAnchors = new ArrayList<>();
      for (Segment segment : poly.sides()) {
        double nOfAnchors = Math.max(Math.floor(segment.length() * anchorsDensity), 2);
        for (double i = 0; i < nOfAnchors; i = i + 1) {
          Point sidePoint = segment.pointAtRate((i + 1d) / (nOfAnchors + 1d));
          Point aP = sidePoint.sum(new Point(segment.direction() + Math.PI / 2d).scale(anchorSideDistance));
          Body closest = bodies.stream()
              .min(
                  Comparator.comparingDouble(
                      b -> Utils.point(b.getLocalCenter()).distance(aP)
                  )
              )
              .orElseThrow();
          localAnchors.add(new BodyAnchor(closest, aP, this));
        }
      }
      anchors = Collections.unmodifiableList(localAnchors);
    } else {
      anchors = List.of();
    }
  }

  private static Point center(List<Body> bodies) {
    return Point.average(
        bodies.stream()
            .map(AbstractPhysicsBody::getWorldCenter)
            .map(Utils::point)
            .toArray(Point[]::new)
    );
  }

  @Override
  public List<Anchor> anchors() {
    return anchors;
  }

  @Override
  public Collection<Body> getBodies() {
    return bodies;
  }

  @Override
  public Collection<Joint<Body>> getJoints() {
    return List.of();
  }

  @Override
  public Poly poly() {
    // assuming it can only be translated, we just check diff wrt initial center
    Point t = center(bodies).diff(initialCenter);
    return new Poly(Arrays.stream(poly.vertexes()).map(p -> p.sum(t)).toArray(Point[]::new));
  }

  @Override
  public String toString() {
    return String.format("%s at %s", this.getClass().getSimpleName(), poly().center());
  }
}
