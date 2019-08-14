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
import com.google.devtools.intellij.protoeditor.lang.psi.PbMessageType;
import com.google.devtools.intellij.protoeditor.lang.psi.PbPackageName;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.QualifiedName;
import com.intellij.testFramework.ResolveTestCase;
import org.junit.Assert;

/** Tests for {@link ProtoSymbolPathReference}. */
public class PbTypeReferenceTest extends ResolveTestCase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    TestUtils.registerTestdataFileExtension();
  }

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(this);
    String path = discoveredPath == null ? "" : discoveredPath;
    return path + "lang/resolve/";
  }

  private PsiElement resolve() throws Exception {
    PsiReference ref = configureByFile(getTestName(false) + ".proto.testdata");
    return ref.resolve();
  }

  public void testNonQualifiedType() throws Exception {
    assertIsMessageType(resolve(), "foo.bar.Foo");
  }

  public void testFullyQualifiedType() throws Exception {
    assertIsMessageType(resolve(), "foo.bar.Foo");
  }

  public void testPartiallyQualifiedType() throws Exception {
    assertIsMessageType(resolve(), "foo.bar.Baz.Foo.Tom");
  }

  public void testPartialPackageNameType() throws Exception {
    assertIsMessageType(resolve(), "foo.bar.Foo.Tom");
  }

  public void testIntermediateFullyQualifiedType() throws Exception {
    assertIsMessageType(resolve(), "foo.bar.Foo");
  }

  public void testIntermediateFullyQualifiedPackage() throws Exception {
    assertIsPackage(resolve(), "foo.bar");
  }

  public void testIntermediatePartiallyQualifiedType() throws Exception {
    assertIsMessageType(resolve(), "foo.bar.Foo");
  }

  public void testIntermediatePartiallyQualifiedPackage() throws Exception {
    assertIsPackage(resolve(), "foo.bar");
  }

  public void testIntermediateTypeOfUnresolvedType() throws Exception {
    assertIsMessageType(resolve(), "foo.bar.baz.Foo");
  }

  public void testIntermediatePackageOfUnresolvedType() throws Exception {
    assertIsPackage(resolve(), "foo.bar.baz");
  }

  public void testSameTypeAndFieldName() throws Exception {
    assertIsMessageType(resolve(), "foo.bar.Foo");
  }

  public void testTypeDeclaredInOneof() throws Exception {
    assertIsMessageType(resolve(), "foo.bar.Bar.SomeGroup");
  }

  public void testGeneratedMapEntry() throws Exception {
    assertIsMessageType(resolve(), "foo.bar.Foo.SomeMapEntry");
  }

  public void testInnerMostSymbolWins() throws Exception {
    assertNull("Reference should be unresolvable", resolve());
  }

  public void testFullyQualifiedBuiltInTypeName() throws Exception {
    assertIsMessageType(resolve(), "string");
  }

  public void testUnqualifiedBuiltInTypeName() throws Exception {

    // The following would be ideal, but ResolveTestCase raises an AssertionError if the element
    // at the <ref> marker doesn't return any reference at all, which is the case for built-in
    // types.
    //
    // assertNull("Built-in type should be unresolvable", resolve());
    //
    // TODO(volkman): Stop using ReferenceTestCase, and instead switch to
    // PbCodeInsightFixtureTestCase and use getReferenceAtCaretPosition()

    try {
      resolve();
    } catch (AssertionError e) {
      return;
    }
    throw new AssertionError("Expected AssertionError.");
  }

  private static void assertIsMessageType(PsiElement target, String name) {
    Assert.assertTrue(target instanceof PbMessageType);
    QualifiedName qualifiedName = ((PbMessageType) target).getQualifiedName();
    Assert.assertNotNull(qualifiedName);
    Assert.assertEquals(name, qualifiedName.toString());
  }

  private static void assertIsPackage(PsiElement target, String name) {
    Assert.assertTrue(target instanceof PbPackageName);
    QualifiedName qualifiedName = ((PbPackageName) target).getQualifiedName();
    Assert.assertNotNull(qualifiedName);
    Assert.assertTrue(qualifiedName.matchesPrefix(QualifiedName.fromDottedString(name)));
  }
}
