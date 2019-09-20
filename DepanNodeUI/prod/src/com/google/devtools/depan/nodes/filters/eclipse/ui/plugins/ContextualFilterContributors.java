/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.nodes.filters.eclipse.ui.plugins;

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;

import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ContextualFilterContributors {

  private ContextualFilterContributors() {
    // Prevent instantiation.
  }

  public static ContextualFilter createFilter(
      ContextualFilterContributor<? extends ContextualFilter> contrib,
      DependencyModel model,
      List<ContextualFilter> filters) {
    switch (contrib.getForm()) {
    case ELEMENT:
      return contrib.createElementFilter(model);
    case GROUP:
      return contrib.createGroupFilter(filters);
    case STEPS:
      return contrib.createStepsFilter(filters);
    case WRAPPER:
      return contrib.createWrapperFilter(filters.get(0));
    }
    return null;
  }
}
