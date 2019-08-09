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

import com.google.devtools.intellij.protoeditor.lang.psi.PbTextFile;
import com.google.devtools.intellij.protoeditor.lang.resolve.directive.SchemaComment;
import com.google.devtools.intellij.protoeditor.lang.resolve.directive.SchemaDirective;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link AnAction action} that inserts <code>proto-file</code> and <code>proto-message</code>
 * comments into a text format file using a live template to guide the user.
 *
 * <p>Comments are inserted at the end of the first block of line comments within the file, or at
 * the start of the file if no comments exist at the top. If either of the comments already exist,
 * existing values will be used and the comments will be moved to the end of the first comment
 * block.
 */
public class InsertSchemaDirectiveAction extends AnAction {

  public static final String ACTION_ID = "prototext.InsertSchemaDirective";

  @Override
  public void actionPerformed(AnActionEvent event) {
    Project project = event.getData(CommonDataKeys.PROJECT);
    if (project == null) {
      return;
    }
    Editor editor = event.getData(CommonDataKeys.EDITOR);
    if (editor == null) {
      return;
    }
    PsiFile file = event.getData(CommonDataKeys.PSI_FILE);
    if (!(file instanceof PbTextFile)) {
      return;
    }
    PbTextFile textFile = (PbTextFile) file;
    if (!textFile.isWritable()) {
      return;
    }

    WriteCommandAction.runWriteCommandAction(
        project,
        event.getPresentation().getText(),
        /* groupID= */ null,
        () -> insertFileAnnotation(project, textFile, editor));
  }

  /** Update action visibility: only show for physical, writeable text format files. */
  @Override
  public void update(AnActionEvent event) {
    PsiFile file = event.getData(CommonDataKeys.PSI_FILE);
    if (!(file instanceof PbTextFile)) {
      event.getPresentation().setEnabledAndVisible(false);
      return;
    }
    PbTextFile textFile = (PbTextFile) file;
    if (!textFile.isWritable()) {
      event.getPresentation().setEnabledAndVisible(false);
      return;
    }

    event.getPresentation().setEnabledAndVisible(true);
  }

  @Override
  public boolean startInTransaction() {
    return true;
  }

  private static void insertFileAnnotation(Project project, PbTextFile file, Editor editor) {
    PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
    SchemaDirective directive = SchemaDirective.find(file);
    String existingFilename = null;
    String existingMessageName = null;
    List<String> existingImportNames = new ArrayList<>();
    if (directive != null) {
      existingFilename = directive.getFilename();
      existingMessageName = directive.getMessageName();
      removeComment(directive.getFileComment());
      removeComment(directive.getMessageComment());
      for (SchemaComment importComment : directive.getImportComments()) {
        String name = importComment.getName();
        if (name != null) {
          existingImportNames.add(name);
        }
        removeComment(importComment);
      }
      PsiDocumentManager.getInstance(file.getProject())
          .doPostponedOperationsAndUnblockDocument(editor.getDocument());
    }
    Template template =
        createFileAnnotationTemplate(
            file.getProject(), existingFilename, existingMessageName, existingImportNames);
    int targetPos = findEndOfFirstCommentBlock(file);
    editor.getCaretModel().moveToOffset(targetPos);
    TemplateManager.getInstance(file.getProject()).startTemplate(editor, template);
  }

  private static void removeComment(SchemaComment comment) {
    if (comment == null) {
      return;
    }
    PsiElement element = comment.getComment();
    if (element != null) {
      element.delete();
    }
  }

  /**
   * Finds the offset immediately following the first contiguous block of line comments.
   *
   * <pre>
   *   # One comment.
   *   # Another
   *   # And another.
   *   <-- findEndOfFirstCommentBlock() returns this.
   *
   *   # And another, starting a
   *   # new block.
   * </pre>
   */
  private static int findEndOfFirstCommentBlock(PsiFile file) {
    PsiElement child = file.getFirstChild();
    int resultPos = 0;
    while (child instanceof PsiComment) {
      resultPos = child.getTextRange().getEndOffset() + 1;
      child = child.getNextSibling();
      if (child != null && "\n".equals(child.getText())) {
        // Also consume the trailing newline.
        child = child.getNextSibling();
      }
    }
    return resultPos;
  }

  private static Template createFileAnnotationTemplate(
      Project project, String filename, String messageName, List<String> importNames) {
    StringBuilder templateBuilder = new StringBuilder();
    templateBuilder
        .append("# proto-file: ")
        .append(StringUtil.isEmptyOrSpaces(filename) ? "$FILE$" : filename)
        .append('\n');
    templateBuilder
        .append("# proto-message: ")
        .append(StringUtil.isEmptyOrSpaces(messageName) ? "$MESSAGE$" : messageName)
        .append('\n');
    for (String importName : importNames) {
      templateBuilder.append("# proto-import: ").append(importName).append('\n');
    }
    Template template =
        TemplateManager.getInstance(project)
            .createTemplate(/* key= */ "", /* group= */ "", templateBuilder.toString());
    if (StringUtil.isEmptyOrSpaces(filename)) {
      template.addVariable(
          "FILE", "complete()", /* defaultValueExpression= */ null, /* isAlwaysStopAt= */ true);
    }
    if (StringUtil.isEmptyOrSpaces(messageName)) {
      template.addVariable(
          "MESSAGE", "complete()", /* defaultValueExpression= */ null, /* isAlwaysStopAt= */ true);
    }
    return template;
  }
}
