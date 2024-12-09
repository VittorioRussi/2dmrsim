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
import io.github.ericmedvet.jnb.datastructure.DoubleRange;
import io.github.ericmedvet.jnb.datastructure.FormattedNamedFunction;
import io.github.ericmedvet.mrsim2d.core.tasks.AgentsOutcome;
import io.github.ericmedvet.mrsim2d.core.tasks.balancing.BalancingAgentsOutcome;
import java.util.function.Function;

@Discoverable(prefixTemplate = "sim|s.function|f.outcome|o")
public class OutcomeFunctions {

  private OutcomeFunctions() {}

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaAvgH(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).allAgentsAverageHeight();
    return FormattedNamedFunction.from(f, format, "all.agents.avg.h").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaAvgMaxH(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).allAgentsAverageMaxHeight();
    return FormattedNamedFunction.from(f, format, "all.agents.avg.max.h").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaAvgMaxW(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).allAgentsAverageMaxWidth();
    return FormattedNamedFunction.from(f, format, "all.agents.avg.max.w").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaAvgW(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).allAgentsAverageWidth();
    return FormattedNamedFunction.from(f, format, "all.agents.avg.w").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaFinalAvgW(
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f = AgentsOutcome::allAgentsFinalAverageWidth;
    return FormattedNamedFunction.from(f, format, "all.agents.final.avg.w").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaFinalMrH(
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f = AgentsOutcome::allAgentsFinalMidrangeHeight;
    return FormattedNamedFunction.from(f, format, "all.agents.final.mr.h").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaFinalMrW(
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f = AgentsOutcome::allAgentsFinalMidrangeWidth;
    return FormattedNamedFunction.from(f, format, "all.agents.final.mr.w").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaFinalH(
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f = AgentsOutcome::allAgentsFinalHeight;
    return FormattedNamedFunction.from(f, format, "all.agents.final.h").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaFinalW(
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f = AgentsOutcome::allAgentsFinalWidth;
    return FormattedNamedFunction.from(f, format, "all.agents.final.w").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaFinalMaxH(
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f = AgentsOutcome::allAgentsFinalMaxHeight;
    return FormattedNamedFunction.from(f, format, "all.agents.final.max.h").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaFinalMaxW(
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f = AgentsOutcome::allAgentsFinalMaxWidth;
    return FormattedNamedFunction.from(f, format, "all.agents.final.max.w").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaFinalMinH(
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f = AgentsOutcome::allAgentsFinalMinHeight;
    return FormattedNamedFunction.from(f, format, "all.agents.final.min.h").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaFinalMinW(
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f = AgentsOutcome::allAgentsFinalMinWidth;
    return FormattedNamedFunction.from(f, format, "all.agents.final.min.w").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaMaxH(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).allAgentsMaxHeight();
    return FormattedNamedFunction.from(f, format, "all.agents.max.h").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> aaMaxW(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).allAgentsMaxWidth();
    return FormattedNamedFunction.from(f, format, "all.agents.max.w").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> avgSwingAngle(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, BalancingAgentsOutcome> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<BalancingAgentsOutcome, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).avgSwingAngle();
    return FormattedNamedFunction.from(f, format, "avg.swing.angle").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> avgSwingAngleWithMalus(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "malus", dD = Math.PI / 2d) double malus,
      @Param(value = "of", dNPM = "f.identity()") Function<X, BalancingAgentsOutcome> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<BalancingAgentsOutcome, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).avgSwingAngleWithMalus(malus);
    return FormattedNamedFunction.from(f, format, "avg.swing.angle.with.malus")
        .compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> faAvgArea(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).firstAgentAverageArea();
    return FormattedNamedFunction.from(f, format, "first.agent.avg.area").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> faAvgBBMinY(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).firstAgentAverageBBMinY();
    return FormattedNamedFunction.from(f, format, "first.agent.avg.bb.min.y")
        .compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> faAvgTerrainHeight(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).firstAgentAverageTerrainHeight();
    return FormattedNamedFunction.from(f, format, "first.agent.avg.terrain.h")
        .compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> faAvgY(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).firstAgentAverageY();
    return FormattedNamedFunction.from(f, format, "first.agent.avg.y").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> faMaxBBMinY(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).firstAgentMaxBBMinY();
    return FormattedNamedFunction.from(f, format, "first.agent.max.bb.min.y")
        .compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> faMaxMaxRelJumpH(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).firstAgentMaxRelativeJumpHeight();
    return FormattedNamedFunction.from(f, format, "first.agent.max.rel.jump.h")
        .compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> faMaxY(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).firstAgentMaxY();
    return FormattedNamedFunction.from(f, format, "first.agent.max.y").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> faXDistance(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).firstAgentXDistance();
    return FormattedNamedFunction.from(f, format, "first.agent.distance.x").compose(beforeF);
  }

  @SuppressWarnings("unused")
  public static <X> Function<X, Double> faXVelocity(
      @Param(value = "transientTime", dD = 5.0) double transientTime,
      @Param(value = "of", dNPM = "f.identity()") Function<X, AgentsOutcome<?>> beforeF,
      @Param(value = "format", dS = "%.1f") String format) {
    Function<AgentsOutcome<?>, Double> f =
        o -> o.subOutcome(new DoubleRange(transientTime, o.duration())).firstAgentXVelocity();
    return FormattedNamedFunction.from(f, format, "first.agent.velocity.x").compose(beforeF);
  }
}
