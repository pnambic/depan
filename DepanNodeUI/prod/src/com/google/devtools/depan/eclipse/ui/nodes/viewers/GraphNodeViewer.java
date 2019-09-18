/*
 * Copyright 2015 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.ui.nodes.viewers;

import com.google.devtools.depan.eclipse.ui.nodes.trees.NodeWrapperTreeSorter;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Selections;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import java.util.List;

/**
 * Provide tree structured viewer for all node lists.
 * 
 * @author <a href='mailto:leeca@pnambic.com'>Lee Carver</a>
 */
public class GraphNodeViewer extends Composite {

  protected static final NodeWrapperTreeSorter SORTER =
      new NodeWrapperTreeSorter();

  private NodeViewerProvider provider;

  /////////////////////////////////////
  // UX Elements

  private TreeViewer treeViewer;

  public GraphNodeViewer(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(1));

    treeViewer = createTreeViewer(this);
    treeViewer.getTree().setLayoutData(Widgets.buildGrabFillData());
  }

  public void setNvProvider(NodeViewerProvider nvProvider) {
    this.provider = nvProvider;
  }

  public Object findNodeObject(GraphNode node) {
    return provider.findNodeObject(node);
  }

  public void refresh() {
    PlatformObject treeRoots = provider.buildViewerRoots();
    treeViewer.setInput(treeRoots);
    provider.updateExpandState(treeViewer);
    treeViewer.refresh();
  }

  public void setNodeViewProvider(NodeViewerProvider provider) {
    setNvProvider(provider);
    refresh();
  }

  public void handleSelectNone() {
    treeViewer.setSelection(StructuredSelection.EMPTY);
  }

  public void handleCollapseAll() {
    treeViewer.collapseAll();
  }

  public void handleExpandAll() {
    treeViewer.expandAll();
  }

  /**
   * Derived types sometimes need to manipulate the tree, such as
   * for sorting.
   */
  protected TreeViewer getTreeViewer() {
    return treeViewer;
  }

  /**
   * Can be overridden to provide a customized tree viewer.
   * For example, the derived type {@link CheckNodeTreeView}
   * uses a {@code CheckboxTreeViewer}.
   */
  protected TreeViewer createTreeViewer(Composite parent) {
    int style = SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER
        | SWT.H_SCROLL | SWT.V_SCROLL;
    TreeViewer result = new TreeViewer(parent, style);
    result.setLabelProvider(new WorkbenchLabelProvider());
    result.setContentProvider(new BaseWorkbenchContentProvider());
    result.setComparator(SORTER);

    setupHierarchyMenu(result);

    return result;
  }

  /**
   * Available for derived classes to integrated context menus for items.
   */
  protected void setupHierarchyMenu(final TreeViewer viewer) {
    MenuManager menuMgr = new MenuManager();

    Menu menu = menuMgr.createContextMenu(viewer.getControl());

    menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(IMenuManager manager) {
        ISelection selection = viewer.getSelection();
        List<?> choices = Selections.getObjects(selection);
        if (choices.isEmpty()) {
          return;
        }
        if (choices.size() > 1) {
          addMultiActions(manager, choices);
          return;
        }

        Object menuElement = Selections.getFirstObject(selection);
        addItemActions(manager, menuElement);
      }});

    menuMgr.setRemoveAllWhenShown(true);
    viewer.getControl().setMenu(menu);
  }

  private void addMultiActions(IMenuManager manager, List<?> choices) {
    provider.addMultiActions(manager, choices);
  }

  private void addItemActions(IMenuManager manager, Object menuElement) {
    provider.addItemActions(manager, menuElement);
  }
}
