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
package idea.plugin.protoeditor.lang.resolve.directive;

import com.google.common.collect.ImmutableList;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import idea.plugin.protoeditor.lang.psi.PbFile;
import idea.plugin.protoeditor.lang.psi.PbTypeName;
import idea.plugin.protoeditor.lang.psi.ProtoSymbolPath;
import idea.plugin.protoeditor.lang.psi.ProtoSymbolPathDelegate;
import idea.plugin.protoeditor.lang.psi.impl.PbElementFactory;
import idea.plugin.protoeditor.lang.resolve.PbSymbolLookupElement;
import idea.plugin.protoeditor.lang.resolve.PbSymbolResolver;
import idea.plugin.protoeditor.lang.resolve.ProtoSymbolPathReference;
import idea.plugin.protoeditor.lang.resolve.ResolveFilters;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class MessageComment extends SchemaComment {

  private SchemaComment fileComment;

  MessageComment(PsiComment comment, TextRange keyRange, TextRange nameRange) {
    super(comment, keyRange, nameRange, Type.MESSAGE);
  }

  void setFileComment(SchemaComment comment) {
    this.fileComment = comment;
  }

  @Nullable
  @Override
  public PsiReference getReference() {
    List<PsiReference> references = getAllReferences();
    if (references.isEmpty()) {
      return null;
    }
    // The first reference in the list should be the fully-qualified message reference.
    return references.get(0);
  }

  @Override
  public List<PsiReference> getAllReferences() {
    if (fileComment == null || getNameRange() == null || getComment() == null) {
      return ImmutableList.of();
    }
    PsiReference fileReference = fileComment.getReference();
    if (fileReference == null) {
      return ImmutableList.of();
    }
    String name = getName();
    if (StringUtil.isEmptyOrSpaces(name)) {
      return ImmutableList.of();
    }
    PsiElement file = fileReference.resolve();
    if (!(file instanceof PbFile)) {
      return ImmutableList.of();
    }

    // First, parse the symbol path into its standard recursive form.
    PbFile pbFile = (PbFile) file;
    PbTypeName parsedType =
        PbElementFactory.getInstance(pbFile)
            .typeNameBuilder()
            .setParent(pbFile)
            .setName(name)
            .build();
    parsedType.setDelegateOverride(
        new ProtoSymbolPathDelegate() {
          @Override
          public PsiReference getReference(ProtoSymbolPath path) {
            // The factory-created typename should start at position 0. To get the position in the
            // text format comment, we just need to shift the symbol range to the right.
            TextRange symbolRange =
                path.getSymbol().getTextRange().shiftRight(getNameRange().getStartOffset());
            return new ProtoSymbolPathReference(
                getComment(),
                symbolRange,
                path,
                PbSymbolResolver.forFileExports(pbFile),
                parsedType.isFullyQualified()
                    ? null
                    : pbFile.getPrimarySymbolOwner().getChildScope(),
                ResolveFilters.packageOrMessage(),
                ResolveFilters.packageOrMessage(),
                PbSymbolLookupElement::new);
          }
        });

    // Now collect all of the references in the recursive path structure.
    ImmutableList.Builder<PsiReference> references = ImmutableList.builder();
    ProtoSymbolPath path = parsedType.getSymbolPath();
    while (path != null) {
      PsiReference ref = path.getReference();
      if (ref != null) {
        references.add(ref);
      }
      path = path.getQualifier();
    }
    return references.build();
  }
}
