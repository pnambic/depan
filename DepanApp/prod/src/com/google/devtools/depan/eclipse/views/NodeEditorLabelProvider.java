/*
 * Copyright 2008 Google Inc.
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

package com.google.devtools.depan.eclipse.views;

import com.google.devtools.depan.eclipse.trees.NodeTreeView.NodeWrapper;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.Tools;
import com.google.devtools.depan.eclipse.views.tools.NodeEditorTool;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view.NodeDisplayProperty;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class NodeEditorLabelProvider extends LabelProvider implements
    ITableLabelProvider {

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITableLabelProvider
   *      #getColumnImage(java.lang.Object, int)
   */
  @SuppressWarnings("unchecked")
  public Image getColumnImage(Object element, int columnIndex) {
    if (!(element instanceof NodeWrapper)) {
      return null;
    }
    NodeWrapper<NodeDisplayProperty> wrapper =
        (NodeWrapper<NodeDisplayProperty>) element;
    NodeDisplayProperty property = wrapper.getContent();
    GraphNode node = wrapper.getNode();

    switch (columnIndex) {
    case NodeEditorTool.INDEX_NAME:
      return Tools.getIcon(node);
    case NodeEditorTool.INDEX_VISIBLE:
      return Resources.getOnOff(property.isVisible());
    case NodeEditorTool.INDEX_SELECTED:
      return Resources.getOnOff(property.isSelected());
    default:
      break;
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITableLabelProvider
   *      #getColumnText(java.lang.Object, int)
   */
  @SuppressWarnings("unchecked")
  public String getColumnText(Object element, int columnIndex) {
    if (element instanceof NodeWrapper) {
      return getColumnText(
          (NodeWrapper<NodeDisplayProperty>) element, columnIndex);
    }
    return null;
  }

  public String getColumnText(
      NodeWrapper<NodeDisplayProperty> element, int columnIndex) {
    switch (columnIndex) {
    case NodeEditorTool.INDEX_NAME:
      return element.getNode().friendlyString();
    case NodeEditorTool.INDEX_VISIBLE:
      return "";
    case NodeEditorTool.INDEX_SELECTED:
      return "";
    case NodeEditorTool.INDEX_SIZE:
      return element.getContent().getSize().toString().toLowerCase();
    case NodeEditorTool.INDEX_COLOR:
      if (element.getContent().getColor() == null) {
        return "";
      }
      Color c = element.getContent().getColor();
      return Tools.getRgb(c);
    default:
      break;
    }
    return "";
  }
}