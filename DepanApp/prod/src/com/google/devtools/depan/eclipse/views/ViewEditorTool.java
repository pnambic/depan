/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.eclipse.views;

import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.visualization.View;
import com.google.devtools.depan.view.ViewModel;

/**
 * Provide an abstract Tool implementation for tools that operate
 * on ViewEditor contents.
 * <p>
 * Most tools operate on the GraphModel provided by a ViewEditor.
 * Only those nodes and edges are available for the tool.
 *
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public abstract class ViewEditorTool implements Tool {

  /** Editor being edited */
  private ViewEditor editor = null;

  public ViewEditor getEditor() {
    return editor;
  }

  protected boolean hasEditor() {
    return (null != getEditor());
  }

  public ViewModel getViewModel() {
    if (hasEditor()) {
      return getEditor().getViewModel();
    } else {
      return null;
    }
  }

  public View getView() {
    if (hasEditor()) {
      return getEditor().getView();
    } else {
      return null;
    }
  }

  public void selected(boolean isSelected) {
    // nothing to do here.
  }

  protected boolean hasView() {
    if (!hasEditor()) {
      return false;
    }
    if (null == getView()) {
      return false;
    }
    return true;
  }

  /**
   * Acquire any necessary resources, such as Listeners.
   * Extending classes are expected to override this as needed.
   */
  protected void acquireResources() {
  }

  /**
   * Release any acquired resources, such as Listeners.
   * Extending classes are expected to override this as needed.
   */
  protected void releaseResources() {
  }

  /**
   * Clear any controls, such as selections.
   * Extending classes are expected to override this as needed.
   */
  protected void clearControls() {
  }

  /**
   * Update the contents of controls, such as selections.
   * Extending classes are expected to override this as needed.
   */
  protected void updateControls() {
  }

  /**
   * {@inheritDoc}
   * <p>
   * In this implementation, all resources (e.g. Listeners) are released
   * when the editor is closed and any selection is emptied.
   */
  public void editorClosed(ViewEditor viewEditor) {
    if (getEditor() != viewEditor) {
      return;
    }

    releaseResources();
    clearControls();
    this.editor = null;
  }

  /**
   * {@inheritDoc}
   * <p>
   * In this implementation, any previous editor is closed, releasing
   * any resources (e.g. Listeners) it was using.
   * The new editor then acquires it own resources and updates its
   * controls as necessary.
   */
  public void setEditor(ViewEditor viewEditor) {
    if (getEditor() == viewEditor) {
      return;
    }
    if (hasEditor()) {
      editorClosed(getEditor());
    }
    this.editor = viewEditor;

    acquireResources();

    clearControls();  // Is this necessary? Also happens in editorClosed().
    updateControls();
  }
}