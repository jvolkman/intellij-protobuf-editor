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
import com.google.devtools.intellij.protoeditor.fixtures.PbCodeInsightFixtureTestCase;
import com.google.devtools.intellij.protoeditor.lang.psi.PbEnumValue;
import com.google.devtools.intellij.protoeditor.lang.psi.PbField;
import com.google.devtools.intellij.protoeditor.lang.psi.PbMessageType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.QualifiedName;
import org.junit.Assert;

/** Tests for injected text format references. */
public class PbTextReferenceTest extends PbCodeInsightFixtureTestCase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.addFileToProject(
        TestUtils.OPENSOURCE_DESCRIPTOR_PATH, TestUtils.getOpensourceDescriptorText());
    TestUtils.addTestFileResolveProvider(
        getProject(), TestUtils.OPENSOURCE_DESCRIPTOR_PATH, getTestRootDisposable());
    TestUtils.registerTestdataFileExtension();
  }

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(this);
    return discoveredPath == null ? "" : discoveredPath;
  }

  private PsiElement resolve() throws Exception {
    String filename = "lang/options/" + getTestName(false) + ".proto.testdata";
    PsiReference ref = myFixture.getReferenceAtCaretPosition(filename);
    assertNotNull(ref);
    return ref.resolve();
  }

  public void testTextTopLevelFieldReference() throws Exception {
    assertIsField(resolve(), "foo.bar.MyType.nested");
  }

  public void testTextNestedFieldReference() throws Exception {
    assertIsField(resolve(), "foo.bar.MyNestedType.str");
  }

  public void testTextEnumValueReference() throws Exception {
    assertIsEnumValue(resolve(), "foo.bar.FOO");
  }

  public void testTextEnumNumberReference() throws Exception {
    assertIsEnumValue(resolve(), "foo.bar.FOO");
  }

  public void testTextExtensionNameReference() throws Exception {
    assertIsField(resolve(), "foo.bar.ext_opt");
  }

  public void testTextAnyTypeReference() throws Exception {
    myFixture.configureByFile("lang/options/any.proto");
    assertIsMessage(resolve(), "foo.bar.MyType");
  }

  public void testTextAnyValueReference() throws Exception {
    myFixture.configureByFile("lang/options/any.proto");
    assertIsField(resolve(), "foo.bar.MyType.value");
  }

  public void testTextAnyDirectValueReference() throws Exception {
    myFixture.configureByFile("lang/options/any.proto");
    assertIsField(resolve(), "foo.bar.MyType.value");
  }

  public void testTextNestedAnyValueReference() throws Exception {
    myFixture.configureByFile("lang/options/any.proto");
    assertIsField(resolve(), "foo.bar.AnyType2.bar");
  }

  public void testTextFieldDoesNotResolveExtensionField() throws Exception {
    // A non-extension field name should not resolve extension fields in the target type.
    assertNull(resolve());
  }

  public void testAggregateExtensionImport() throws Exception {
    myFixture.configureByFile("lang/options/constraints.proto");
    myFixture.configureByFile("lang/options/validation.proto");
    assertIsField(resolve(), "ext_bug.validation.InRange.min");
  }

  public void testTextGroupDefinitionFieldOption() throws Exception {
    assertIsField(resolve(), "foo.bar.OptionType.mygroupoption");
  }

  public void testTextGroupDefinitionFieldOptionUnresolved() throws Exception {
    assertNull(resolve());
  }

  public void testTextGroupDefinitionMemberOption() throws Exception {
    assertIsField(resolve(), "foo.bar.OptionType.MyGroupOption.zz");
  }

  private static void assertIsField(PsiElement target, String name) {
    Assert.assertTrue(target instanceof PbField);
    QualifiedName qualifiedName = ((PbField) target).getQualifiedName();
    Assert.assertNotNull(qualifiedName);
    Assert.assertEquals(name, qualifiedName.toString());
  }

  private static void assertIsMessage(PsiElement target, String name) {
    Assert.assertTrue(target instanceof PbMessageType);
    QualifiedName qualifiedName = ((PbMessageType) target).getQualifiedName();
    Assert.assertNotNull(qualifiedName);
    Assert.assertEquals(name, qualifiedName.toString());
  }

  private static void assertIsEnumValue(PsiElement target, String name) {
    Assert.assertTrue(target instanceof PbEnumValue);
    QualifiedName qualifiedName = ((PbEnumValue) target).getQualifiedName();
    Assert.assertNotNull(qualifiedName);
    Assert.assertEquals(name, qualifiedName.toString());
  }
}
