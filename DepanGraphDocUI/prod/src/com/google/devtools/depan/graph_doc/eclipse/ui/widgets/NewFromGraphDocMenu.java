package com.google.devtools.depan.graph_doc.eclipse.ui.widgets;

import com.google.devtools.depan.graph_doc.eclipse.ui.editor.GraphEditor;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocContributor;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocRegistry;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocWizard;
import com.google.devtools.depan.model.GraphNode;

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

public class NewFromGraphDocMenu extends ContributionItem
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
      fillFromGraphDocContributor(menu, (GraphEditor) editor);
      return;
    }
  }

  private void fillFromGraphDocContributor(
      Menu parent, final GraphEditor editor) {
    MenuItem newItem = new MenuItem(parent, SWT.CASCADE);
    newItem.setText("New Analysis...");

    Collection<GraphNode> nodes = editor.getSelectedNodes();
    GraphNode topNode = nodes.isEmpty() ? null : nodes.iterator().next();
    WizardDispatch selectHandler = new WizardDispatch(editor, topNode, nodes);

    Menu newMenu = new Menu(newItem);
    for (Entry<String, FromGraphDocContributor> entry
        : FromGraphDocRegistry.getRegistryContributionMap().entrySet()) {

      fillContribItem(newMenu, entry.getValue(), selectHandler);
    }

    newItem.setMenu(newMenu);
    newItem.setEnabled(!nodes.isEmpty() && newMenu.getItemCount() > 0);
  }

  private void fillContribItem(Menu parent,
      final FromGraphDocContributor contrib, final WizardDispatch dispatch) {
 
    MenuItem item = new MenuItem(parent, SWT.NONE);
    item.setText(contrib.getLabel());
    item.setEnabled(true);

    item.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        FromGraphDocWizard wizard = contrib.newWizard();
        dispatch.dispatch(wizard);
      }
    });
  }

  private static class WizardDispatch extends SelectionAdapter {
    final GraphEditor editor;
    final GraphNode topNode;
    final Collection<GraphNode> nodes;

    public WizardDispatch(GraphEditor editor,
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
