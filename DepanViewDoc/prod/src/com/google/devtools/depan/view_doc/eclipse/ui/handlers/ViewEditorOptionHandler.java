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

package com.google.devtools.depan.view_doc.eclipse.ui.handlers;

import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Toggles boolean options for ViewEditor option commands.
 * The command id must match the id for the view option.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ViewEditorOptionHandler extends AbstractViewEditorHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    ViewEditor viewer = getViewEditor(event);

    Command command = event.getCommand();
    String optionId = command.getId();
    boolean value = viewer.isOptionChecked(optionId);
    viewer.setBooleanOption(optionId, !value);
    return null;
  }
}
