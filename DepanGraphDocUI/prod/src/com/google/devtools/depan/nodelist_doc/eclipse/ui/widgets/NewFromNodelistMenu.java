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

import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocContributor;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocRegistry;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocWizard;
import com.google.devtools.depan.model.GraphNode;
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

import java.util.Collection;
import java.util.Map.Entry;

public class NewFromNodelistMenu extends ContributionItem
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
      fillFromGraphDocContributor(menu, (NodeListEditor) editor);
      return;
    }
  }

  private void fillFromGraphDocContributor(
      Menu parent, final NodeListEditor editor) {
    MenuItem newItem = new MenuItem(parent, SWT.CASCADE);
    newItem.setText("New Analysis...");

    Collection<GraphNode> nodes = editor.getSelectedNodes();
    GraphNode topNode = nodes.isEmpty() ? null : nodes.iterator().next();
    WizardContext context = new WizardContext(editor, topNode, nodes);

    Menu newMenu = new Menu(newItem);
    for (Entry<String, FromGraphDocContributor> entry
        : FromGraphDocRegistry.getRegistryContributionMap().entrySet()) {

      fillContribItem(newMenu, entry.getValue(), context);
    }

    newItem.setMenu(newMenu);
    newItem.setEnabled(!nodes.isEmpty() && newMenu.getItemCount() > 0);
  }

  private void fillContribItem(Menu parent,
      final FromGraphDocContributor contrib, final WizardContext context) {
 
    MenuItem item = new MenuItem(parent, SWT.NONE);
    item.setText(contrib.getLabel());
    item.setEnabled(true);

    item.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        FromGraphDocWizard wizard = contrib.newWizard();
        context.dispatch(wizard);
      }
    });
  }

  private static class WizardContext extends SelectionAdapter {
    final NodeListEditor editor;
    final GraphNode topNode;
    final Collection<GraphNode> nodes;

    public WizardContext(NodeListEditor editor,
        GraphNode topNode, Collection<GraphNode> nodes) {
      this.editor = editor;
      this.topNode = topNode;
      this.nodes = nodes;
    }

    public void dispatch(FromGraphDocWizard wizard) {
      editor.runFromGraphDocWizard(wizard, topNode, nodes);
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
