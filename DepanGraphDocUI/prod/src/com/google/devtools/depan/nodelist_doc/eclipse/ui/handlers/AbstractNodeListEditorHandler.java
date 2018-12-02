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

package com.google.devtools.depan.nodelist_doc.eclipse.ui.handlers;

import com.google.devtools.depan.nodelist_doc.eclipse.ui.editor.NodeListEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Shared framework for command handlers for {@link ViewEditor} commands.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class AbstractNodeListEditorHandler extends AbstractHandler {

  private boolean isEnabled = false;

  protected NodeListEditor getNodeListEditor(ExecutionEvent event) {
    IEditorPart editor = HandlerUtil.getActiveEditor(event);
    if (editor instanceof NodeListEditor) {
      return (NodeListEditor) editor;
    }
    return null;
  }

  @Override
  public boolean isEnabled() {
    return isEnabled;
  }

  @Override
  public void setEnabled(Object evalContext) {
    if (null == evalContext) {
      isEnabled = false;
      return;
    }

    IEvaluationContext context = (IEvaluationContext) evalContext;
    Object editor = context.getVariable(ISources.ACTIVE_EDITOR_NAME);
    if (editor instanceof NodeListEditor) {
      isEnabled = true;
      return;
    }

    isEnabled = false;
    return;
  }
}
