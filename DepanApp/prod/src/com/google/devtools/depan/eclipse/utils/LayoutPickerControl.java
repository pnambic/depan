/*
 * Copyright 2010 Google Inc.
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

package com.google.devtools.depan.eclipse.utils;

import com.google.devtools.depan.eclipse.visualization.layout.Layouts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A control for selecting a {@link Layouts } (graph layout) option.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class LayoutPickerControl extends Composite {

  private static final String LAYOUT_KEEP = "Keep positions";

  private CCombo layoutChoice;

  private boolean allowKeep;

  /**
   * @param parent Containing composite
   * @param allowKeep use {@code true} if "Keep positions" is a valid choice.
   */
  public LayoutPickerControl(Composite parent, boolean allowKeep) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());

    this.allowKeep = allowKeep;

    layoutChoice = new CCombo(this, SWT.READ_ONLY | SWT.BORDER);

    // Populate the dropdown's choices.
    if (allowKeep) {
      layoutChoice.add(LAYOUT_KEEP);
    }

    for (Layouts l : Layouts.values()) {
      layoutChoice.add(l.toString());
    }
    layoutChoice.select(0);
  }

  /**
   * Set the control to the selected {@code Layouts}.
   * 
   * @param layout selected {@code Layouts} to show in control
   */
  public void setLayoutChoice(Layouts layout) {
    if (allowKeep && (null == layout)) {
      layoutChoice.select(0);
      return;
    }
    if (null == layout) {
      return;
    }
    int base = (allowKeep) ? 1 : 0;
    layoutChoice.select(base + Layouts.indexOf(layout));
  }

  /**
   * Determine the {@code Layouts} instance chosen by the user.
   * 
   * @return a Layouts object, or null if positions should be retained.
   */
  public Layouts getLayoutChoice() {
    if (allowKeep && (layoutChoice.getSelectionIndex() == 0)) {
      return null;
    }
    Layouts l = Layouts.valueOf(
        layoutChoice.getItem(layoutChoice.getSelectionIndex()));
    return l;
  }
}