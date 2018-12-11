/*
 * Copyright 2018 The Depan Project Authors
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

package com.google.devtools.depan.nodelist_doc.eclipse.ui.wizards;

import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.platform.eclipse.ui.wizards.CompositeWizardPage;
import com.google.devtools.depan.platform.eclipse.ui.wizards.NewWizardOptionPart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * Provide UX elements to support node list subtraction.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class CombineOperationPart implements NewWizardOptionPart {

  public enum CombineOperation {
    UNION("union"),
    INTERSECT("intersect"),
    SUBTRACT("subtract");

    private final String label;

    private CombineOperation(String label) {
      this.label = label;
    }

    public String getLabel() {
      return label;
    }
  }

  @SuppressWarnings("unused")
  private final CompositeWizardPage containingPage;

  private Combo operation;

  /////////////////////////////////////
  // Public API

  public CombineOperationPart(CompositeWizardPage containingPage) {
    this.containingPage = containingPage;
  }

  @Override
  public Composite createPartControl(Composite container) {
    Composite result = Widgets.buildGridGroup(container, "Operation", 1);

    operation = createOperationCombo(result);
    operation.setLayoutData(Widgets.buildHorzFillData());

    return result;
  }

  @Override
  public boolean isComplete() {
    return (null == getErrorMsg());
  }

  @Override
  // Never a problem
  public String getErrorMsg() {
    return null;
  }

  public CombineOperation getOperation() {
    return extractOperation();
  }

  /////////////////////////////////////
  // Base (minuend) UX elements

  private Combo createOperationCombo(Composite parent) {
    Combo result = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
    result.add(CombineOperation.UNION.getLabel());
    result.add(CombineOperation.INTERSECT.getLabel());
    result.add(CombineOperation.SUBTRACT.getLabel());
    result.select(0);
    return result;
  }

  private CombineOperation extractOperation() {
    int index = operation.getSelectionIndex();
    if (index < 0) {
      return null;
    }
    String choice = operation.getItem(index);
    if (CombineOperation.UNION.label.equals(choice)) {
      return CombineOperation.UNION;
    }
    if (CombineOperation.INTERSECT.label.equals(choice)) {
      return CombineOperation.INTERSECT;
    }
    if (CombineOperation.SUBTRACT.label.equals(choice)) {
      return CombineOperation.SUBTRACT;
    }

    // Nothing known
    return null ;
  }
}
