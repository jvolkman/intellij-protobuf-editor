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
package idea.plugin.protoeditor.erlang;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.QualifiedName;
import idea.plugin.protoeditor.lang.psi.PbElement;
import idea.plugin.protoeditor.lang.psi.PbNamedElement;
import idea.plugin.protoeditor.lang.stub.index.ShortNameIndex;
import org.intellij.erlang.ErlangLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/** Handles goto declaration from erlang generated code -> .proto files. */
public final class PbErlangGotoDeclarationHandler implements GotoDeclarationHandler {

  @Nullable
  @Override
  public String getActionText(@NotNull DataContext context) {
    return null;
  }

  @Nullable
  @Override
  public PsiElement[] getGotoDeclarationTargets(
      @Nullable PsiElement sourceElement, int offset, Editor editor) {
    if (sourceElement == null) {
      return null;
    }
    if (!sourceElement.getLanguage().is(ErlangLanguage.INSTANCE)) {
      return null;
    }
    ErlFileReferenceContext context = ErlFileReferenceContext.findContext(sourceElement);
    if (context == null) {
      return null;
    }
    Collection<? extends PbElement> matches = erlangToProto(context, sourceElement.getProject());
    if (matches == null || matches.isEmpty()) {
      return null;
    }
    return matches.toArray(new PsiElement[0]);
  }

  public static ImmutableCollection<? extends PbElement> erlangToProto(
          ErlFileReferenceContext referenceContext, Project project) {
    return findPbByIndex(referenceContext.getFileLocalSymbol(), project);
  }

  private static ImmutableCollection<? extends PbElement> findPbByIndex(QualifiedName protoName, Project project) {
    String nameFirst = protoName.getFirstComponent();
    if (nameFirst == null) return null;
    Collection<PbNamedElement> pbNamedElements = ShortNameIndex.getInstance().get(nameFirst, project, GlobalSearchScope.projectScope(project));
    if (pbNamedElements.size() > 0)
      return ImmutableList.copyOf(pbNamedElements);
    return null;
  }
}
