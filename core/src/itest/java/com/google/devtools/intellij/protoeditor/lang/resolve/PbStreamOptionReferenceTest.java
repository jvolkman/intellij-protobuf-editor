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
import com.google.devtools.intellij.protoeditor.lang.psi.PbField;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.QualifiedName;
import com.intellij.testFramework.ResolveTestCase;
import org.junit.Assert;

/**
 * Tests for stream options. Streams aren't part of the open source release, so unlike the other
 * tests in {@link PbOptionReferenceTest}, this test does not use the open source descriptor.proto.
 */
public class PbStreamOptionReferenceTest extends ResolveTestCase {

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(PbStreamOptionReferenceTest.class);
    return discoveredPath == null ? "" : discoveredPath;
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    String descriptorText = loadFile("lang/options/stream_descriptor.proto");
    createFile("stream_descriptor.proto", descriptorText);
    TestUtils.addTestFileResolveProvider(getProject(), "stream_descriptor.proto");
    TestUtils.registerTestdataFileExtension();
  }

  @Override
  public void tearDown() throws Exception {
    TestUtils.removeTestFileResolveProvider(getProject());
    super.tearDown();
  }

  public void testStreamOption() throws Exception {
    PsiReference ref = configureByFile("lang/options/" + getTestName(false) + ".proto.testdata");
    PsiElement element = ref.resolve();
    Assert.assertTrue(element instanceof PbField);
    QualifiedName qualifiedName = ((PbField) element).getQualifiedName();
    Assert.assertNotNull(qualifiedName);
    Assert.assertEquals("proto2.StreamOptions.deprecated", qualifiedName.toString());
  }
}
