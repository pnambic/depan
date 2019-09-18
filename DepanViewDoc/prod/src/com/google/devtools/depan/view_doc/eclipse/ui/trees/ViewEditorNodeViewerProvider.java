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

package com.google.devtools.depan.view_doc.eclipse.ui.trees;

import com.google.devtools.depan.eclipse.ui.collapse.trees.CollapseDataWrapper;
import com.google.devtools.depan.eclipse.ui.collapse.trees.CollapseTreeRoot;
import com.google.devtools.depan.eclipse.ui.nodes.trees.ViewerRoot;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeViewerProvider;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.filters.eclipse.ui.plugins.ContextualFilterContributor;
import com.google.devtools.depan.nodes.filters.eclipse.ui.plugins.ContextualFilterRegistry;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.FilterEditorDialog;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.sequence.NodeKindFilter;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeViewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link NodeViewerProvider} for the {@link ViewEditor}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ViewEditorNodeViewerProvider implements NodeViewerProvider {

  private final ViewEditor editor;

  public ViewEditorNodeViewerProvider(ViewEditor editor) {
    this.editor = editor;
  }

  @Override
  public void addMultiActions(IMenuManager manager, List<?> choices) {
    List<GraphNode> masters = buildMastersFromChoices(choices);
    if (!masters.isEmpty()) {
      manager.add(new Action("Uncollapse selected", IAction.AS_PUSH_BUTTON) {
        @Override
        public void run() {
          editor.uncollapseMasterNodes(masters);
        }
      });
    }
  }

  @Override
  public void addItemActions(IMenuManager manager, Object menuElement) {
    if (menuElement instanceof ActionableViewerObject) {
      ((ActionableViewerObject) menuElement).addItemActions(manager, editor);
    }
    if (menuElement instanceof CollapseTreeRoot) {
      CollapseTreeRoot<?> root = (CollapseTreeRoot<?>) menuElement;

      manager.add(new Action("Uncollapse each", IAction.AS_PUSH_BUTTON) {
        @Override
        public void run() {
          List<GraphNode> masterNodes = buildMastersFromRoot(root);
          editor.uncollapseMasterNodes(masterNodes);
        }
      });
      manager.add(new Action("Select by type", IAction.AS_PUSH_BUTTON) {
        @Override
        public void run() {
          selectByType();
        }
      });
    }

    if (menuElement instanceof CollapseDataWrapper<?>) {
      CollapseDataWrapper<?> root = (CollapseDataWrapper<?>) menuElement;
      final GraphNode master = root.getCollapseData().getMasterNode();
      manager.add(new Action("Uncollapse", IAction.AS_PUSH_BUTTON) {
        @Override
        public void run() {
          editor.uncollapseMasterNode(master);
        }
      });
    }
  }

  @Override
  public ViewerRoot buildViewerRoots() {
    return editor.buildViewerRoot();
  }

  @Override
  public Object findNodeObject(GraphNode node) {
    return editor.findViewerNodeObject(node);
  }

  @Override
  public void updateExpandState(TreeViewer viewer) {
    int nodeCnt = editor.getViewGraph().getNodes().size();
    if (nodeCnt < NodeViewerProvider.AUTO_EXPAND_LIMIT) {
      viewer.expandAll();
    } else {
      viewer.expandToLevel(1);
    }
  }

  private List<GraphNode> buildMastersFromRoot(CollapseTreeRoot<?> root) {
    CollapseDataWrapper<?>[] children = root.getChildren();
    List<GraphNode> result = new ArrayList<>(children.length);
    for (CollapseDataWrapper<?> child : children) {
      result.add(child.getCollapseData().getMasterNode());
    }
    return result;
  }

  private List<GraphNode> buildMastersFromChoices(List<?> choices) {
    List<GraphNode> result = new ArrayList<>(choices.size());
    for (Object choice : choices) {
      if (choice instanceof CollapseDataWrapper) {
        result.add(((CollapseDataWrapper<?>) choice).getCollapseData().getMasterNode());
      } else {
        return Collections.emptyList();
      }
    }
    return result;
  }

  private void selectByType() {
    // stubbed out ..
  }

/* Waiting for node-types
  private void selectByType() {
    editor.get
    filter = new NodeKindFilter(null);
  }

  private ContextualFilter editFilter(ContextualFilter filter) {
    ContextualFilterContributor<?> contrib =
        ContextualFilterRegistry.findRegistryContributor(filter);
    if (null == contrib) {
      return null;
    }

    FilterEditorDialog<?> dialog =
        contrib.buildEditorDialog(getShell(), filter, getModel(), getProject());
    if (null == dialog) {
      return null;
    }
    if (Dialog.OK == dialog.open()) {
      return dialog.getResult();
    }
    return null;
  }
*/
}
