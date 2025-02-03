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

import io.github.ericmedvet.mrsim2d.core.bodies.Anchor;
import io.github.ericmedvet.mrsim2d.core.geometry.Point;
import io.github.ericmedvet.mrsim2d.core.geometry.Poly;
import io.github.ericmedvet.mrsim2d.core.geometry.Segment;
import java.util.*;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

public class RigidBody implements io.github.ericmedvet.mrsim2d.core.bodies.RigidBody, MultipartBody {

  private final Body body;
  private final double mass;
  private final Vector2 initialFirstSideDirection;
  private final List<Anchor> anchors;

  public RigidBody(
      Poly convexPoly,
      double mass,
      double anchorsDensity,
      double friction,
      double restitution,
      double linearDamping,
      double angularDamping,
      double anchorSideDistance
  ) {
    this.mass = mass;
    body = new Body();
    body.addFixture(Utils.poly(convexPoly), mass / convexPoly.area(), friction, restitution);
    body.setMass(MassType.NORMAL);
    body.setLinearDamping(linearDamping);
    body.setAngularDamping(angularDamping);
    body.setUserData(this);
    initialFirstSideDirection = getFirstSideDirection();
    if (Double.isFinite(anchorsDensity)) {
      List<Anchor> localAnchors = new ArrayList<>();
      for (Segment segment : convexPoly.sides()) {
        double nOfAnchors = Math.max(Math.floor(segment.length() * anchorsDensity), 2);
        for (double i = 0; i < nOfAnchors; i = i + 1) {
          Point sidePoint = segment.pointAtRate((i + 1d) / (nOfAnchors + 1d));
          Point aP = sidePoint.sum(new Point(segment.direction() + Math.PI / 2d).scale(anchorSideDistance));
          localAnchors.add(new BodyAnchor(body, aP, this));
        }
      }
      anchors = Collections.unmodifiableList(localAnchors);
    } else {
      anchors = List.of();
    }
  }

  @Override
  public List<Anchor> anchors() {
    return anchors;
  }

  @Override
  public double angle() {
    Vector2 currentFirstSideDirection = getFirstSideDirection();
    return -currentFirstSideDirection.getAngleBetween(initialFirstSideDirection);
  }

  @Override
  public Point centerLinearVelocity() {
    return Utils.point(body.getLinearVelocity());
  }

  @Override
  public double mass() {
    return mass;
  }

  @Override
  public Poly poly() {
    Transform t = body.getTransform();
    return new Poly(
        Arrays.stream(((Polygon) body.getFixture(0).getShape()).getVertices())
            .map(v -> {
              Vector2 cv = v.copy();
              t.transform(cv);
              return Utils.point(cv);
            })
            .toArray(Point[]::new)
    );
  }

  @Override
  public Collection<Body> getBodies() {
    return List.of(body);
  }

  @Override
  public Collection<Joint<Body>> getJoints() {
    return List.of();
  }

  private Vector2 getFirstSideDirection() {
    Poly poly = poly();
    return new Vector2(
        poly.vertexes()[1].x() - poly.vertexes()[0].x(),
        poly.vertexes()[1].y() - poly.vertexes()[0].y()
    );
  }

  @Override
  public String toString() {
    return String.format("%s at %s", this.getClass().getSimpleName(), poly().center());
  }
}
