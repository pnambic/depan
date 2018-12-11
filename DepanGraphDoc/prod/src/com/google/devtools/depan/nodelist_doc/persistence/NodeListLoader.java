/*
 * Copyright 2018 The Depan Project Authors
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
package com.google.devtools.depan.nodelist_doc.persistence;

import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.model.GraphModelReference;
import com.google.devtools.depan.graph_doc.persistence.ResourceCache;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodelist_doc.model.NodeListDocument;
import com.google.devtools.depan.persistence.PersistenceLogger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

import java.util.Collection;
import java.util.Collections;

/**
 * Provide a {@link NodeListGraphRef} descriptor from various sources,
 * including named files.
 */
public class NodeListLoader {

  private NodeListLoader() {
    // Prevent instantiation.
  }

  public static class NodeListGraphRef {
    private final GraphModelReference graphRef;
    private final Collection<GraphNode> nodes;

    public NodeListGraphRef(
        GraphModelReference graphRef, Collection<GraphNode> nodes) {
      this.graphRef = graphRef;
      this.nodes = nodes;
    }

    public GraphModelReference getGraphRef() {
      return graphRef;
    }

    public Collection<GraphNode> getNodes() {
      return nodes;
    }
  }

  public static NodeListGraphRef loadFile(IFile baseFile) {
    String ext = baseFile.getFileExtension();
    if (ext == null) {
      return null;
    }

    // This should be an extension point
    if (GraphDocument.EXTENSION.equals(ext)) {
      GraphDocument baseDoc = ResourceCache.fetchGraphDocument(baseFile);
      GraphModelReference graphRef = new GraphModelReference(baseFile, baseDoc);
      GraphModel graph = graphRef.getGraph().getGraph();
      return new NodeListGraphRef(graphRef, graph.getNodes());
    }
    if (NodeListDocument.EXTENSION.equals(ext)) {
      NodeListDocument nodeListDoc = buildNodeListDoc(baseFile);
      GraphModelReference graphRef = nodeListDoc.getReferenceGraph();
      Collection<GraphNode> nodes = nodeListDoc.getNodes();
      return new NodeListGraphRef(graphRef, nodes);
    }
    return null;
  }

  public static Collection<GraphNode> loadNodes(IFile baseFile) {
    String ext = baseFile.getFileExtension();
    if (ext == null) {
      return Collections.emptyList();
    }

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

  private static NodeListDocument buildNodeListDoc(IFile nodeListDefn) {
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

}
