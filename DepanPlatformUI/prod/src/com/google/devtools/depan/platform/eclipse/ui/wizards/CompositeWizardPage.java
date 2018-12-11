/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.platform.eclipse.ui.wizards;

import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import com.google.common.collect.Lists;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import java.util.List;

/**
 * A standard wizard page for composed of "parts" that each validate some
 * aspect of the wizard's data.
 */
public abstract class CompositeWizardPage extends WizardPage {

  private List<NewWizardOptionPart> wizardParts = Lists.newArrayList();

  public CompositeWizardPage(String pageLabel) {
    super(pageLabel);

    setTitle(pageLabel);
  }

  @Override
  public boolean isPageComplete() {
    for (NewWizardOptionPart part : wizardParts) {
      boolean result = part.isComplete();
      // Early exit fail is this part is not complete.
      if (!result) {
        return result;
      }
    }
    return true;
  }

  @Override
  public void createControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);

    GridLayout layout = new GridLayout(1, true);
    layout.marginWidth = 0;
    layout.verticalSpacing = 9;
    container.setLayout(layout);

    composeParts(container);

    updateStatus(getPageErrorMsg());
    setControl(container);
  }

  /**
   * Utility method for derived classes to adding input part for the wizard.
   * 
   * Set's the new control's {@link LayoutData} to consume the full width.
   */
  protected void addOptionPart(
      Composite container, NewWizardOptionPart part) {
    wizardParts.add(part);
    Composite widget = part.createPartControl(container);
    widget.setLayoutData(Widgets.buildHorzFillData());
  }

  /////////////////////////////////////
  // Error management methods

  /**
   * Derived classes should extend this with any tests required for their
   * document included in the overriding implementation.
   */
  private String getPageErrorMsg() {
    for (NewWizardOptionPart part : wizardParts) {
      String result = part.getErrorMsg();
      if (null != result) {
        return result;
      }
    }
    return null;
  }

  private void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(isPageComplete());
  }

  /**
   * Callback for child parts to update page status.
   * Could be listener driven.
   */
  public void updatePageStatus() {
    updateStatus(getPageErrorMsg());
  }

  /////////////////////////////////////
  // Hook methods

  /**
   * Hook method for defining additional document options.
   */
  protected void composeParts(Composite container) {
  }
}
