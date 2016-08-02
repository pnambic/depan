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

package com.google.devtools.depan.filesystem;

import com.google.devtools.depan.analysis_doc.model.AnalysisProperties;
import com.google.devtools.depan.edges.matchers.GraphEdgeMatchers;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.AnalysisResourceInstaller;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.models.MatcherResources;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.model.RelationSets;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetResources;
import com.google.devtools.depan.resources.ResourceContainer;

/**
 * Captures many of the capabilities provided by the legacy
 * {@code FileSystemPlugin} mechanism.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class FileSystemAnalysisResourcePlugin implements
    AnalysisResourceInstaller {

  private static final String FILE_SYSTEM_CONTAINER_LABEL =
      "Filesystem Containers";

  public static final RelationSet FILE_SYSTEM_RELSET =
      RelationSets.createArray(FileSystemRelation.values());

  @Override
  public void installResource(ResourceContainer installRoot) {
    installMatchers(MatcherResources.getContainer());
    installRelSets(RelationSetResources.getContainer());
  }

  private void installMatchers(ResourceContainer matchers) {
    GraphEdgeMatcher matcher =
        GraphEdgeMatchers.createForwardEdgeMatcher(FILE_SYSTEM_RELSET);
    GraphEdgeMatcherDescriptor resource = new GraphEdgeMatcherDescriptor(
        FILE_SYSTEM_CONTAINER_LABEL,
        FileSystemPluginActivator.FILE_SYSTEM_MODEL,
        matcher);
    resource.setProperty(
        AnalysisProperties.DEFAULT_PROP, FileSystemRelationContributor.ID);
    matchers.addResource(resource.getName(), resource);
  }

  private void installRelSets(ResourceContainer relSets) {
    RelationSetDescriptor resource = new RelationSetDescriptor(
        FILE_SYSTEM_CONTAINER_LABEL,
        FileSystemPluginActivator.FILE_SYSTEM_MODEL,
        FILE_SYSTEM_RELSET);
    resource.setProperty(
        AnalysisProperties.DEFAULT_PROP, FileSystemRelationContributor.ID);
    relSets.addResource(resource.getName(), resource);
  }
}