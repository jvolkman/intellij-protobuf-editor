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

import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.QualifiedName;

import org.intellij.erlang.psi.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Determines the erlang file and qualified name that is referenced from a erlang qualified
 * reference expression.
 */
public class ErlFileReferenceContext {

  private final QualifiedName fileLocalSymbol;
  private final ErlangFile file;

  private ErlFileReferenceContext(ErlangFile file, QualifiedName fileLocalSymbol) {
    this.file = file;
    this.fileLocalSymbol = fileLocalSymbol;
  }

  /**
   * Determines if the given element is a reference expression where a part of the qualifiers
   * resolves to a {@link ErlangFile}. If so, returns a pair of the resolved file and the trailing
   * qualifiers. E.g., given "#pb{filed =1}" returns (pb.hrl, pb.field)
   *
   * @param erlangElement erlang element under consideration
   * @return resolved file + trailing qualifier if found, otherwise null
   */
  @Nullable
  static ErlFileReferenceContext findContext(PsiElement erlangElement) {
    ErlangRecordField recordField = PsiTreeUtil.getParentOfType(erlangElement, ErlangRecordField.class);
    List<String> reversedQualifiers = new ArrayList<>();
    if (recordField != null){
      ErlangQAtom fieldNameAtom = recordField.getFieldNameAtom();
      // must be field atom
      if (!PsiTreeUtil.isAncestor(fieldNameAtom, erlangElement, false)) return null;
      reversedQualifiers.add(fieldNameAtom.getText());
      ErlangRecordExpression recordExpression =
              PsiTreeUtil.getParentOfType(erlangElement, ErlangRecordExpression.class);
      ErlangRecordRef recordRef = recordExpression != null ? recordExpression.getRecordRef() : null;
      if (recordRef == null) return null;
      return getContextByRef(reversedQualifiers, recordRef);
    }
    ErlangRecordRef recordRef = PsiTreeUtil.getParentOfType(erlangElement, ErlangRecordRef.class);
    if (recordRef != null){
      return getContextByRef(reversedQualifiers, recordRef);
    }

    return null;
  }

  @Nullable
  private static ErlFileReferenceContext getContextByRef(List<String> reversedQualifiers, ErlangRecordRef recordRef) {
    reversedQualifiers.add(recordRef.getText());
    PsiElement resolve = recordRef.getReference().resolve();
    if (resolve == null) return null;
    PsiFile resolvedFile = resolve.getContainingFile();
    if (resolvedFile instanceof ErlangFile) {
      return new ErlFileReferenceContext((ErlangFile) resolvedFile, QualifiedName.fromComponents(Lists.reverse(reversedQualifiers)));
    }
    return null;
  }

  ErlangFile getFile() {
    return file;
  }

  QualifiedName getFileLocalSymbol() {
    return fileLocalSymbol;
  }
}
