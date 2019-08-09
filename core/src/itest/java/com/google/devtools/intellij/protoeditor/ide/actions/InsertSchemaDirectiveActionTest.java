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
package com.google.devtools.intellij.protoeditor.ide.actions;

import static com.google.common.truth.Truth.assertThat;

import com.google.devtools.intellij.protoeditor.TestUtils;
import com.google.devtools.intellij.protoeditor.fixtures.PbCodeInsightFixtureTestCase;
import com.google.devtools.intellij.protoeditor.lang.PbFileType;
import com.google.devtools.intellij.protoeditor.lang.PbTextFileType;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileTypes.FileType;

/** Tests for {@link InsertSchemaDirectiveAction}. */
public class InsertSchemaDirectiveActionTest extends PbCodeInsightFixtureTestCase {

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(InsertSchemaDirectiveActionTest.class);
    return discoveredPath == null ? "" : discoveredPath;
  }

  public void testActionIsVisibleForTextFormat() {
    AnAction action = getAction();
    AnActionEvent event = getTextEvent(action, "# Comment\n", PbTextFileType.INSTANCE);
    action.update(event);
    assertThat(event.getPresentation().isEnabledAndVisible()).isTrue();
  }

  public void testActionIsNotVisibleForOthers() {
    AnAction action = getAction();
    AnActionEvent event = getTextEvent(action, "// Comment\n", PbFileType.INSTANCE);
    action.update(event);
    assertThat(event.getPresentation().isEnabledAndVisible()).isFalse();
  }

  public void testInsertEmptyFile() {
    AnAction action = getAction();
    AnActionEvent event = getTextEvent(action, "");
    action.actionPerformed(event);
    // For some reason, uninitialized variables default to 'a': https://git.io/v9cdH
    // But this only happens when running tests locally. When running via TAP, the "a" string
    // isn't present. As a workaround, these tests allow either "a" or "".
    // TODO(volkman): figure out what causes the TAP/local discrepancy.
    assertThat(myFixture.getFile().getText())
        .matches(file("# proto-file: a?", "# proto-message: a?"));
  }

  public void testInsertNoBlankLineBeforeEntries() {
    AnAction action = getAction();
    AnActionEvent event = getTextEvent(action, file("# First", "# Second", "foo: bar"));
    action.actionPerformed(event);
    assertThat(myFixture.getFile().getText())
        .matches(
            file("# First", "# Second", "# proto-file: a?", "# proto-message: a?", "foo: bar"));
  }

  public void testInsertAfterExistingBlock() {
    AnAction action = getAction();
    AnActionEvent event =
        getTextEvent(action, file("# First", "# Second", "# Third", "", "# New block", "foo: bar"));
    action.actionPerformed(event);
    assertThat(myFixture.getFile().getText())
        .matches(
            file(
                "# First",
                "# Second",
                "# Third",
                "# proto-file: a?",
                "# proto-message: a?",
                "",
                "# New block",
                "foo: bar"));
  }

  public void testUseExistingFileValue() {
    AnAction action = getAction();
    AnActionEvent event =
        getTextEvent(
            action,
            file(
                "# First",
                "# proto-file: foo/bar.proto",
                "# Second",
                "# Third",
                "",
                "# New block",
                "foo: bar"));
    action.actionPerformed(event);
    assertThat(myFixture.getFile().getText())
        .matches(
            file(
                "# First",
                "# Second",
                "# Third",
                "# proto-file: foo/bar.proto",
                "# proto-message: a?",
                "",
                "# New block",
                "foo: bar"));
  }

  public void testUseExistingMessageValue() {
    AnAction action = getAction();
    AnActionEvent event =
        getTextEvent(
            action,
            file(
                "# First",
                "# proto-message: Foo",
                "# Second",
                "# Third",
                "",
                "# New block",
                "foo: bar"));
    action.actionPerformed(event);
    assertThat(myFixture.getFile().getText())
        .matches(
            file(
                "# First",
                "# Second",
                "# Third",
                "# proto-file: a?",
                "# proto-message: Foo",
                "",
                "# New block",
                "foo: bar"));
  }

  public void testUseBothExistingValues() {
    AnAction action = getAction();
    AnActionEvent event =
        getTextEvent(
            action,
            file(
                "# First",
                "# proto-message: Foo",
                "# Second",
                "# proto-file: foo/bar.proto",
                "# Third",
                "",
                "# New block",
                "foo: bar"));
    action.actionPerformed(event);
    assertThat(myFixture.getFile().getText())
        .isEqualTo(
            file(
                "# First",
                "# Second",
                "# Third",
                "# proto-file: foo/bar.proto",
                "# proto-message: Foo",
                "",
                "# New block",
                "foo: bar"));
  }

  public void testInsertImportsAtEnd() {
    AnAction action = getAction();
    AnActionEvent event =
        getTextEvent(
            action,
            file(
                "# proto-import: foo/biz.proto",
                "# First",
                "# proto-message: Foo",
                "# Second",
                "# proto-file: foo/bar.proto",
                "# Third",
                "# proto-import: foo/baz.proto",
                "# Fourth",
                "",
                "# New block",
                "foo: bar"));
    action.actionPerformed(event);
    assertThat(myFixture.getFile().getText())
        .isEqualTo(
            file(
                "# First",
                "# Second",
                "# Third",
                "# Fourth",
                "# proto-file: foo/bar.proto",
                "# proto-message: Foo",
                "# proto-import: foo/biz.proto",
                "# proto-import: foo/baz.proto",
                "",
                "# New block",
                "foo: bar"));
  }

  private String file(String... lines) {
    return String.join("\n", lines) + "\n";
  }

  private AnActionEvent getTextEvent(AnAction action, String text) {
    return getTextEvent(action, text, PbTextFileType.INSTANCE);
  }

  private AnActionEvent getTextEvent(AnAction action, String text, FileType fileType) {
    myFixture.configureByText(fileType, text);
    return getEditorEvent(action);
  }

  private AnActionEvent getEditorEvent(AnAction action) {
    return AnActionEvent.createFromAnAction(
        action, null, "", DataManager.getInstance().getDataContext(getEditor().getComponent()));
  }

  private AnAction getAction() {
    AnAction action = ActionManager.getInstance().getAction(InsertSchemaDirectiveAction.ACTION_ID);
    assertThat(action).isNotNull();
    return action;
  }
}
