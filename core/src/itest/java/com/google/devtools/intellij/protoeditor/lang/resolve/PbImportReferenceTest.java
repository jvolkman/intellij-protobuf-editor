/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.devtools.intellij.protoeditor.lang.resolve;

import com.google.devtools.intellij.protoeditor.TestUtils;
import com.google.devtools.intellij.protoeditor.lang.psi.PbFile;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.ResolveTestCase;
import com.intellij.testFramework.VfsTestUtil;
import org.junit.Assert;

/** Tests for {@link PbImportReference}. */
public class PbImportReferenceTest extends ResolveTestCase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    TestUtils.addTestFileResolveProvider(getProject(), getTestRootDisposable());
    TestUtils.registerTestdataFileExtension();
  }

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(this);
    String path = discoveredPath == null ? "" : discoveredPath;
    return path + "lang/resolve/";
  }

  public void testImportSibling() throws Exception {
    String siblingProto = "Sibling.proto";

    final VirtualFile vFile =
        VfsTestUtil.findFileByCaseSensitivePath(getTestDataPath() + siblingProto);
    assertNotNull(vFile);
    createFile(myModule, siblingProto, VfsUtil.loadText(vFile));

    assertIsFileType(resolve(), siblingProto);
  }

  public void testImportIncompleteQuotes() throws Exception {
    // Just test that there's no exception while attempting to resolve.
    loadIncompleteReference();
  }

  private PsiElement resolve() throws Exception {
    PsiReference ref = configureByFile(getTestName(false) + ".proto.testdata");
    return ref.resolve();
  }

  /**
   * Like {@link ResolveTestCase#configureByFile(String)}, but assumes the reference at the ref
   * marker will be null.
   */
  private void loadIncompleteReference() throws Exception {
    String filePath = getTestName(false) + ".proto.testdata";
    VirtualFile vFile = VfsTestUtil.findFileByCaseSensitivePath(this.getTestDataPath() + filePath);
    assertNotNull("file " + filePath + " not found", vFile);
    String fileText = StringUtil.convertLineSeparators(VfsUtilCore.loadText(vFile));
    int offset = fileText.indexOf("<ref>");
    assertTrue(offset >= 0);
    fileText = fileText.substring(0, offset) + fileText.substring(offset + "<ref>".length());
    this.myFile = this.createFile(this.myModule, vFile.getName(), fileText);
    PsiReference ref = this.myFile.findReferenceAt(offset);
    assertNull(ref);
  }

  private static void assertIsFileType(PsiElement target, String name) {
    Assert.assertTrue(target instanceof PbFile);
    String fileName = ((PbFile) target).getName();
    Assert.assertNotNull(fileName);
    Assert.assertEquals(name, fileName);
  }
}
