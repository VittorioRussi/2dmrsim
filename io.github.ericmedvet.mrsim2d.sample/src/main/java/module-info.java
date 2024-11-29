/*-
 * ========================LICENSE_START=================================
 * mrsim2d-sample
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
module io.github.ericmedvet.mrsim2d.sample {
  requires java.logging;

  uses io.github.ericmedvet.mrsim2d.core.engine.Engine;

  requires io.github.ericmedvet.mrsim2d.core;
  requires io.github.ericmedvet.mrsim2d.viewer;
  requires io.github.ericmedvet.jnb.core;
  requires io.github.ericmedvet.jnb.datastructure;
  requires io.github.ericmedvet.jsdynsym.core;
    requires java.desktop;

    exports io.github.ericmedvet.mrsim2d.sample;
}
