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

package it.units.erallab.mrsim.core.geometry;

import java.util.Arrays;

/**
 * @author "Eric Medvet" on 2022/07/06 for 2dmrsim
 */
public record Point(double x, double y) implements Shape {

  public static Point average(Point... points) {
    return new Point(
        Arrays.stream(points)
            .mapToDouble(Point::x)
            .average()
            .orElseThrow(() -> new IllegalArgumentException("There has to be at least one point")),
        Arrays.stream(points)
            .mapToDouble(Point::y)
            .average()
            .orElseThrow(() -> new IllegalArgumentException("There has to be at least one point"))
    );
  }

  public static Point min(Point... points) {
    return Arrays.stream(points).sequential()
        .reduce((p1, p2) -> new Point(
            Math.min(p1.x, p2.x),
            Math.min(p1.y, p2.y)
        ))
        .orElseThrow(() -> new IllegalArgumentException("There has to be at least one point"));
  }

  public static Point max(Point... points) {
    return Arrays.stream(points).sequential()
        .reduce((p1, p2) -> new Point(
            Math.max(p1.x, p2.x),
            Math.max(p1.y, p2.y)
        ))
        .orElseThrow(() -> new IllegalArgumentException("There has to be at least one point"));
  }

  @Override
  public BoundingBox boundingBox() {
    return new BoundingBox(this, this);
  }

  @Override
  public double area() {
    return 0d;
  }

  @Override
  public Point center() {
    return this;
  }
}
