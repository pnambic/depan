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
import com.google.devtools.depan.nodelist_doc.model.NodeListDocument;
import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.platform.eclipse.ui.wizards.AbstractNewDocumentOutputPart;
import com.google.devtools.depan.platform.eclipse.ui.wizards.AbstractNewDocumentPage;
import com.google.devtools.depan.platform.eclipse.ui.wizards.NewDocumentOutputPart;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;

import java.util.List;

/**
 * Provide the UX elements for subtracting node lists.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NewUnionNodesPage extends AbstractNewDocumentPage {

  public static final String DEFAULT_FILENAME =
      "union." + GraphDocument.EXTENSION;

  private UnionOptionPart unionOptions;

  public NewUnionNodesPage(ISelection selection) {
    super(selection,
        "Union Graph Info", 
        "Create new graph info by union");
  }

  @Override
  protected NewDocumentOutputPart createOutputPart() {
    IContainer outputContainer = guessContainer();
    String outputFilename = PlatformTools.guessNewFilename(
        outputContainer, DEFAULT_FILENAME, 1, 10);

    return new AbstractNewDocumentOutputPart(
        "Node List Union", this, outputContainer,
        NodeListDocument.EXTENSION, outputFilename);
  }

  @Override
  protected void createOptionsParts(Composite container) {
    unionOptions = new UnionOptionPart(this);
    addOptionPart(container, unionOptions);
  }

  public List<IResource> getTerms() {
    return unionOptions.getTerms();
  }
}
