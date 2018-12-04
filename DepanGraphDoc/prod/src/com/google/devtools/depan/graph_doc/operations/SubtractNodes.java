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

package com.google.devtools.depan.graph_doc.operations;

import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Although {@link GraphNode}s don't have an {@code equals()} method,
 * two nodes with the same {@link GraphNode#getId()} are considered
 * the same object.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class SubtractNodes {

  private Map<String, GraphNode> nodeMap;

  public SubtractNodes(GraphModel graph) {
    nodeMap = graph.getNodesMap();
  }

  public SubtractNodes(Collection<GraphNode> nodes) {
    nodeMap = new HashMap<>(nodes.size());
    for (GraphNode node : nodes) {
      nodeMap.put(node.getId(), node);
    }
  }

  public void subtract(Collection<GraphNode> nodes) {
    for (GraphNode remove : nodes) {
      String key = remove.getId();
      if (nodeMap.containsKey(key)) {
        nodeMap.remove(key);
      }
    }
  }

  public Collection<GraphNode> getNodes() {
    return nodeMap.values();
  }
}
