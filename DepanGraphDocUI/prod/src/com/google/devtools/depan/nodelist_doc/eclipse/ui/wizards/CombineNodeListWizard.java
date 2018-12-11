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

package com.google.devtools.depan.nodelist_doc.eclipse.ui.wizards;

import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodelist_doc.eclipse.ui.editor.NodeListEditor;
import com.google.devtools.depan.nodelist_doc.eclipse.ui.wizards.CombineOperationPart.CombineOperation;
import com.google.devtools.depan.nodelist_doc.persistence.NodeListLoader;
import com.google.devtools.depan.platform.WorkspaceTools;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wizard to create new node lists by subtraction.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class CombineNodeListWizard extends Wizard {

  /**
   * Eclipse extension identifier for this wizard.
   */
  public static final String ANALYSIS_WIZARD_ID =
      "com.google.devtools.depan.nodelist_doc.eclipse.ui.wizards.CombineNodeListWizard";

  private final NodeListEditor editor;

  private CombineNodeListPage page;

  /**
   * Create the wizard (with a progress monitor)
   */
  public CombineNodeListWizard(NodeListEditor editor) {
    super();
    this.editor = editor;
    setNeedsProgressMonitor(true);
  }

  /**
   * This method is called when 'Finish' button is pressed in the wizard.
   * The new document generation is performed in a separate thread.
   */
  @Override
  public boolean performFinish() {
    IRunnableWithProgress op = new IRunnableWithProgress() {

      @Override
      public void run(IProgressMonitor monitor)
          throws InvocationTargetException {
        performOperation(monitor);
      }
    };

    try {
      getContainer().run(false, false, op);
    } catch (InterruptedException e) {
      return false;
    } catch (InvocationTargetException e) {
      Throwable realException = e.getTargetException();
      MessageDialog.openError(getShell(), "Error", realException.getMessage());
      realException.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Adding the page to the wizard.
   */
  @Override
  public void addPages() {
    page = new CombineNodeListPage();
    addPage(page);
  }

  private void performOperation(IProgressMonitor monitor) {
    CombineOperation operation = page.getOperation();
    NodeListComposer composer = buildComposer(operation);
    if (null == composer) {
      return;
    }

    for (IResource rsrc : page.getTerms()) {
      IFile rsrcFile =  WorkspaceTools.buildResourceFile(rsrc.getFullPath());
      Collection<GraphNode> nodes = NodeListLoader.loadNodes(rsrcFile);
      composer.combine(nodes);
    }

    editor.setCheckedNodes(composer.getNodes());
  }

  private NodeListComposer buildComposer(CombineOperation operation) {
    if (CombineOperation.UNION.equals(operation)) {
      return new UnionNodeListComposer(
          editor.getCheckedNodes(),
          editor.getGraphModelReference().getGraph().getGraph());
    }
    if (CombineOperation.INTERSECT.equals(operation)) {
      return new IntersectNodeListComposer(editor.getCheckedNodes());
    }
    if (CombineOperation.SUBTRACT.equals(operation)) {
      return new SubtractNodeListComposer(editor.getCheckedNodes());
    }
    return null;
  }

  private static abstract class NodeListComposer {
    protected Map<String, GraphNode> nodeMap;

    public NodeListComposer(Collection<GraphNode> initialNodes) {
      nodeMap = new HashMap<>();
      union(initialNodes);
    }

    private void union(Collection<GraphNode> nodes) {
      for (GraphNode union : nodes) {
        String key = union.getId();
        nodeMap.put(key, union);
      }
    }

    public abstract void combine(Collection<GraphNode> nodes);

    public Collection<GraphNode> getNodes() {
      return nodeMap.values();
    }
  }

  private static class UnionNodeListComposer extends NodeListComposer {

    private final GraphModel graph;

    UnionNodeListComposer(Collection<GraphNode> initialNodes, GraphModel graph) {
      super(initialNodes);
      this.graph = graph;
    }

    public void combine(Collection<GraphNode> nodes) {
      for (GraphNode union : nodes) {
        String key = union.getId();

        // Don't let unknown nodes leak into the collection.
        if (null != graph.findNode(key)) {
          nodeMap.put(key, union);
        }
      }
    }
  }

  private static class IntersectNodeListComposer extends NodeListComposer {

    IntersectNodeListComposer(Collection<GraphNode> initialNodes) {
      super(initialNodes);
    }

    public void combine(Collection<GraphNode> nodes) {
      // Avoid update while scanning
      List<String> keys = new ArrayList<>();
      for (GraphNode base : nodeMap.values()) {
        if (!nodes.contains(base)) {
          keys.add(base.getId());
        }
      }

      for (String key : keys) {
        nodeMap.remove(key);
      }
    }
  }

  private static class SubtractNodeListComposer extends NodeListComposer {

    SubtractNodeListComposer(Collection<GraphNode> initialNodes) {
      super(initialNodes);
    }

    public void combine(Collection<GraphNode> nodes) {
      for (GraphNode remove : nodes) {
        String key = remove.getId();
        nodeMap.remove(key);
      }
    }
  }
}
