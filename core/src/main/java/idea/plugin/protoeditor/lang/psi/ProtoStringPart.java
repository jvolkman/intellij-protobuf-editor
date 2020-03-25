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
package idea.plugin.protoeditor.lang.psi;

import com.google.common.collect.ImmutableList;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import idea.plugin.protoeditor.lang.psi.util.PbPsiImplUtil;
import idea.plugin.protoeditor.lang.util.ProtoString;
import org.jetbrains.annotations.NotNull;

/** A shared interface implemented by elements that represent a proto-formatted string part. */
public interface ProtoStringPart extends PsiElement {

  @NotNull
  default ProtoString getParsedString() {
    return CachedValuesManager.getCachedValue(
        this,
        () ->
            Result.create(ProtoString.parse(getText()), PsiModificationTracker.MODIFICATION_COUNT));
  }

  default boolean isUnterminated() {
    return getParsedString().isUnterminated();
  }

  default ImmutableList<TextRange> getInvalidEscapeRanges() {
    return getParsedString().getInvalidEscapeRanges();
  }

  @NotNull
  default TextRange getTextRangeNoQuotes() {
    return PbPsiImplUtil.getTextRangeNoQuotes(this);
  }
}
