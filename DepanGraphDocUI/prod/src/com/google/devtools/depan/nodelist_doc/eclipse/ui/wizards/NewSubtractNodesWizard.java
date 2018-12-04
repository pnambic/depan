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

import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.model.GraphModelReference;
import com.google.devtools.depan.graph_doc.operations.SubtractNodes;
import com.google.devtools.depan.graph_doc.persistence.ResourceCache;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodelist_doc.model.NodeListDocument;
import com.google.devtools.depan.nodelist_doc.persistence.NodeListDocXmlPersist;
import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.persistence.PersistenceLogger;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.platform.eclipse.ui.wizards.AbstractNewDocumentWizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * Wizard to create new node lists by subtraction.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NewSubtractNodesWizard
    extends AbstractNewDocumentWizard<NodeListDocument> {

  /**
   * Eclipse extension identifier for this wizard.
   */
  public static final String ANALYSIS_WIZARD_ID =
      "com.google.devtools.depan.nodelist_doc.eclipse.ui.wizards.NewSubtractNodesWizard";

  private NewSubtractNodesPage page;

  /**
   * Adding the page to the wizard.
   */
  @Override
  public void addPages() {
    page = new NewSubtractNodesPage(getSelection());
    addPage(page);
  }

  @Override
  protected String getOutputFilename() {
    return page.getOutputFilename();
  }

  @Override
  protected IFile getOutputFile() throws CoreException {
    return page.getOutputFile();
  }

  @Override
  protected int countCreateWork() {
    return 1 + page.getSubtrahends().size();
  }

  @Override
  protected NodeListDocument createNewDocument(IProgressMonitor monitor)
      throws CoreException, IOException {

    String baseName = page.getMinuend();
    IFile baseFile = WorkspaceTools.buildResourceFile(baseName);
    NodeListSubtractBuilder builder = createNodeListSubtractBuilder(baseFile);
    if (null == builder) {
      return null;
    }

    for (IResource name : page.getSubtrahends()) {
      builder.subtract(getGraphNodes(name));
    }

    return builder.build();
  }

  private static class NodeListSubtractBuilder {
    private final GraphModelReference parentGraph;
    private final SubtractNodes subtract;

    public NodeListSubtractBuilder(
        GraphModelReference parentGraph, SubtractNodes subtract) {
      this.parentGraph = parentGraph;
      this.subtract = subtract;
    }

    public NodeListSubtractBuilder(
        GraphModelReference parentGraph, GraphModel graph) {
      this(parentGraph, new SubtractNodes(graph));
    }

    public NodeListSubtractBuilder(
        GraphModelReference parentGraph, Collection<GraphNode> baseNodes) {
      this(parentGraph, new SubtractNodes(baseNodes));
    }

    public void subtract(Collection<GraphNode> nodes) {
      subtract.subtract(nodes);
    }

    public NodeListDocument build() {
      return new NodeListDocument(parentGraph, subtract.getNodes());
    }
  }

  private NodeListSubtractBuilder createNodeListSubtractBuilder(IFile baseFile) {
    String ext = baseFile.getFileExtension();
    if (ext == null) {
      return null;
    }

    // This should be an extension point
    if (GraphDocument.EXTENSION.equals(ext)) {
      GraphDocument baseDoc = ResourceCache.fetchGraphDocument(baseFile);
      GraphModelReference graphRef = new GraphModelReference(baseFile, baseDoc);
      GraphModel graph = graphRef.getGraph().getGraph();
      return new NodeListSubtractBuilder(graphRef, graph);
    }
    if (NodeListDocument.EXTENSION.equals(ext)) {
      NodeListDocument nodeListDoc = buildNodeListDoc(baseFile);
      GraphModelReference graphRef = nodeListDoc.getReferenceGraph();
      Collection<GraphNode> nodes = nodeListDoc.getNodes();
      return new NodeListSubtractBuilder(graphRef, nodes);
    }
    return null;
  }

  private Collection<GraphNode> getGraphNodes(IResource name) {
    String ext = name.getFileExtension();
    if (ext == null) {
      return Collections.emptyList();
    }

    IFile baseFile = WorkspaceTools.buildResourceFile(name.getFullPath());

    // This should be an extension point
    if (GraphDocument.EXTENSION.equals(ext)) {
      GraphDocument baseDoc = ResourceCache.fetchGraphDocument(baseFile);
      return baseDoc.getGraph().getNodes();
    }
    if (NodeListDocument.EXTENSION.equals(ext)) {
      NodeListDocument nodeListDoc = buildNodeListDoc(baseFile);
      return nodeListDoc.getNodes();
    }

    return Collections.emptyList();
  }

  private NodeListDocument buildNodeListDoc(IFile nodeListDefn) {
    IPath label = nodeListDefn.getProjectRelativePath();
    try {
      PersistenceLogger.LOG.info("Loading NodeList from {}", label);
      NodeListDocXmlPersist loader = NodeListDocXmlPersist.buildForLoad(nodeListDefn);
      return loader.load(nodeListDefn.getLocationURI());
    } catch (RuntimeException err) {
      PersistenceLogger.LOG.error(
          "Unable to load NodeList from {}", label, err);
    }
    return null;
  }

  @Override
  protected int countSaveWork() {
    return 3;
  }

  @Override
  protected void saveNewDocument(
      IProgressMonitor monitor, NodeListDocument doc)
      throws CoreException {

    monitor.setTaskName("Creating document...");
    final IFile file = getOutputFile();
    monitor.worked(1);

    monitor.setTaskName("Saving document...");
    AbstractDocXmlPersist<NodeListDocument> persist =
        NodeListDocXmlPersist.buildForSave();
    persist.saveDocument(file, doc, monitor);
    monitor.worked(1);

    monitor.setTaskName("Refreshing resources ...");
    file.refreshLocal(IResource.DEPTH_ZERO, null);
    monitor.worked(1);
  }
}
