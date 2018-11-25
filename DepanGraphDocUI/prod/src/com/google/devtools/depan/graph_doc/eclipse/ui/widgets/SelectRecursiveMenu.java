package com.google.devtools.depan.graph_doc.eclipse.ui.widgets;

import com.google.devtools.depan.graph_doc.eclipse.ui.editor.GraphEditor;

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

    if (editor instanceof GraphEditor) {
      final GraphEditor graphEditor = (GraphEditor) editor;
      boolean recursiveSelect = graphEditor.getRecursiveSelect();
      MenuItem item = new MenuItem(menu, SWT.CHECK);
      item.setText("Select Recursive");
      item.setEnabled(true);
      item.setSelection(recursiveSelect);
      item.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
          graphEditor.handleSelectRecursive();
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
