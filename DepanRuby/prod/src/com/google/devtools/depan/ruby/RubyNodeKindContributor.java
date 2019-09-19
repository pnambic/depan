package com.google.devtools.depan.ruby;

import com.google.devtools.depan.graph.registry.NodeKindContributor;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.ruby.graph.RubyElements;

import java.util.Collection;

public class RubyNodeKindContributor implements NodeKindContributor {

  public static final String LABEL = "Ruby";

  public static final String ID =
      "com.google.devtools.depan.ruby.RubyNodeKindContributor";

  @Override
  public String getLabel() {
    return LABEL;
  }

  @Override
  public Collection<Class<? extends Element>> getNodeKinds() {
    return RubyElements.NODES;
  }
}
