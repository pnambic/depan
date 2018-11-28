/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.graph_doc.eclipse.ui.widgets;

import com.google.devtools.depan.eclipse.ui.nodes.cache.HierarchyCache;
import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.CheckNodeTreeView;
import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.graph_doc.eclipse.ui.editor.GraphEditorNodeViewProvider;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.EdgeMatcherSaveLoadConfig;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.PropertyDocumentReference;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class NodeListCommandViewer extends CheckNodeTreeView {

  private final HierarchyCache<GraphNode> hierarchies;

  /////////////////////////////////////
  // UX Elements

  private Label nameViewer;

  /////////////////////////////////////
  // Public methods

  public NodeListCommandViewer(Composite parent, NodeListCommandInfo viewerInfo) {
    super(parent);
    this.hierarchies = viewerInfo.buildHierachyCache();
  }

  @Override
  protected Composite createCommands(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 4);

    @SuppressWarnings("unused")
    Control label = Widgets.buildCompactLabel(result, "Hierarchy from: ");
    nameViewer = Widgets.buildGridLabel(result, "");

    return result;
  }

  public void setHierachyInput(
      PropertyDocumentReference<GraphEdgeMatcherDescriptor> selectedRelSet,
      IProject project) {
    EdgeMatcher<String> matcher = selectedRelSet.getDocument().getInfo();
    GraphData<GraphNode> graphData = hierarchies.getHierarchy(matcher);
    GraphEditorNodeViewProvider<GraphNode> provider =
        new GraphEditorNodeViewProvider<GraphNode>(graphData);
    setNvProvider(provider);
    refresh();

    nameViewer.setText(selectedRelSet.getDocument().getName());
  }

  public void handleHierarchyFrom(Shell shell, IProject proj) {
    PropertyDocumentReference<GraphEdgeMatcherDescriptor> rsrc =
        EdgeMatcherSaveLoadConfig.CONFIG.loadResource(shell, proj);
    if (null != rsrc) {
      setHierachyInput(rsrc, proj);
    }
  }
}
