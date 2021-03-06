/*
 * Copyright 2007 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.eclipse.ui.nodes.viewers;

import com.google.devtools.depan.eclipse.ui.nodes.trees.NodeWrapper;
import com.google.devtools.depan.eclipse.ui.nodes.trees.NodeWrapperTreeSorter;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Sets;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class CheckNodeTreeView extends GraphNodeViewer {

  private CheckboxTreeViewer tree;

  private boolean recursiveTreeSelect;

  private ControlCheckStateProvider checkedProvider;

  public CheckNodeTreeView(Composite parent) {
    super(parent);
  }

  @Override
  protected CheckboxTreeViewer createTreeViewer(Composite parent) {
    int style = SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER
        | SWT.H_SCROLL | SWT.V_SCROLL;
    CheckboxTreeViewer result = new CheckboxTreeViewer(parent, style);
    result.setLabelProvider(new WorkbenchLabelProvider());
    result.setContentProvider(new BaseWorkbenchContentProvider());
    result.setComparator(new NodeWrapperTreeSorter());

     checkedProvider = new ControlCheckStateProvider();
     result.setCheckStateProvider(checkedProvider);

    result.addCheckStateListener(new ICheckStateListener() {
      @Override
      public void checkStateChanged(CheckStateChangedEvent event) {
        Object element = event.getElement();
        boolean checked = event.getChecked();
        if (element instanceof NodeWrapper) {
          NodeWrapper<?> wrapper = (NodeWrapper<?>) element;
          if (recursiveTreeSelect) {
            selectChildren(wrapper, checked);
            tree.refresh(element, true);
          } else {
            checkedProvider.updateNode(wrapper.getNode(), checked);
          }
        }
      }
    });

    tree = result;
    return result;
  }

  private void selectChildren(NodeWrapper<?> wrapper, boolean checked) {
    checkedProvider.updateNode(wrapper.getNode(), checked);

    for (Object child : wrapper.getChildren()) {
      if (child instanceof NodeWrapper) {
        selectChildren((NodeWrapper<?>) child, checked);
      }
    }
  }

  public void setRecursive(boolean recursiveTreeSelect) {
    this.recursiveTreeSelect = recursiveTreeSelect;
  }

  public boolean getRecursive() {
    return recursiveTreeSelect;
  }

  public void handleSelectNone() {
    super.handleSelectNone();
    checkedProvider.clearNodes();
    tree.refresh();
  }

  public GraphNode getFirstNode() {
    for (Object item : getCheckedElements()) {
      if (item instanceof NodeWrapper) {
        return ((NodeWrapper<?>) item).getNode();
      }
    }
    return null;
  }

  public Collection<GraphNode> getSelectedNodes() {
    Set<GraphNode> result = Sets.newHashSet();
    for (Object item : getCheckedElements()) {
      if (item instanceof NodeWrapper) {
        GraphNode node = ((NodeWrapper<?>) item).getNode();
        result.add(node);
      }
    }
    return result;
  };

  private Object[] getCheckedElements() {
    return tree.getCheckedElements();
  }

  public void addCheckedNodes(Collection<GraphNode> additions) {
    checkedProvider.addNodes(additions);
  }

  public Collection<GraphNode> getCheckedNodes() {
    return checkedProvider.getCheckedNodes();
  }

  public void setCheckedNodes(Collection<GraphNode> checkedNodes) {
    checkedProvider.setCheckedNodes(checkedNodes);
    tree.refresh();
  }

  public static class ControlCheckStateProvider implements ICheckStateProvider {

    private final Collection<GraphNode> checkedNodes = new HashSet<GraphNode>();

    public ControlCheckStateProvider(Collection<GraphNode> initialChecks) {
      checkedNodes.addAll(initialChecks);
    }

    public Collection<GraphNode> getCheckedNodes() {
      return new ArrayList<GraphNode>(checkedNodes);
    }

    public ControlCheckStateProvider() {
      // TODO Auto-generated constructor stub
    }

    @Override
    public boolean isChecked(Object item) {
      if (item instanceof NodeWrapper) {
        GraphNode node = ((NodeWrapper<?>) item).getNode();
        return checkedNodes.contains(node);
      }

      return false;
    }

    @Override
    public boolean isGrayed(Object element) {
      return false;
    }

    public void clearNodes() {
      checkedNodes.clear();
    }

    public void addNode(GraphNode addition) {
      checkedNodes.add(addition);
    }

    public void addNodes(Collection<GraphNode> additions) {
      checkedNodes.addAll(additions);
    }

    public void removeNode(GraphNode subtrahend) {
      checkedNodes.remove(subtrahend);
    }

    public void setCheckedNodes(Collection<GraphNode> nodes) {
      checkedNodes.clear();
      checkedNodes.addAll(nodes);
    }

    public void updateNode(GraphNode node, boolean checked) {
      if (checked) {
        addNode(node);
      } else {
        removeNode(node);
      }
    }
  }
}
