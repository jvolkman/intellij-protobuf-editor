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
import com.google.devtools.intellij.protoeditor.lang.psi.PbEnumValue;
import com.google.devtools.intellij.protoeditor.lang.psi.PbField;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.QualifiedName;
import com.intellij.testFramework.ResolveTestCase;
import org.junit.Assert;

/** Tests for {@link PbOptionNameReference}. */
public class PbOptionReferenceTest extends ResolveTestCase {

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(PbOptionReferenceTest.class);
    return discoveredPath == null ? "" : discoveredPath;
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    createFile("descriptor.proto", TestUtils.getOpensourceDescriptorText());
    TestUtils.addTestFileResolveProvider(getProject(), "descriptor.proto");
    TestUtils.registerTestdataFileExtension();
  }

  @Override
  public void tearDown() throws Exception {
    TestUtils.removeTestFileResolveProvider(getProject());
    super.tearDown();
  }

  private PsiElement resolve() throws Exception {
    PsiReference ref = configureByFile("lang/options/" + getTestName(false) + ".proto.testdata");
    return ref.resolve();
  }

  public void testCustomExtendedFieldOption() throws Exception {
    assertIsField(resolve(), "foo.bar.crazy_extended_option_field");
  }

  public void testCustomFieldOption() throws Exception {
    assertIsField(resolve(), "foo.bar.MyType.crazy_option_field");
  }

  public void testEnumOption() throws Exception {
    assertIsField(resolve(), "google.protobuf.EnumOptions.allow_alias");
  }

  public void testEnumValueOption() throws Exception {
    assertIsField(resolve(), "google.protobuf.EnumValueOptions.deprecated");
  }

  public void testFieldOption() throws Exception {
    assertIsField(resolve(), "google.protobuf.FieldOptions.weak");
  }

  public void testFileOption() throws Exception {
    assertIsField(resolve(), "google.protobuf.FileOptions.java_package");
  }

  public void testMessageOption() throws Exception {
    assertIsField(resolve(), "google.protobuf.MessageOptions.deprecated");
  }

  public void testMethodOption() throws Exception {
    assertIsField(resolve(), "google.protobuf.MethodOptions.deprecated");
  }

  public void testOneofCustomOption() throws Exception {
    assertIsField(resolve(), "foo.bar.test_oneof_option");
  }

  public void testServiceOption() throws Exception {
    assertIsField(resolve(), "google.protobuf.ServiceOptions.deprecated");
  }

  public void testMapKeyOption() throws Exception {
    assertIsField(resolve(), "foo.bar.TestMessage.SomeMapEntry.key");
  }

  public void testMapValueOption() throws Exception {
    assertIsField(resolve(), "foo.bar.TestMessage.SomeMapEntry.value");
  }

  public void testGroupDefinitionFieldOption() throws Exception {
    assertIsField(resolve(), "foo.bar.mygroupoption");
  }

  public void testGroupDefinitionMemberOption() throws Exception {
    assertIsField(resolve(), "foo.bar.MyGroupOption.zz");
  }

  public void testEnumValueReference() throws Exception {
    assertIsEnumValue(resolve(), "google.protobuf.FieldOptions.STRING_PIECE");
  }

  public void testCustomOptionEnumValueReference() throws Exception {
    assertIsEnumValue(resolve(), "foo.bar.FOO");
  }

  public void testCustomOptionExtensionNameEnumValueReference() throws Exception {
    assertIsEnumValue(resolve(), "foo.bar.FOO");
  }

  public void testCustomOptionEnumKeywordNameReference() throws Exception {
    assertIsEnumValue(resolve(), "foo.bar.inf");
  }

  public void testOptionNameDoesNotResolveExtensionField() throws Exception {
    // A non-extension option name should not resolve extension fields in the target type.
    assertNull(resolve());
  }

  public void testFirstOfMultipleExtensionNames() throws Exception {
    assertIsField(resolve(), "foo.bar.opt");
  }

  public void testExtensionRangeOption() throws Exception {
    assertIsField(resolve(), "foo.bar.value_ext");
  }

  private static void assertIsField(PsiElement target, String name) {
    Assert.assertTrue(target instanceof PbField);
    QualifiedName qualifiedName = ((PbField) target).getQualifiedName();
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
