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
package idea.plugin.protoeditor.lang.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.QualifiedName;
import idea.plugin.protoeditor.TestUtils;
import idea.plugin.protoeditor.fixtures.PbCodeInsightFixtureTestCase;
import idea.plugin.protoeditor.lang.psi.PbField;
import org.junit.Assert;

/**
 * Tests for stream options. Streams aren't part of the open source release, so unlike the other
 * tests in {@link PbOptionReferenceTest}, this test does not use the open source descriptor.proto.
 */
public class PbStreamOptionReferenceTest extends PbCodeInsightFixtureTestCase {

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(this);
    return discoveredPath == null ? "" : discoveredPath;
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    String descriptorPath = "lang/options/stream_descriptor.proto";
    myFixture.copyFileToProject(descriptorPath, descriptorPath);
    TestUtils.addTestFileResolveProvider(
        getProject(), descriptorPath, getTestRootDisposable());
    TestUtils.registerTestdataFileExtension();
  }

  public void testStreamOption() {
    String filename = "lang/options/" + getTestName(false) + ".proto.testdata";
    PsiReference ref = myFixture.getReferenceAtCaretPosition(filename);
    assertNotNull(ref);
    PsiElement element = ref.resolve();
    Assert.assertTrue(element instanceof PbField);
    QualifiedName qualifiedName = ((PbField) element).getQualifiedName();
    Assert.assertNotNull(qualifiedName);
    Assert.assertEquals("proto2.StreamOptions.deprecated", qualifiedName.toString());
  }
}
