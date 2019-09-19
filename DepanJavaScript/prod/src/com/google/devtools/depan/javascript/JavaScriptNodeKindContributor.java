package com.google.devtools.depan.javascript;

import com.google.devtools.depan.graph.registry.NodeKindContributor;
import com.google.devtools.depan.javascript.graph.JavaScriptElements;
import com.google.devtools.depan.model.Element;

import java.util.Collection;

public class JavaScriptNodeKindContributor implements NodeKindContributor {

  public static final String LABEL = "JavaScript";

  public static final String ID =
      "com.google.devtools.depan.javascript.JavaScriptNodeKindContributor";

  @Override
  public String getLabel() {
    return LABEL;
  }

  @Override
  public Collection<Class<? extends Element>> getNodeKinds() {
    return JavaScriptElements.NODES;
  }
}
