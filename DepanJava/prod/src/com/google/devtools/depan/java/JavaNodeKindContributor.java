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

import com.google.devtools.depan.graph.registry.NodeKindContributor;
import com.google.devtools.depan.java.graph.JavaElements;
import com.google.devtools.depan.model.Element;

import java.util.Collection;

public class JavaNodeKindContributor implements NodeKindContributor {

  public static final String LABEL = "Java";

  public static final String ID =
      "com.google.devtools.depan.java.JavaNodeKindContributor";

  @Override
  public String getLabel() {
    return LABEL;
  }

  @Override
  public Collection<Class<? extends Element>> getNodeKinds() {
    return JavaElements.NODES;
  }
}
