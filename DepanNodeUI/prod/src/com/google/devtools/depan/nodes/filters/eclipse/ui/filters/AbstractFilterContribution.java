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

package com.google.devtools.depan.nodes.filters.eclipse.ui.filters;

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.nodes.filters.eclipse.ui.plugins.ContextualFilterContributor;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class AbstractFilterContribution<T extends ContextualFilter>
    implements ContextualFilterContributor<T> {

  @Override
  public T createElementFilter(DependencyModel model) {
    throw buildUnsupported("element");
  }

  @Override
  public T createWrapperFilter(ContextualFilter filter) {
    throw buildUnsupported("wrapper");
  }

  @Override
  public T createGroupFilter(Collection<ContextualFilter> filters) {
    throw buildUnsupported("group");
  }

  @Override
  public T createStepsFilter(List<ContextualFilter> filters) {
    throw buildUnsupported("steps");
  }

  /////////////////////////////////////
  // Utilities for derived types

  protected boolean isAssignableAs(ContextualFilter filter, Class<?> type) {
    return filter.getClass().isAssignableFrom(type);
  }

  protected IllegalArgumentException buildUnsupported(String kind) {
    String msg = MessageFormat.format(
        "No {0} factory defined for {1}", kind, getLabel());
    throw new UnsupportedOperationException(msg);
  }

  protected IllegalArgumentException buildNotAssignable(
      ContextualFilter filter, Class<?> type) {
    String msg = MessageFormat.format(
        "Filter {0} is not assignable as a {1} type.",
        filter.getName(), type.getName());
    return new IllegalArgumentException(msg);
  }
}
