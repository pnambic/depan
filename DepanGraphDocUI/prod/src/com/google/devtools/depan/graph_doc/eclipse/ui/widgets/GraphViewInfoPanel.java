/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.graph_doc.eclipse.ui.widgets;

import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class GraphViewInfoPanel extends Composite {

  // private final HierarchyCache<GraphNode> hierarchies;

  /////////////////////////////////////
  // UX Elements

  private Label nameViewer;

  /////////////////////////////////////
  // Public methods

  public GraphViewInfoPanel(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(4));

    @SuppressWarnings("unused")
    Control label = Widgets.buildCompactLabel(this, "Hierarchy from: ");
    nameViewer = Widgets.buildGridLabel(this, "");
  }

  public void setMatcherName(String matcherName) {;
    nameViewer.setText(matcherName);
  }
}
