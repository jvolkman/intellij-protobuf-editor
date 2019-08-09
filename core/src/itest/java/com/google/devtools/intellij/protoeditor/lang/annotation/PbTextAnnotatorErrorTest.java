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
package com.google.devtools.intellij.protoeditor.lang.annotation;

import com.google.devtools.intellij.protoeditor.TestUtils;
import com.google.devtools.intellij.protoeditor.fixtures.PbCodeInsightFixtureTestCase;
import com.intellij.psi.PsiFile;

/** Tests for {@link PbTextAnnotator} error annotations. */
public class PbTextAnnotatorErrorTest extends PbCodeInsightFixtureTestCase {

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(PbTextAnnotatorErrorTest.class);
    return discoveredPath == null ? "" : discoveredPath;
  }

  private void doTest(String filename) {
    PsiFile testFile = myFixture.configureByFile(filename);
    myFixture.testHighlighting(
        /* checkWarnings= */ false,
        /* checkInfos= */ false,
        /* checkWeakWarnings= */ false,
        testFile.getVirtualFile());
  }

  public void testStringErrorAnnotations() {
    doTest("lang/annotation/StringErrors.pb");
  }
}
