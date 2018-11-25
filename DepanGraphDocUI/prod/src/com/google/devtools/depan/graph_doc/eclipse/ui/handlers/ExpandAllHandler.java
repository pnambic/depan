package com.google.devtools.depan.graph_doc.eclipse.ui.handlers;

import com.google.devtools.depan.graph_doc.eclipse.ui.editor.GraphEditor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class ExpandAllHandler extends AbstractGraphEditorHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    GraphEditor editor = getGraphEditor(event);
    editor.handleExpandAll();
    return null;
  }
}
