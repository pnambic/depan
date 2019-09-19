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

import com.google.devtools.depan.model.Element;
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
public class NodeKindRegistry extends
    ContributionRegistry<NodeKindContributor> {

  /**
   * Extension point name for persistence configuration
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.graph.registry.nodeKind";

  /**
   * Static instance. This class is a singleton.
   * It is created lazily on first access.
   */
  private static NodeKindRegistry INSTANCE = null;

  static class Entry extends ContributionEntry<NodeKindContributor>{

    public Entry(String bundleId, IConfigurationElement element) {
      super(bundleId, element);
    }

    @Override
    protected NodeKindContributor createInstance() throws CoreException {
      return (NodeKindContributor) buildInstance(ATTR_CLASS);
    }
  }

  private Map<Class<? extends Element>, NodeKindContributor> nodeKindToContrib =
      Maps.newHashMap();

  /**
   * Singleton class: private constructor to prevent instantiation.
   */
  private NodeKindRegistry() {
  }

  @Override
  protected ContributionEntry<NodeKindContributor> buildEntry(
      String bundleId, IConfigurationElement element) {
    return new Entry(bundleId, element);
  }

  @Override
  protected void reportException(String entryId, Exception err) {
    PlatformLogger.LOG.error(
        "NodeKind registry load failure for {}", entryId, err);
  }

  @Override
  protected void load(String extensionId) {
    super.load(extensionId);
    deriveDetails();
  }

  private void deriveDetails() {
    // Build reverse map from relation to contributor
    for (ContributionEntry<NodeKindContributor> entry : getContributions()) {
      NodeKindContributor contrib = entry.getInstance();
      for (Class<? extends Element> nodeKind : contrib.getNodeKinds()) {
        nodeKindToContrib.put(nodeKind, contrib);
      }
    }
  }

  /////////////////////////////////////
  // Project Relations

  public Collection<Class<? extends Element>> getNodeKinds() {
    return buildNodeKinds(getContributions());
  }

  public Collection<Class<? extends Element>> getNodeKinds(Collection<String> contribIds) {
    return buildNodeKinds(getContributions(contribIds));
  }

  public List<String> getContribIds() {
    List<String> result = Lists.newArrayList();
    for (ContributionEntry<NodeKindContributor> contrib : getContributions()) {
      result.add(contrib.getId());
    }
    return result;
  }

  private Collection<Class<? extends Element>> buildNodeKinds(
      Collection<ContributionEntry<NodeKindContributor>> contrib) {

    List<Class<? extends Element>> result = Lists.newArrayList();
    for (ContributionEntry<NodeKindContributor> entry : contrib) {
      result.addAll(entry.getInstance().getNodeKinds());
    }
    return result;
  }

  private String getNodeKindSource(Class<? extends Element> nodeKind) {
    NodeKindContributor result = nodeKindToContrib.get(nodeKind);
    if (null != result) {
      return result.getLabel();
    }

    String msg = MessageFormat.format(
        "- unsrcd {0} -", nodeKind.getTypeName());
    return msg;
  }

  /////////////////////////////////////
  // Singleton access methods

  /**
   * Provide the {@code SourcePluginRegistry} singleton.
   * It is created lazily when needed.
   */
  public static synchronized NodeKindRegistry getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new NodeKindRegistry();
      INSTANCE.load(EXTENTION_POINT);
    }
    return INSTANCE;
  }

  public static Collection<Class<? extends Element>> getRegistryNodeKinds() {
    return getInstance().getNodeKinds();
  }

  public static Collection<Class<? extends Element>> getRegistryNodeKinds(
      Collection<String> contribIds) {
    return getInstance().getNodeKinds(contribIds);
  }

  public static String getRegistryNodeKindSource(
      Class<? extends Element> nodeKind) {
    return getInstance().getNodeKindSource(nodeKind);
  }

  public static List<String> getRegistryContribIds() {
    return getInstance().getContribIds();
  }
}
