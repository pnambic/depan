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
package com.google.devtools.depan.nodelist_doc.eclipse.ui.widgets;

import com.google.devtools.depan.nodelist_doc.eclipse.ui.editor.NodeListEditor;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

public class SelectRecursiveMenu extends ContributionItem
    implements IWorkbenchContribution {

  private IServiceLocator serviceLocator;

  @Override
  public void initialize(IServiceLocator serviceLocator) {
    this.serviceLocator = serviceLocator;
  }

  @Override
  public boolean isDynamic() {
    return true;
  }

  @Override
  public void fill(Menu menu, int index) {
    IEditorPart editor = getEditor();

    if (editor instanceof NodeListEditor) {
      final NodeListEditor nodeListEditor = (NodeListEditor) editor;
      boolean recursiveSelect = nodeListEditor.getRecursiveSelect();
      MenuItem item = new MenuItem(menu, SWT.CHECK);
      item.setText("Select Recursive");
      item.setEnabled(true);
      item.setSelection(recursiveSelect);
      item.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
          nodeListEditor.handleSelectRecursive();
        }
      });
      return;
    }
  }

  private IEditorPart getEditor() {
    IPageService pageService = serviceLocator.getService(IPageService.class);
    if (null == pageService) {
      return null;
    }
    IWorkbenchPage page = pageService.getActivePage();
    if (null == page) {
      return null;
    }
    return page.getActiveEditor();
  }
}
