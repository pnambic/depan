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

import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.operations.MergeGraphDoc;
import com.google.devtools.depan.graph_doc.persistence.GraphModelXmlPersist;
import com.google.devtools.depan.persistence.PersistenceLogger;
import com.google.devtools.depan.platform.eclipse.ui.wizards.AbstractNewDocumentWizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;
import java.net.URI;

/**
 * Wizard to create new node lists by subtraction.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NewUnionNodesWizard
    extends AbstractNewDocumentWizard<GraphDocument> {

  /**
   * Eclipse extension identifier for this wizard.
   */
  public static final String ANALYSIS_WIZARD_ID =
      "com.google.devtools.depan.nodelist_doc.eclipse.ui.wizards.NewUnionNodesWizard";

  private NewUnionNodesPage page;

  /**
   * Adding the page to the wizard.
   */
  @Override
  public void addPages() {
    page = new NewUnionNodesPage(getSelection());
    addPage(page);
  }

  @Override
  protected String getOutputFilename() {
    return page.getOutputFilename();
  }

  @Override
  protected IFile getOutputFile() throws CoreException {
    return page.getOutputFile();
  }

  @Override
  protected int countCreateWork() {
    return page.getTerms().size();
  }

  @Override
  protected GraphDocument createNewDocument(IProgressMonitor monitor)
      throws CoreException, IOException {

    MergeGraphDoc builder = new MergeGraphDoc();
    for (IResource mergeTermTerm : page.getTerms()) {
      GraphDocument mergeDoc = buildGraphDoc(mergeTermTerm.getLocationURI());

      builder.merge(mergeDoc);
    }

    return builder.getGraphDocument();
  }

  /**
   * Provide the {@link GraphDocument} associated
   * with the supplied {@link URI}.
   * 
   * If the URI fails to load as a {@link GraphDocument}, writes a message
   * to the log and returns {@code null}.
   */
  private GraphDocument buildGraphDoc(URI graphUri) {
    try {
      PersistenceLogger.LOG.info("Loading GraphDoc from {}", graphUri);
      GraphModelXmlPersist loader = GraphModelXmlPersist.build(true);
      return loader.load(graphUri);
    } catch (RuntimeException err) {
      PersistenceLogger.LOG.error(
          "Unable to load GraphDoc from {}", graphUri, err);
    }
    return null;
  }

  @Override
  protected int countSaveWork() {
    return 3;
  }

  @Override
  protected void saveNewDocument(
      IProgressMonitor monitor, GraphDocument doc)
      throws CoreException {

    monitor.setTaskName("Creating document...");
    final IFile file = getOutputFile();
    monitor.worked(1);

    monitor.setTaskName("Saving document...");
    GraphModelXmlPersist persist = GraphModelXmlPersist.build(false);
        // GraphDocument.buildForSave();
    persist.saveDocument(file, doc, monitor);
    monitor.worked(1);

    monitor.setTaskName("Refreshing resources ...");
    file.refreshLocal(IResource.DEPTH_ZERO, null);
    monitor.worked(1);
  }
}
