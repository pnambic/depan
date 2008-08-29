/*
 * Copyright 2008 Google Inc.
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

package com.google.devtools.depan.filesystem.elements;

import com.google.devtools.depan.filesystem.FileSystemElementVisitor;

import java.io.File;

/**
 * {@link FileSystemElement} that represents a file.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class FileElement extends FileSystemElement {
  /**
   * Absolute path to this source file.
   */
  private final String filePath;

  /**
   * Creates a new SourceElement.
   *
   * @param fileName Absolute path to the file.
   */
  public FileElement(String fileName) {
    this.filePath = fileName;
  }

  /**
   * Uses sourceFile to create a hashCode.
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return filePath.hashCode();
  }

  /**
   * Compares the given object with this one and returns <code>true</code> iff
   * the other object is also a <code>FileElement</code> and both objects share
   * the same file path.
   *
   * @return <code>true</code> iff the other object is also a
   * <code>FileElement</code> and both objects share the same file path.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof FileElement) {
      return ((FileElement) obj).filePath.equals(this.filePath);
    }
    return super.equals(obj);
  }

  /**
   * Returns the file name of this object.
   *
   * @return File name of this object.
   */
  @Override
  public String friendlyString() {
    return new File(filePath).getName();
  }

  @Override
  public void accept(FileSystemElementVisitor visitor) {
    visitor.visitFileElement(this);
  }

  /**
   * Returns the file path which may be used to uniquely identify this object.
   *
   * @return File Path which may be used to uniquely identify this object.
   */
  @Override
  public String getId() {
    return filePath;
  }

  /**
   * Returns the <code>String</code> representation of this object.
   *
   * @return <code>String</code> representation of this object.
   */
  @Override
  public String toString() {
    return "Source " + getId();
  }
}
