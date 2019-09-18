/*
 * Copyright 2019 The Depan Project Authors
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

package com.google.devtools.depan.graph.registry;

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.platform.PlatformLogger;
import com.google.devtools.depan.platform.plugin.ContributionEntry;
import com.google.devtools.depan.platform.plugin.ContributionRegistry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Support common plugin pattern with multiple contributions to a single
 * extension point.
 * 
 * Extension points based on this class should reside in an {@code .plugins}
 * directory for their supporting system. (The {@code .registry} package
 * suffix is reserved for registry support.)
 * 
 * @author Lee Carver
 */
public class NodeTypeRegistry extends
    ContributionRegistry<NodeTypeContributor> {

  /**
   * Extension point name for persistence configuration
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.graph.registry.nodeType";

  /**
   * Static instance. This class is a singleton.
   * It is created lazily on first access.
   */
  private static NodeTypeRegistry INSTANCE = null;

  static class Entry extends ContributionEntry<NodeTypeContributor>{

    public Entry(String bundleId, IConfigurationElement element) {
      super(bundleId, element);
    }

    @Override
    protected NodeTypeContributor createInstance() throws CoreException {
      return (NodeTypeContributor) buildInstance(ATTR_CLASS);
    }
  }

  private Map<Class<? extends GraphNode>, NodeTypeContributor> nodeTypeToContrib =
      Maps.newHashMap();

  /**
   * Singleton class: private constructor to prevent instantiation.
   */
  private NodeTypeRegistry() {
  }

  @Override
  protected ContributionEntry<NodeTypeContributor> buildEntry(
      String bundleId, IConfigurationElement element) {
    return new Entry(bundleId, element);
  }

  @Override
  protected void reportException(String entryId, Exception err) {
    PlatformLogger.LOG.error(
        "NodeType registry load failure for {}", entryId, err);
  }

  @Override
  protected void load(String extensionId) {
    super.load(extensionId);
    deriveDetails();
  }

  private void deriveDetails() {
    // Build reverse map from relation to contributor
    for (ContributionEntry<NodeTypeContributor> entry : getContributions()) {
      NodeTypeContributor contrib = entry.getInstance();
      for (Class<? extends GraphNode> nodeTypes : contrib.getNodeTypes()) {
        nodeTypeToContrib.put(nodeTypes, contrib);
      }
    }
  }

  /////////////////////////////////////
  // Project Relations

  public Collection<Class<? extends GraphNode>> getNodeTypes() {
    return buildNodeTypes(getContributions());
  }

  public Collection<Class<? extends GraphNode>> getNodeTypes(Collection<String> contribIds) {
    return buildNodeTypes(getContributions(contribIds));
  }

  public List<String> getContribIds() {
    List<String> result = Lists.newArrayList();
    for (ContributionEntry<NodeTypeContributor> contrib : getContributions()) {
      result.add(contrib.getId());
    }
    return result;
  }

  private Collection<Class<? extends GraphNode>> buildNodeTypes(
      Collection<ContributionEntry<NodeTypeContributor>> contrib) {

    List<Class<? extends GraphNode>> result = Lists.newArrayList();
    for (ContributionEntry<NodeTypeContributor> entry : contrib) {
      result.addAll(entry.getInstance().getNodeTypes());
    }
    return result;
  }

  private String getNodeTypeSource(Class<? extends GraphNode> nodeType) {
    NodeTypeContributor result = nodeTypeToContrib.get(nodeType);
    if (null != result) {
      return result.getLabel();
    }

    String msg = MessageFormat.format(
        "- unsrcd {0} -", nodeType.getTypeName());
    return msg;
  }

  /////////////////////////////////////
  // Singleton access methods

  /**
   * Provide the {@code SourcePluginRegistry} singleton.
   * It is created lazily when needed.
   */
  public static synchronized NodeTypeRegistry getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new NodeTypeRegistry();
      INSTANCE.load(EXTENTION_POINT);
    }
    return INSTANCE;
  }

  public static Collection<Class<? extends GraphNode>> getRegistryNodeTypes() {
    return getInstance().getNodeTypes();
  }

  public static Collection<Class<? extends GraphNode>> getRegistryNodeTypes(
      Collection<String> contribIds) {
    return getInstance().getNodeTypes(contribIds);
  }

  public static String getRegistryNodeTypeSource(
      Class<? extends GraphNode> nodeType) {
    return getInstance().getNodeTypeSource(nodeType);
  }

  public static List<String> getRegistryContribIds() {
    return getInstance().getContribIds();
  }
}
