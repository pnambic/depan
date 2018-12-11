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

import com.google.devtools.depan.platform.WorkspaceTools;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;

/**
 * A standard page for accepting user input in new document wizard.
 * The top portion allows the user to specify the file name for
 * the analysis output.
 */
public abstract class AbstractNewDocumentPage extends CompositeWizardPage {

  private final ISelection selection;

  private NewDocumentOutputPart outputPart;

  public AbstractNewDocumentPage(
      ISelection selection, String pageLabel, String pageDescription) {
    super(pageLabel);
    this.selection = selection;

    setTitle(pageLabel);
    setDescription(pageDescription);
  }

  @Override
  protected void composeParts(Composite parent) {

    // The output part is required and comes first.
    // ??? Should it come last ??? for better defaults/inferred ???
    outputPart = createOutputPart();
    if (null != outputPart) {
      addOptionPart(parent, outputPart);
    }

    createOptionsParts(parent);
  }

  /////////////////////////////////////
  // Utility methods for derived types

  public IContainer guessContainer() {
    return WorkspaceTools.guessContainer(selection);
  }

  /////////////////////////////////////
  // Public API for container and file location

  public IFile getOutputFile() throws CoreException {
    return outputPart.getOutputFile();
  }

  public String getOutputFilename () {
    return outputPart.getFilename();
  }

  /////////////////////////////////////
  // Hook methods

  /**
  * Hook method for defining the output part.
  * 
  * Most resource pages should {@code @Override} this method to define
  * permissible extensions and containers.
  * 
  * @param containingPage Access to page attributes,
  *     such as the shell for dialog inputs.
  */
  protected NewDocumentOutputPart createOutputPart() {
    return null;
  }

  /**
   * Hook method for defining additional document options.
   */
  protected void createOptionsParts(Composite container) {
  }
}
