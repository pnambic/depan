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

package com.google.devtools.depan.view_doc.eclipse.ui.filters;

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.nodes.filters.eclipse.ui.filters.DefaultingFilterContribution;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.FilterEditorDialog;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.SimpleFilterEditorDialog;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;

/**
 * Provides labels, {@link Form}s, factories, and dialog editors
 * for non-view {@link ViewNodesFilter}s.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ExcludeViewNodesFilterContribution
  extends DefaultingFilterContribution<ViewNodesFilter> {

  @Override
  public String getLabel() {
    return "Only non-view nodes";
  }

  @Override
  public Form getForm() {
    return Form.ELEMENT;
  }

  @Override
  public ViewNodesFilter createElementFilter(DependencyModel model) {
    return new ViewNodesFilter(
        getLabel(), "Exclude nodes present in view", false);
  }

  @Override
  public boolean handlesFilterInstance(ContextualFilter filter) {
    if (isAssignableAs(filter, ViewNodesFilter.class)) {
      return ( ! ((ViewNodesFilter) filter).isInclude());
    }
    return false;
  }

  @Override
  public FilterEditorDialog<ViewNodesFilter> buildEditorDialog(
      Shell shell, ContextualFilter filter,
      DependencyModel model, IProject project) {
    if (handlesFilterInstance(filter)) {
      return new SimpleFilterEditorDialog<ViewNodesFilter>(
          shell, (ViewNodesFilter) filter, model, project);
    }
    throw buildNotAssignable(filter, ViewNodesFilter.class);
  }
}
