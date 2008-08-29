/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.views.tools;

import com.google.devtools.depan.eclipse.utils.DoubleElementEditorChooser;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.views.ViewSelectionListenerTool;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A refactoring tool. For now, only shows a {@link DoubleElementEditorChooser}
 * associated with the first selected Element.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class RefactorTool extends ViewSelectionListenerTool {

  private DoubleElementEditorChooser chooser;

  @Override
  public void emptySelection() {
    chooser.setNoEditor();
  }

  protected void setNode(GraphNode node) {
    chooser.setEditorFor(node);
  }

  @Override
  public Image getIcon() {
    return Resources.IMAGE_REFACTORING;
  }

  @Override
  public String getName() {
    return Resources.NAME_REFACTORING;
  }

  @Override
  public Control setupComposite(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);

    chooser = new DoubleElementEditorChooser(composite, SWT.NONE);

    // layout
    GridLayout layout = new GridLayout(2, false);
    composite.setLayout(layout);

    chooser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    return composite;
  }

  @Override
  public void updateSelectedAdd(GraphNode[] selection) {
    if (selection.length > 0) {
      setNode(selection[0]);
    } else {
      emptySelection();
    }
  }

  @Override
  public void updateSelectedRemove(GraphNode[] selection) {
    emptySelection();
  }

  @Override
  public void updateSelectionTo(GraphNode[] selection) {
    emptySelection();
    updateSelectedAdd(selection);
  }

}
