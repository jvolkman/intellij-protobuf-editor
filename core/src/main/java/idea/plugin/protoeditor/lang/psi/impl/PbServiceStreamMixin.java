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
package idea.plugin.protoeditor.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.util.QualifiedName;
import idea.plugin.protoeditor.lang.descriptor.Descriptor;
import idea.plugin.protoeditor.lang.descriptor.DescriptorOptionType;
import idea.plugin.protoeditor.lang.psi.PbBlockBody;
import idea.plugin.protoeditor.lang.psi.PbMethodOptions;
import idea.plugin.protoeditor.lang.psi.PbOptionStatement;
import idea.plugin.protoeditor.lang.psi.PbServiceStream;
import idea.plugin.protoeditor.lang.psi.util.PbCommentUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

abstract class PbServiceStreamMixin extends PbNamedElementBase implements PbServiceStream {

  PbServiceStreamMixin(ASTNode node) {
    super(node);
  }

  @NotNull
  @Override
  public QualifiedName getDescriptorOptionsTypeName(Descriptor descriptor) {
    return DescriptorOptionType.STREAM_OPTIONS.forDescriptor(descriptor);
  }

  @Override
  @Nullable
  public QualifiedName getExtensionOptionScope() {
    QualifiedName name = getQualifiedName();
    return name != null ? name.removeLastComponent() : null;
  }

  @NotNull
  @Override
  public List<PsiComment> getTrailingComments() {
    PbBlockBody options = getMethodOptions();
    if (options == null) {
      // No options defined; collect comments after the stream statement.
      return PbCommentUtil.collectTrailingComments(this);
    }
    return PbCommentUtil.collectTrailingComments(options.getStart());
  }

  @Override
  @NotNull
  public List<PbOptionStatement> getOptionStatements() {
    PbMethodOptions methodOptions = getMethodOptions();
    if (methodOptions == null) {
      return Collections.emptyList();
    }
    return methodOptions.getOptionStatements();
  }
}
