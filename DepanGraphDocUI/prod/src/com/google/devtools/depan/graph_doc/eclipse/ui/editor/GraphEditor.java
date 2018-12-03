/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.graph_doc.eclipse.ui.editor;

import com.google.devtools.depan.eclipse.ui.nodes.cache.HierarchyCache;
import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.CheckNodeTreeView;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProviders;
import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.graph_doc.GraphDocLogger;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocWizard;
import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResourceBuilder;
import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResources;
import com.google.devtools.depan.graph_doc.eclipse.ui.widgets.GraphViewInfoPanel;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.persistence.ResourceCache;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.EdgeMatcherSaveLoadConfig;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.PropertyDocumentReference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import java.util.Collection;

/**
 * Show the entire set of nodes from an analysis tree.  Allow the user
 * to select interesting subsets for more detailed investigation.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class GraphEditor extends MultiPageEditorPart {

  public static final String ID =
      "com.google.devtools.depan.graph_doc.eclipse.ui.editor.GraphEditor";

  private static final boolean RECURSIVE_SELECT_DEFAULT = true;

  /////////////////////////////////////
  // Editor input data

  private IFile file = null;

  private GraphDocument graph = null;

  private GraphResources graphResources;

  private List associatedViews = null;

  /////////////////////////////////////

  private HierarchyCache<GraphNode> hierarchies = null;

  /////////////////////////////////////
  // UX Elements

  private CheckNodeTreeView checkNodeTreeView = null;

  private GraphViewInfoPanel infoPanel = null;

  // TODO(leeca): Figure out how to turn this back on
  // private Binop<GraphModel> binop = null;

  /////////////////////////////////////
  // Basic Getters and Setters

  /**
   * Provide the project to use for saving resource associated with this
   * instance.  Under normal circumstances, it's the same project that contains
   * the underlying view document.
   */
  public IProject getResourceProject() {
    if (null != file) {
      return file.getProject();
    }
    return null;
  }

  /////////////////////////////////////
  // Public methods

  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException {
    super.init(site, input);
    initFromInput(input);
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
  }

  @Override
  public void doSaveAs() {
  }

  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  /////////////////////////////////////
  // Editor setup

  private void initFromInput(IEditorInput input) throws PartInitException {

    if (!(input instanceof IFileEditorInput)) {
      throw new PartInitException("Invalid Input: Must be IFileEditorInput");
    }

    // load the graph
    file = ((IFileEditorInput) input).getFile();

    GraphDocLogger.LOG.info("Reading {}", file.getRawLocationURI());
    graph = ResourceCache.fetchGraphDocument(file);
    GraphDocLogger.LOG.info("  DONE");

    DependencyModel model = graph.getDependencyModel();
    graphResources = GraphResourceBuilder.forModel(model);
    hierarchies = new HierarchyCache<GraphNode>(
        NodeTreeProviders.GRAPH_NODE_PROVIDER, graph.getGraph());

    // set the title to the filename, excepted the file extension
    String title = file.getName();
    title = title.substring(0, title.lastIndexOf('.'));
    this.setPartName(title);
  }

  /////////////////////////////////////
  // UX Setup

  @Override
  protected void createPages() {
    Composite parent = getContainer();
    createPage0(parent);
    createPage1(parent);
  }

  private void createPage0(Composite parent) {
    Composite composite = Widgets.buildGridContainer(parent, 1);

    infoPanel = new GraphViewInfoPanel(composite);
    infoPanel.setLayoutData(Widgets.buildHorzFillData());

    checkNodeTreeView = new CheckNodeTreeView(composite);
    checkNodeTreeView.setLayoutData(Widgets.buildGrabFillData());
    checkNodeTreeView.setRecursive(RECURSIVE_SELECT_DEFAULT);

    setHierachyInput(graphResources.getDefaultEdgeMatcher().getDocument());

    int index = addPage(composite);
    setPageText(index, "New View");
  }

  private void createPage1(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    composite.setLayout(layout);
    layout.numColumns = 2;
    layout.makeColumnsEqualWidth = true;

    Composite leftPanel = new Composite(composite, SWT.NONE);
    GridLayout leftLayout = new GridLayout();
    leftPanel.setLayout(leftLayout);
    leftLayout.numColumns = 1;
    leftPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    Button refresh = new Button(leftPanel, SWT.PUSH);
    refresh.setText("Refresh list");
    refresh.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        updateList();
      }
    });

    associatedViews = new List(
        leftPanel, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
    associatedViews.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));
    // fill associated Views list.
    updateList();
    associatedViews.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        selectView();
      }
    });

    // Right panel --------------
    // TODO(leeca): Figure out how to turn this back on
    // binop = new Binop<GraphModel>(composite, SWT.None, this, this);
    // binop.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    int index = addPage(composite);
    setPageText(index, "Opened related Views");
  }

  /////////////////////////////////////
  // UX Handler

  public void handleSelectNone() {
    checkNodeTreeView.handleSelectNone();
  }

  public boolean getRecursiveSelect() {
    return checkNodeTreeView.getRecursive();
  }

  public void handleSelectRecursive() {
    boolean state =  checkNodeTreeView.getRecursive();

    checkNodeTreeView.setRecursive(!state);
  }

  public void handleHierarchyFrom() {
    Shell shell = getEditorSite().getShell();
    PropertyDocumentReference<GraphEdgeMatcherDescriptor> rsrc =
        EdgeMatcherSaveLoadConfig.CONFIG.loadResource(shell, getResourceProject());
    if (null != rsrc) {
      setHierachyInput(rsrc.getDocument());
    }
  }

  public void handleCollapseAll() {
    checkNodeTreeView.handleCollapseAll();
  }

  public void handleExpandAll() {
    checkNodeTreeView.handleExpandAll();
  }

  public Collection<GraphNode> getSelectedNodes() {
    return checkNodeTreeView.getSelectedNodes();
  }

  public GraphNode getTopNode(Collection<GraphNode> source) {
    return source.isEmpty() ? null : source.iterator().next();
  }

  public void setHierachyInput(GraphEdgeMatcherDescriptor document) {
    EdgeMatcher<String> matcher = document.getInfo();
    GraphData<GraphNode> graphData = hierarchies.getHierarchy(matcher);
    GraphEditorNodeViewProvider<GraphNode> provider =
        new GraphEditorNodeViewProvider<GraphNode>(graphData);
    String matcherName = document.getName();
    checkNodeTreeView.setNodeViewProvider(provider);
    infoPanel.setMatcherName(matcherName);
  }

  public void runFromGraphDocWizard(
      FromGraphDocWizard wizard, GraphNode topNode, Collection<GraphNode> nodes) {
    String name = FromGraphDocWizard.calcDetailName(topNode);
    wizard.init(file, graph, graphResources, nodes, name);

    // Run the wizard.
    WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
    dialog.open();
  }

  /////////////////////////////////////
  // Support methods

  protected void selectView() {
/* TODO(leeca):  Need richer ReferencedGraphModel
    associatedViews.getSelectionIndices();
    if (associatedViews.getSelectionCount() == 1) {
      for (ViewModel v : graph.getViews()) {
        if (v.getName().equals(associatedViews.getSelection()[0])) {
          binop.setFirst(v);
        }
      }
    } else if (associatedViews.getSelectionCount() == 2) {
      ViewModel v1 = null;
      ViewModel v2 = null;
      for (ViewModel v : graph.getViews()) {
        if (v.getName().equals(associatedViews.getSelection()[0])) {
          v1 = v;
        } else if (v.getName().equals(associatedViews.getSelection()[1])) {
          v2 = v;
        }
      }
      binop.setBoth(v1, v2);
    }
*/
  }

  private void updateList() {
/* TODO(leeca):  Need richer ReferencedGraphModel
    associatedViews.removeAll();
    for (ViewModel v : graph.getViews()) {
      associatedViews.add(v.getName());
    }
    associatedViews.redraw();
*/
  }
}
