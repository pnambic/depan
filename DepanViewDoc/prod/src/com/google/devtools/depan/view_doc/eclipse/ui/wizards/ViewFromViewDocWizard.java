/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.eclipse.ui.wizards;

import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditorInput;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.FromViewDocWizard;
import com.google.devtools.depan.view_doc.layout.LayoutGenerator;
import com.google.devtools.depan.view_doc.layout.grid.GridLayoutGenerator;
import com.google.devtools.depan.view_doc.model.ViewDocument;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ViewFromViewDocWizard extends FromViewDocWizard {

  private ViewFromGraphDocPage page;

  @Override
  public void addPages() {
    page = new ViewFromGraphDocPage(getGraphResources());
    addPage(page);
  }

  @Override
  public boolean performFinish() {
    ViewEditorInput viewInput = buildViewInput();
    ViewEditor.startViewEditor(viewInput);
    return true;
  }

  /**
   * Unpack wizard page controls into a {@link ViewEditorInput}.
   */
  private ViewEditorInput buildViewInput() {
    String basename = calcName();

    ViewDocument viewInfo = buildNewViewDocument();

    ViewEditorInput result = new ViewEditorInput(viewInfo, basename);
    result.setInitialLayout(calcInitialLayout());

    return result;
  }

  private LayoutGenerator calcInitialLayout() {
    LayoutGenerator layout = page.getLayoutGenerator();
    if (null != layout) {
      return layout;
    }
    return new GridLayoutGenerator();
  }
}
