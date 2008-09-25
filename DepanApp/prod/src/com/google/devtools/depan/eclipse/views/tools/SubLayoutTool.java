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

import com.google.devtools.depan.eclipse.utils.DefaultRelationshipSet;
import com.google.devtools.depan.eclipse.utils.RelationshipSetSelector;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.views.ViewEditorTool;
import com.google.devtools.depan.eclipse.visualization.layout.Layouts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class SubLayoutTool extends ViewEditorTool {

  /**
   * Drop down List of available layouts.
   */
  private CCombo layoutChoice = null;

  /**
   * Selector for named relationships sets.
   */
  private RelationshipSetSelector relationshipSetselector = null;

  @Override
  public Image getIcon() {
    return Resources.IMAGE_SUBLAYOUT;
  }

  @Override
  public String getName() {
    return Resources.NAME_SUBLAYOUT;
  }

  @Override
  public Control setupComposite(Composite parent) {
    Composite baseComposite = new Composite(parent, SWT.NONE);

    // components
    new Label(baseComposite, SWT.NONE).setText("Sub layout : ");
    layoutChoice = new CCombo(baseComposite, SWT.READ_ONLY | SWT.BORDER);
    Label selectLabel = new Label(baseComposite, SWT.NONE);
    relationshipSetselector = new RelationshipSetSelector(baseComposite);
    Button apply = new Button(baseComposite, SWT.PUSH);
    Control selector = relationshipSetselector.getControl();
    Label help = new Label(baseComposite, SWT.WRAP);

    selectLabel.setText("Relationship set");

    help.setText("The relationship set is used only in layouts requiring "
        + "a hierarchy. Basically Tree layouts.\n\n"
        + ""
        + "If \"Set size to\" is not selected, the size used is the bounding "
        + "box of all selected nodes. So if your nodes are in line, you will "
        + "most likely get all your nodes at the same position.\n\n"
        + ""
        + "If there are no selected nodes, apply the layout / [default]size "
        + "to the entire graph.");

    // layout (SWT layouts...)
    GridLayout grid = new GridLayout(2, false);
    baseComposite.setLayout(grid);
    layoutChoice.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
    apply.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
    help.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
    selector.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

    // View's layouts:
    for (Layouts l : Layouts.values()) {
      layoutChoice.add(l.toString());
    }
    layoutChoice.select(0);

    apply.setText("Apply");
    apply.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        apply();
      }
    });

    initSelector();

    return baseComposite;
  }

  /**
   * Initialize the relationship set selector with the
   * {@link BuiltinRelationshipSets#CONTAINER} set.
   */
  private void initSelector() {
    relationshipSetselector.selectSet(DefaultRelationshipSet.SET);
  }

  protected void apply() {
    if (!hasEditor()) {
      return;
    }

    try {
      Layouts layout = Layouts.valueOf(
          layoutChoice.getItem(layoutChoice.getSelectionIndex()));
      getEditor().clusterize(layout, relationshipSetselector.getSelection());
    } catch (IllegalArgumentException ex) {
      // bad layout. don't do anything for the layout, but still finish the
      // creation of the view.
      System.err.println("Bad layout.");
    }
  }

}