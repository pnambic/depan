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
package com.google.devtools.depan.java;

import com.google.devtools.depan.graph.registry.NodeTypeContributor;
import com.google.devtools.depan.java.graph.FieldElement;
import com.google.devtools.depan.java.graph.InterfaceElement;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.java.graph.TypeElement;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Lists;

import java.util.Collection;

public class JavaNodeTypeContributor implements NodeTypeContributor {

  public static final String LABEL = "Java";

  public static final String ID =
      "com.google.devtools.depan.java.JavaNodeTypeContributor";

  private static final Collection<Class<? extends GraphNode>> NODE_TYPES =
      buildNodeTypes();

  @Override
  public String getLabel() {
    return LABEL;
  }

  @Override
  public Collection<Class<? extends GraphNode>> getNodeTypes() {
    return NODE_TYPES;
  }

  private static Collection<Class<? extends GraphNode>> buildNodeTypes() {
    Collection<Class<? extends GraphNode>> result = Lists.newArrayList();
    result.add(FieldElement.class);
    result.add(InterfaceElement.class);
    result.add(MethodElement.class);
    result.add(PackageElement.class);
    result.add(TypeElement.class);
    return result;
  }
}
