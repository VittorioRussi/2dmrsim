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

module io.github.ericmedvet.mrsim2d.core {
  uses io.github.ericmedvet.mrsim2d.core.engine.Engine;

  requires java.logging;
  requires io.github.ericmedvet.jsdynsym.core;
  requires io.github.ericmedvet.jsdynsym.control;
  requires io.github.ericmedvet.jnb.datastructure;

  exports io.github.ericmedvet.mrsim2d.core;
  exports io.github.ericmedvet.mrsim2d.core.actions;
  exports io.github.ericmedvet.mrsim2d.core.agents.gridvsr;
  exports io.github.ericmedvet.mrsim2d.core.agents.independentvoxel;
  exports io.github.ericmedvet.mrsim2d.core.agents.legged;
  exports io.github.ericmedvet.mrsim2d.core.bodies;
  exports io.github.ericmedvet.mrsim2d.core.engine;
  exports io.github.ericmedvet.mrsim2d.core.geometry;
  exports io.github.ericmedvet.mrsim2d.core.tasks;
  exports io.github.ericmedvet.mrsim2d.core.tasks.locomotion;
  exports io.github.ericmedvet.mrsim2d.core.tasks.balancing;
  exports io.github.ericmedvet.mrsim2d.core.tasks.jumping;
  exports io.github.ericmedvet.mrsim2d.core.tasks.piling;
  exports io.github.ericmedvet.mrsim2d.core.tasks.sumo;
  exports io.github.ericmedvet.mrsim2d.core.util;
}
