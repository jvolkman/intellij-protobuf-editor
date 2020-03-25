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
import com.google.devtools.intellij.protoeditor.lang.PbTextFileType;
import com.intellij.codeInsight.completion.CompletionType;
import java.util.List;

/** Tests completion scenarios for text format schema comments. */
public class PbTextDirectiveCompletionTest extends PbCodeInsightFixtureTestCase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    TestUtils.addTestFileResolveProvider(getProject(), getTestRootDisposable());
    myFixture.configureByFile("lang/resolve/root_message.proto");
    myFixture.configureByFile("lang/resolve/other_message.proto");
  }

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(this);
    return discoveredPath == null ? "" : discoveredPath;
  }

  public void testCommentPrefixCompletions() {
    List<String> completions = completeStrings("# <caret>");
    assertThat(completions).containsAtLeast("proto-file: ", "proto-message: ", "proto-import: ");
  }

  public void testCommentPrefixCompletionsAfterExistingTokenPrefix() {
    List<String> completions = completeStrings("# pro<caret>");
    assertThat(completions).containsAtLeast("proto-file: ", "proto-message: ", "proto-import: ");
  }

  public void testCommentPrefixCompletionsBeforeExistingToken() {
    List<String> completions = completeStrings("# <caret> foo");
    assertThat(completions).containsAtLeast("proto-file: ", "proto-message: ", "proto-import: ");
  }

  public void testNoCompletionsAfterExistingToken() {
    List<String> completions = completeStrings("# foo <caret>");
    assertThat(completions).isEmpty();
  }

  public void testAlreadySpecifiedCommentsAreNotSuggested() {
    List<String> completions = completeStrings("# proto-file: foo\n# <caret>");
    assertThat(completions).containsAtLeast("proto-message: ", "proto-import: ");
    assertThat(completions).doesNotContain("proto-file: ");

    completions = completeStrings("# proto-message: foo\n# <caret>");
    assertThat(completions).containsAtLeast("proto-file: ", "proto-import: ");
    assertThat(completions).doesNotContain("proto-message: ");
  }

  public void testMultipleImports() {
    List<String> completions =
        completeStrings("# proto-file: foo\n# proto-message: bar\n# proto-import: foo\n# <caret>");
    assertThat(completions).containsExactly("proto-import: ");
  }

  public void testFileCompletion() {
    List<String> completions = completeStrings("# proto-file: lang/resolve/<caret>");
    assertThat(completions)
        .containsAtLeast("lang/resolve/root_message.proto", "lang/resolve/other_message.proto");
  }

  public void testMessageCompletion() {
    List<String> completions =
        completeStrings(
            "# proto-file: lang/resolve/root_message.proto\n" + "# proto-message: <caret>");
    assertThat(completions).containsAtLeast("foo.", "bar.", "Message");
  }

  private List<String> completeStrings(String text) {
    myFixture.configureByText(PbTextFileType.INSTANCE, text);
    myFixture.complete(CompletionType.BASIC, 1);
    return myFixture.getLookupElementStrings();
  }
}
