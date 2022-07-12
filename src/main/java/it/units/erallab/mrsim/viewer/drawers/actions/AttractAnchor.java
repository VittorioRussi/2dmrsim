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

package it.units.erallab.mrsim.viewer.drawers.actions;

import it.units.erallab.mrsim.core.ActionOutcome;
import it.units.erallab.mrsim.util.DoubleRange;
import it.units.erallab.mrsim.viewer.DrawingUtils;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.function.BiPredicate;

/**
 * @author "Eric Medvet" on 2022/07/12 for 2dmrsim
 */
public class AttractAnchor extends AbstractActionOutcomeDrawer<it.units.erallab.mrsim.core.actions.AttractAnchor,
    Double> {

  private final static Color COLOR = DrawingUtils.alphaed(Color.GREEN, 0.25f);

  private final static DoubleRange RADIUS = new DoubleRange(0.1, 0.5);
  private final static double ANGLE = Math.PI / 6d;
  private final static double ATTRACTED_LENGTH_RATIO = 0.5;

  private final Color color;

  public AttractAnchor(Color color) {
    super(it.units.erallab.mrsim.core.actions.AttractAnchor.class, 0);
    this.color = color;
  }

  public AttractAnchor() {
    this(COLOR);
  }

  @Override
  protected BiPredicate<Double, Graphics2D> innerBuildTask(
      double t,
      ActionOutcome<it.units.erallab.mrsim.core.actions.AttractAnchor, Double> o
  ) {
    return (dT, g) -> {
      if (o.outcome().isEmpty()) {
        return true;
      }
      double magnitude = o.outcome().get();
      if (magnitude == 0) {
        return true;
      }
      double a = o.action().destination().point().diff(o.action().source().point()).direction();
      double sX = o.action().source().point().x();
      double sY = o.action().source().point().y();
      double dX = o.action().destination().point().x();
      double dY = o.action().destination().point().y();
      double l = RADIUS.denormalize(magnitude);
      Path2D triangle = new Path2D.Double();
      triangle.moveTo(sX, sY);
      triangle.lineTo(sX + Math.cos(a - ANGLE / 2d) * l, sY + Math.sin(a - ANGLE / 2d) * l);
      triangle.lineTo(sX + Math.cos(a + ANGLE / 2d) * l, sY + Math.sin(a + ANGLE / 2d) * l);
      triangle.closePath();
      g.setColor(color);
      g.fill(triangle);
      g.draw(new Line2D.Double(
          dX,
          dY,
          dX - Math.cos(a) * l * ATTRACTED_LENGTH_RATIO,
          dY - Math.sin(a) * l * ATTRACTED_LENGTH_RATIO
      ));
      return true;
    };
  }
}
