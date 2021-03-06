/*
 * Copyright 2014 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.eclipse.ui.widgets;

import com.google.devtools.depan.eclipse.visualization.ogl.EdgeRenderingProperty;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.platform.AlphabeticSorter;
import com.google.devtools.depan.platform.Colors;
import com.google.devtools.depan.platform.InverseSorter;
import com.google.devtools.depan.platform.LabelProviderToString;
import com.google.devtools.depan.platform.eclipse.ui.tables.EditColTableDef;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Selections;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.model.EdgeDisplayRepository;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import java.awt.Color;
import java.util.Collection;

/**
 * Show a table of edges with their {@link EdgeDisplayProperty} rendering
 * properties.
 * 
 * Changes to the {@link EdgeRenderingProperty}s are managed through
 * {@link EdgeDisplayRepository.ChangeListener}s.
 * 
 * [Jun 2016] Given the number of columns shared with
 * {@link RelationDisplayTableControl}, these classes should be sharing more
 * of their implementation.  See also {@link NodeDisplayTableControl} for
 * additional columns to consider.
 */
public class EdgeDisplayTableControl extends Composite {

  public static final String COL_NAME = "Relation";
  public static final String COL_HEAD = "Head";
  public static final String COL_TAIL = "Tail";
  public static final String COL_COLOR = "Color";
  public static final String COL_WIDTH = "Width";
  public static final String COL_STYLE = "Style";
  public static final String COL_SHAPE = "Shape";
  public static final String COL_ARROWHEAD = "Arrowhead";

  public static final int INDEX_NAME = 0;
  public static final int INDEX_HEAD = 1;
  public static final int INDEX_TAIL = 2;
  public static final int INDEX_COLOR = 3;
  public static final int INDEX_STYLE = 4;
  public static final int INDEX_ARROWHEAD = 5;

  private static final EditColTableDef[] TABLE_DEF = new EditColTableDef[] {
    new EditColTableDef(COL_NAME, false, COL_NAME, 180),
    new EditColTableDef(COL_HEAD, false, COL_HEAD, 180),
    new EditColTableDef(COL_TAIL, false, COL_TAIL, 180),
    new EditColTableDef(COL_COLOR, true, COL_COLOR, 80),
//    new EditColTableDef(COL_WIDTH, false, COL_WIDTH, 180),
    new EditColTableDef(COL_STYLE, true, COL_STYLE, 60),
    new EditColTableDef(COL_ARROWHEAD, true, COL_ARROWHEAD, 110),
//    new EditColTableDef(COL_SHAPE, false, COL_SHAPE, 180),
  };

  private static final String[] LINE_WIDTHS = {
    "0", "1", "2", "3", "4"
  };

  private static final String[] LINE_SHAPES = {
    "arched", "straight"
  };

  /////////////////////////////////////
  // EdgeDisplayProperty integration

  private static final String[] UPDATE_COLUMNS = new String [] {
    COL_COLOR, COL_WIDTH, COL_ARROWHEAD
  };

  private class ControlChangeListener
      implements EdgeDisplayRepository.ChangeListener {

    @Override
    public void edgeDisplayChanged(GraphEdge edge, EdgeDisplayProperty props) {
      updateNodeColumns(edge, UPDATE_COLUMNS);
    }
  }

  private ControlChangeListener propListener;

  private EdgeDisplayRepository propRepo;

  /////////////////////////////////////
  // UX Elements

  private TableViewer propViewer;

  /////////////////////////////////////
  // Public methods

  public EdgeDisplayTableControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(1));

    propViewer = new TableViewer(this,
        SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);

    // Layout embedded table
    Table propTable = propViewer.getTable();
    propTable.setLayoutData(Widgets.buildGrabFillData());

    // initialize the table
    propTable.setHeaderVisible(true);
    propTable.setToolTipText("Edge Display Properties");
    EditColTableDef.setupTable(TABLE_DEF, propTable);

    // Configure cell editing
    CellEditor[] cellEditors = new CellEditor[TABLE_DEF.length];
    cellEditors[INDEX_NAME] = null;
    cellEditors[INDEX_HEAD] = null;
    cellEditors[INDEX_TAIL] = null;
    cellEditors[INDEX_COLOR] = new ColorCellEditor(propTable);
    cellEditors[INDEX_STYLE] = new ComboBoxCellEditor(propTable,
        toString(EdgeDisplayProperty.LineStyle.values(), true));
    cellEditors[INDEX_ARROWHEAD] = new ComboBoxCellEditor(propTable,
        toString(EdgeDisplayProperty.ArrowheadStyle.values(), true));

    propViewer.setCellEditors(cellEditors);
    propViewer.setLabelProvider(new EdgeDisplayLabelProvider());
    propViewer.setColumnProperties(EditColTableDef.getProperties(TABLE_DEF));
    propViewer.setCellModifier(new EdgeDisplayCellModifier());
    propViewer.setContentProvider(ArrayContentProvider.getInstance());

    configSorters(propTable);
  }

  private String[] toString(Object[] objs, boolean lowercase) {
    String[] s = new String[objs.length];
    int i = 0;
    for (Object o : objs) {
      s[i++] = lowercase ? o.toString().toLowerCase() : o.toString();
    }
    return s;
  }

  /**
   * Fill the list with {@link GraphEdge}s.
   * Since rendering depends on propRepo, set input after 
   * the propRepo is installed.
   */
  public void setInput(Collection<GraphEdge> edges) {
    propViewer.setInput(edges);
  }

  public Collection<GraphEdge> getSelection() {
    ISelection selection = propViewer.getSelection();
    return Selections.getSelection(selection, GraphEdge.class);
  }

  public void setEdgeDisplayRepository(EdgeDisplayRepository edgeDisplayRepo) {
    this.propRepo = edgeDisplayRepo;
    propListener = new ControlChangeListener();
    propRepo.addChangeListener(propListener);
  }

  public void removeEdgeDisplayRepository(EdgeDisplayRepository edgeDisplayRepo) {
    if (null != propListener) {
      this.propRepo.removeChangeListener(propListener);
      propListener = null;
    }
    this.propRepo = null;
  }

  private void updateNodeColumns(GraphEdge edge, String[] cols) {
    propViewer.update(edge, cols);
  }

  /////////////////////////////////////
  // Property repository methods

  /**
   * Acquire properties directly, avoid setting up a default.
   */
  private void saveDisplayProperty(
      GraphEdge edge, EdgeDisplayProperty props) {
    propRepo.setDisplayProperty(edge, props);
  }

  /**
   * Acquire properties directly, avoid setting up a default.
   */
  private EdgeDisplayProperty loadDisplayProperty(GraphEdge edge) {
    return propRepo.getDisplayProperty(edge);
  }

  /**
   * Utility method for both the label provider and cell modifier.
   * Note that the default constructor for {@link EdgeDisplayProperty}
   * uses the default values for all member elements.
   */
  private EdgeDisplayProperty getDisplayProperty(GraphEdge edge) {
    EdgeDisplayProperty relationProp = loadDisplayProperty(edge);
    if (null != relationProp) {
      return relationProp;
    }
    // Provide the default if none are persisted.
    return new EdgeDisplayProperty();
  }

  /////////////////////////////////////
  // Column sorting

  private void configSorters(Table table) {
    int index = 0;
    for (TableColumn column : table.getColumns()) {
      final int colIndex = index++;

      column.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          updateSortColumn((TableColumn) event.widget, colIndex);
        }
      });
    }
  }

  private void updateSortColumn(TableColumn column, int colIndex) {
    setSortColumn(column, colIndex, getSortDirection(column));
  }

  private int getSortDirection(TableColumn column) {
    Table tableControl = (Table) propViewer.getControl();
    if (column != tableControl.getSortColumn()) {
      return SWT.DOWN;
    }
    // If it is unsorted (SWT.NONE), assume down sort
    return (SWT.DOWN == tableControl.getSortDirection())
        ? SWT.UP : SWT.DOWN;
  }

  private void setSortColumn(
      TableColumn column, int colIndex, int direction) {

    ViewerComparator sorter = buildColumnSorter(colIndex);
    if (SWT.UP == direction) {
      sorter = new InverseSorter(sorter);
    }

    Table tableControl = (Table) propViewer.getControl();
    propViewer.setComparator(sorter);
    tableControl.setSortColumn(column);
    tableControl.setSortDirection(direction);
  }

  private ViewerComparator buildColumnSorter(int colIndex) {

    // By default, use an alphabetic sort over the column labels.
    ITableLabelProvider labelProvider =
        (ITableLabelProvider) propViewer.getLabelProvider();
    ViewerComparator result = new AlphabeticSorter(
        new LabelProviderToString(labelProvider, colIndex));
    return result;
  }

  /////////////////////////////////////
  // Label provider for table cell text

  private class EdgeDisplayLabelProvider extends LabelProvider
      implements ITableLabelProvider {

    @Override
    public String getColumnText(Object element, int columnIndex) {
      if (element instanceof GraphEdge) {
        GraphEdge edge = (GraphEdge) element;
        EdgeDisplayProperty prop = getDisplayProperty(edge);
        switch (columnIndex) {
        case INDEX_NAME:
          return edge.getRelation().toString();
        case INDEX_HEAD:
          return edge.getHead().toString();
        case INDEX_TAIL:
          return edge.getTail().toString();
        case INDEX_COLOR:
          return getColorName(prop);
        // case INDEX_WIDTH:
        case INDEX_STYLE:
          if (null != prop) {
            return prop.getLineStyle().toString().toLowerCase();
          }
          return null;
        case INDEX_ARROWHEAD:
          if (null != prop) {
            return prop.getArrowhead().toString().toLowerCase();
          }
          return null;
        }
      }
      return null;
    }

    private String getColorName(EdgeDisplayProperty prop) {
      if (null == prop) {
        return null;
      }
      Color color = prop.getColor();
      if (null == color) {
        return null;
      }
      String result = StringConverter.asString(Colors.rgbFromColor(color));
      return "(" + result + ")";
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      return null;
    }
  }

  /////////////////////////////////////
  // Value provider/modifier for edit cells

  private class EdgeDisplayCellModifier implements ICellModifier{

    @Override
    public boolean canModify(Object element, String property) {
      return EditColTableDef.get(TABLE_DEF, property).isEditable();
    }

    @Override
    public Object getValue(Object element, String property) {
      if (!(element instanceof GraphEdge)) {
        return null;
      }
      GraphEdge edge = (GraphEdge) element;
      EdgeDisplayProperty relProp = getDisplayProperty(edge);
      if (COL_COLOR.equals(property)) {
        Color relColor = relProp.getColor();
        if (null == relColor) {
          return new RGB(0, 0, 0);
        }
         RGB result = Colors.rgbFromColor(relColor);
        return result;
      }
      if (COL_ARROWHEAD.equals(property)) {
        return relProp.getArrowhead().ordinal();
      }
      if (COL_STYLE.equals(property)) {
        return relProp.getLineStyle().ordinal();
      }
      if (COL_SHAPE.equals(property)) {
        
      }
      return null;
    }

    @Override
    public void modify(Object element, String property, Object value) {
      if (!(element instanceof TableItem)) {
        return;
      }
      Object modifiedObject = ((TableItem) element).getData();
      if (!(modifiedObject instanceof GraphEdge)) {
        return;
      }

      GraphEdge edge = (GraphEdge) modifiedObject;

      EdgeDisplayProperty relProp = loadDisplayProperty(edge);
      if (null == relProp) {
        return; // For example, when there is no editor.
      }

      if (property.equals(COL_STYLE) && (value instanceof Integer)) {
        relProp.setLineStyle(EdgeDisplayProperty.LineStyle.values()[(Integer) value]);
      } else if (property.equals(COL_ARROWHEAD) && (value instanceof Integer)) {
        relProp.setArrowhead(EdgeDisplayProperty.ArrowheadStyle.values()[(Integer) value]);
      } else if (property.equals(COL_COLOR) && (value instanceof RGB)) {
        Color newColor = Colors.colorFromRgb((RGB) value);
        relProp.setColor(newColor);
      }

      saveDisplayProperty(edge, relProp);
      // Viewer update via ChangeListener
    }
  }
}
