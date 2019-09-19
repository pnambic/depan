package com.google.devtools.depan.maven;

import com.google.devtools.depan.graph.registry.NodeKindContributor;
import com.google.devtools.depan.maven.graph.MavenElements;
import com.google.devtools.depan.model.Element;

import java.util.Collection;

public class MavenNodeKindContributor implements NodeKindContributor {

  public static final String LABEL = "Maven";

  public static final String ID =
      "com.google.devtools.depan.maven.MavenNodeKindContributor";

  @Override
  public String getLabel() {
    return LABEL;
  }

  @Override
  public Collection<Class<? extends Element>> getNodeKinds() {
    return MavenElements.NODES;
  }
}
