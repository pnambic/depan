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
import com.google.devtools.depan.graph_doc.GraphDocLogger;
import com.google.devtools.depan.graph_doc.eclipse.ui.editor.GraphEditorNodeViewProvider;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocContributor;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.EdgeMatcherSaveLoadConfig;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.PropertyDocumentReference;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import java.util.Collection;

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class NodeListCommandViewer extends CheckNodeTreeView {

  private final NodeListCommandInfo viewerInfo;

  private final HierarchyCache<GraphNode> hierarchies;

  /////////////////////////////////////
  // UX Elements

  private FromGraphDocListControl fromGraphDoc;

  private Label nameViewer;

  /////////////////////////////////////
  // Public methods

  public NodeListCommandViewer(
      Composite parent, NodeListCommandInfo viewerInfo) {
    super(parent);
    this.viewerInfo = viewerInfo;
    this.hierarchies = viewerInfo.buildHierachyCache();
  }

  @Override
  protected Composite createCommands(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 4);

    @SuppressWarnings("unused")
    Control label = Widgets.buildCompactLabel(result, "Hierarchy from: ");
    nameViewer = Widgets.buildGridLabel(result, "");

    Composite newView = setupNewView(result);
    newView.setLayoutData(Widgets.buildTrailFillData());

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

  /////////////////////////////////////
  // UX Setup

  private Composite setupNewView(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 2);

    Button create = Widgets.buildCompactPushButton(result, "Create");
    create.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        createContentEditor();
      }
    });

    fromGraphDoc = new FromGraphDocListControl(result);
    fromGraphDoc.setLayoutData(Widgets.buildHorzFillData());
    return result;
  }

  /////////////////////////////////////
  // Create Views

  /**
   * Create a new content editor from the selected tree elements
   * and other {@code GraphEditor} settings.
   */
  private void createContentEditor() {
    GraphNode topNode = getFirstNode();
    if (null == topNode) {
      GraphDocLogger.LOG.info("no topNode");
      return;
    }

    Collection<GraphNode> nodes = getSelectedNodes();
    if (nodes.isEmpty()) {
      GraphDocLogger.LOG.info("empty nodes");
      return;
    }

    // Prepare the wizard.
    FromGraphDocContributor choice = fromGraphDoc.getChoice();
    if (null == choice) {
      return;
    }

    try {
      viewerInfo.runWizard(choice, topNode, nodes);
    } catch (IllegalArgumentException ex) {
      // bad layout. don't do anything for the layout, but still finish the
      // creation of the view.
      GraphDocLogger.LOG.warn("Bad layout selected.");
    } catch (Exception errView) {
      GraphDocLogger.LOG.error("Unable to create view", errView);
    }
  }
}
