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

package com.google.devtools.depan.nodelist_doc.eclipse.ui.wizards;

import com.google.devtools.depan.platform.ListContentProvider;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Selections;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.platform.eclipse.ui.wizards.AbstractNewDocumentPage;
import com.google.devtools.depan.platform.eclipse.ui.wizards.NewWizardOptionPart;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import java.util.List;

/**
 * Provide UX elements to support node list subtraction.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class UnionOptionPart implements NewWizardOptionPart {

  private final AbstractNewDocumentPage containingPage;

  private NodeListSourcesControl sources;

  /**
   * Show the full path name for resources in the list.
   */
  private static class PartLabelProvider extends LabelProvider {

    @Override
    public String getText(Object element) {
      if (element instanceof IResource) {
        return ((IResource) element).getFullPath().toString();
      }

      return super.getText(element);
    }
  }

  /**
   * Configure a list viewer to show {@link IResource}s.
   */
  private static class NodeListSourcesControl extends ListViewer {

    private ListContentProvider<IResource> listProvider;

    public NodeListSourcesControl(Composite parent) {
      super(parent);
      listProvider = new ListContentProvider<IResource>(this);
      setContentProvider(listProvider);
      IBaseLabelProvider labelProvider = new PartLabelProvider();
      setLabelProvider(labelProvider );
    }

    public void addSource(IResource source) {
      listProvider.add(source);
    }

    public List<IResource> getSources() {
      return listProvider.getObjects();
    }
  }

  /////////////////////////////////////
  // Public API

  public UnionOptionPart(
      AbstractNewDocumentPage containingPage) {
    this.containingPage = containingPage;
  }

  @Override
  public Composite createPartControl(Composite container) {
    Group result = Widgets.buildGridGroup(container, "Terms", 1);

    Composite terms = createTermsControl(result);
    terms.setLayoutData(Widgets.buildGrabFillData());

    return result;
  }

  @Override
  public boolean isComplete() {
    return (null == getErrorMsg());
  }

  @Override
  // TODO: Verify that all files exist.
  public String getErrorMsg() {

    // Empty terms are ok, just not super useful.

    return null;
  }

  /**
   * Provide the terms (subtrahends) for the subtraction.
   */
  public List<IResource> getTerms() {
    return sources.getSources();
  }

  /////////////////////////////////////
  // Terms UX elements

  private Composite createTermsControl(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 3);

    sources = new NodeListSourcesControl(result);
    GridData srcsLayout = Widgets.buildHorzFillData();
    srcsLayout.horizontalSpan = 3;
    sources.getControl().setLayoutData(srcsLayout);

    Button browse = Widgets.buildCompactPushButton(result, "Browse...");
    Button remove = Widgets.buildCompactPushButton(result, "Remove");

    // Install listeners after initial value assignments
    browse.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        handleTermsBrowse();
      }
    });

    remove.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        handleTermsRemove();
      }
    });

    return result;
  }

  /**
   * Add a user selected file to the list of terms.
   */
  private void handleTermsBrowse() {
    IResource result = WorkspaceTools.selectFile(
        containingPage.getShell(), "Add subtrahend term");
    if (null == result) {
      return;
    }

    sources.addSource(result);
    containingPage.updatePageStatus();
    dialogChanged();
  }

  /**
   * Remove all the selected items from the list of terms.
   */
  private void handleTermsRemove() {
    ISelection selection = sources.getSelection();
    for (Object choice : Selections.getObjects(selection)) {
      sources.remove(choice);
    }
    containingPage.updatePageStatus();
    dialogChanged();
  }

  /////////////////////////////////////
  // UX utilities

  private void dialogChanged() {
    containingPage.updatePageStatus();
  }
}
