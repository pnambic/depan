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

import com.google.devtools.depan.nodelist_doc.eclipse.ui.wizards.CombineOperationPart.CombineOperation;
import com.google.devtools.depan.platform.eclipse.ui.wizards.CompositeWizardPage;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.widgets.Composite;

import java.util.List;

/**
 * Provide the UX elements for combining node lists.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class CombineNodeListPage extends CompositeWizardPage {

  private CombineOperationPart combineOperation;

  private CombineNodeListPart combineTerms;

  public CombineNodeListPage() {
    super("Combine node selections");
  }

  @Override // CompositeWizardPage
  public void composeParts(Composite parent) {
    combineOperation = new CombineOperationPart(this);
    addOptionPart(parent, combineOperation);

    combineTerms = new CombineNodeListPart(this);
    addOptionPart(parent, combineTerms);
  }

  public CombineOperation getOperation() {
    return combineOperation.getOperation();
  }

  public List<IResource> getTerms() {
    return combineTerms.getTerms();
  }
}
