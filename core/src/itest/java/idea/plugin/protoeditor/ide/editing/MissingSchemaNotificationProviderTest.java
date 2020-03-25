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
package idea.plugin.protoeditor.ide.editing;

import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.psi.PsiFile;
import com.intellij.ui.EditorNotificationPanel;
import idea.plugin.protoeditor.TestUtils;
import idea.plugin.protoeditor.fixtures.PbCodeInsightFixtureTestCase;
import idea.plugin.protoeditor.lang.PbFileType;
import idea.plugin.protoeditor.lang.PbTextFileType;

import static com.google.common.truth.Truth.assertThat;

/** Tests for {@link MissingSchemaNotificationProvider}. */
public class MissingSchemaNotificationProviderTest extends PbCodeInsightFixtureTestCase {

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(this);
    return discoveredPath == null ? "" : discoveredPath;
  }

  public void testNoActionOnNonTextFormatFile() {
    PsiFile psiFile = myFixture.configureByText(PbFileType.INSTANCE, file("// Comment"));
    TextEditor textEditor = TextEditorProvider.getInstance().getTextEditor(myFixture.getEditor());
    EditorNotificationPanel panel =
        new MissingSchemaNotificationProvider()
            .createNotificationPanel(psiFile.getVirtualFile(), textEditor, myFixture.getProject());
    assertThat(panel).isNull();
  }

  public void testCreatesPanelForTextFormatFile() {
    PsiFile psiFile = myFixture.configureByText(PbTextFileType.INSTANCE, file("# Comment"));
    TextEditor textEditor = TextEditorProvider.getInstance().getTextEditor(myFixture.getEditor());
    EditorNotificationPanel panel =
        new MissingSchemaNotificationProvider()
            .createNotificationPanel(psiFile.getVirtualFile(), textEditor, myFixture.getProject());
    assertThat(panel).isNotNull();
  }

  public void testNoActionWithExistingAnnotation() {
    PsiFile psiFile = myFixture.configureByText(PbTextFileType.INSTANCE, file("# proto-file: foo"));
    TextEditor textEditor = TextEditorProvider.getInstance().getTextEditor(myFixture.getEditor());
    EditorNotificationPanel panel =
        new MissingSchemaNotificationProvider()
            .createNotificationPanel(psiFile.getVirtualFile(), textEditor, myFixture.getProject());
    assertThat(panel).isNull();
  }

  private String file(String... lines) {
    return String.join("\n", lines) + "\n";
  }
}
