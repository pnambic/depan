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

import com.google.devtools.depan.persistence.PersistenceLogger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * A standard framework for implementing a DepAn wizard for a
 * document type.
 */
public abstract class AbstractNewDocumentWizard<T> extends Wizard
    implements INewWizard {

  private ISelection selection;

  /**
   * Create the wizard (with a progress monitor)
   */
  public AbstractNewDocumentWizard() {
    super();
    setNeedsProgressMonitor(true);
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection sel) {
    this.selection = sel;
  }

  /**
   * This method is called when 'Finish' button is pressed in the wizard.
   * The new document generation is performed in a separate thread.
   */
  @Override
  public boolean performFinish() {
    IRunnableWithProgress op = new IRunnableWithProgress() {

      @Override
      public void run(IProgressMonitor monitor)
          throws InvocationTargetException {
        performNewDocument(monitor);
      }
    };

    try {
      getContainer().run(false, false, op);
    } catch (InterruptedException e) {
      return false;
    } catch (InvocationTargetException e) {
      Throwable realException = e.getTargetException();
      MessageDialog.openError(getShell(), "Error", realException.getMessage());
      realException.printStackTrace();
      return false;
    }
    return true;
  }

  protected ISelection getSelection() {
    return selection;
  }

  /**
   * Build the new document based on data in wizard page,
   * and save it to disk.
   * 
   * @param monitor receiver for {@code monitor.worked()} calls
   */
  protected void performNewDocument(IProgressMonitor monitor)
      throws InvocationTargetException {

    String filename = getOutputFilename();
    int work = countSaveWork() + countCreateWork();
    try {
      monitor.beginTask("Creating " + filename, work);
      T doc = createNewDocument(monitor);
      saveNewDocument(monitor, doc);
    } catch (CoreException e) {
      throw new InvocationTargetException(e);
    } catch (IOException errIo) {
      String msg = "Unable to store " + filename;
      PersistenceLogger.LOG.error(msg, errIo);
      throw new InvocationTargetException(errIo, msg);
    } finally {
      monitor.done();
    }
  }

  /**
   * Indicate the number of {@code monitor.worked()} calls that
   * the {@link createNewDocument()} method will generate.
   * 
   * This is a hook method that extending classes are intended to override.
   * 
   * @return count of the number of {@code monitor.worked()} to expect
   */
  protected abstract int countCreateWork();

  /**
   * Using the wizard's internal data, produce an analysis graph
   * that should be saved.
   * 
   * This is a hook method that extending classes are intended to override.
   * 
   * @param monitor receiver for {@code monitor.worked()} calls
   */
  protected abstract T createNewDocument(
      IProgressMonitor monitor)
      throws CoreException, IOException;

  /**
   * Indicate the number of {@code monitor.worked()} calls that
   * the {@link saveNewDocument()} method will generate.
   * 
   * This is a hook method that extending classes are intended to override.
   * 
   * @return count of the number of {@code monitor.worked()} to expect
   */
  protected abstract int countSaveWork();

  /**
   * Save the document generated by
   * {@link #createNewDocument(IProgressMonitor)}.
   * 
   * This is a hook method that extending classes are intended to override.
   * 
   * @param monitor receiver for {@code monitor.worked()} calls
   * @param doc document to write
   */
  protected abstract void saveNewDocument(
      IProgressMonitor monitor, T doc)
      throws CoreException;

  /**
   * Provide the name of the output file to generate.
   * This name is used in error messages.
   */
  protected abstract String getOutputFilename();

  /**
   * Provide the actual {@code IFile} object that should receive the
   * analysis graph.
   * 
   * @return {@code IFile} ready to write to
   */
  protected abstract IFile getOutputFile() throws CoreException;
}
