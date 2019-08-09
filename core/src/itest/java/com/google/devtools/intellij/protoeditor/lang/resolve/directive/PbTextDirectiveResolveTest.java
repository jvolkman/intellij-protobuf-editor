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
package com.google.devtools.intellij.protoeditor.lang.resolve.directive;

import static com.google.common.truth.Truth.assertThat;

import com.google.devtools.intellij.protoeditor.TestUtils;
import com.google.devtools.intellij.protoeditor.fixtures.PbCodeInsightFixtureTestCase;
import com.google.devtools.intellij.protoeditor.lang.psi.PbField;
import com.google.devtools.intellij.protoeditor.lang.psi.PbFile;
import com.google.devtools.intellij.protoeditor.lang.psi.PbMessageType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.QualifiedName;

/** Tests for references when using comment-based format directives in text format files. */
public class PbTextDirectiveResolveTest extends PbCodeInsightFixtureTestCase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    TestUtils.addTestFileResolveProvider(getProject());
  }

  @Override
  public void tearDown() throws Exception {
    TestUtils.removeTestFileResolveProvider(getProject());
    super.tearDown();
  }

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(PbTextDirectiveResolveTest.class);
    return discoveredPath == null ? "" : discoveredPath;
  }

  private PsiElement resolve() throws Exception {
    String filename = "lang/resolve/directive/" + getTestName(false) + ".pb";
    PsiReference ref = myFixture.getReferenceAtCaretPosition(filename);
    assertThat(ref).isNotNull();
    return ref.resolve();
  }

  public void testFileRef() throws Exception {
    myFixture.configureByFile("lang/resolve/root_message.proto");
    PsiElement target = resolve();
    assertThat(target).isInstanceOf(PbFile.class);
  }

  public void testMessageRef() throws Exception {
    myFixture.configureByFile("lang/resolve/root_message.proto");
    PsiElement target = resolve();
    assertThat(target).isInstanceOf(PbMessageType.class);
    QualifiedName qualifiedName = ((PbMessageType) target).getQualifiedName();
    assertThat(qualifiedName).isNotNull();
    assertThat(qualifiedName.toString()).isEqualTo("foo.bar.Message");
  }

  public void testPublicMessageRef() throws Exception {
    myFixture.configureByFile("lang/resolve/root_message.proto");
    myFixture.configureByFile("lang/resolve/public_message.proto");
    PsiElement target = resolve();
    assertThat(target).isInstanceOf(PbMessageType.class);
    QualifiedName qualifiedName = ((PbMessageType) target).getQualifiedName();
    assertThat(qualifiedName).isNotNull();
    assertThat(qualifiedName.toString()).isEqualTo("foo.bar.Message");
  }

  public void testFieldRef() throws Exception {
    myFixture.configureByFile("lang/resolve/root_message.proto");
    PsiElement target = resolve();
    assertThat(target).isInstanceOf(PbField.class);
    QualifiedName qualifiedName = ((PbField) target).getQualifiedName();
    assertThat(qualifiedName).isNotNull();
    assertThat(qualifiedName.toString()).isEqualTo("foo.bar.Message.number");
  }
}
