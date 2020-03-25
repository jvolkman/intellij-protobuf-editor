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
package idea.plugin.protoeditor.ide.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import idea.plugin.protoeditor.lang.PbTextLanguage;
import idea.plugin.protoeditor.lang.psi.ProtoTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A {@link FormattingModelBuilder} for prototext files. */
public class PbTextFormattingModelBuilder implements FormattingModelBuilder {

  @NotNull
  @Override
  public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
    PsiFile file = element.getContainingFile();
    return FormattingModelProvider.createFormattingModelForPsiFile(
        file,
        new PbTextBlock(
            element.getNode(),
            Wrap.createWrap(WrapType.NONE, false),
            /* alignment= */ null,
            createSpaceBuilder(settings)),
        settings);
  }

  private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
    CommonCodeStyleSettings commonSettings = settings.getCommonSettings(PbTextLanguage.INSTANCE);
    return new SpacingBuilder(commonSettings)
        .withinPair(ProtoTokenTypes.LBRACE, ProtoTokenTypes.RBRACE)
        .spaceIf(commonSettings.SPACE_WITHIN_BRACES, false)
        .withinPair(ProtoTokenTypes.LT, ProtoTokenTypes.GT)
        .spaceIf(commonSettings.SPACE_WITHIN_BRACES, false)
        .withinPair(ProtoTokenTypes.LBRACK, ProtoTokenTypes.RBRACK)
        .spaceIf(commonSettings.SPACE_WITHIN_BRACKETS, false)
        .before(ProtoTokenTypes.COMMA)
        .spaceIf(commonSettings.SPACE_BEFORE_COMMA)
        .after(ProtoTokenTypes.COMMA)
        .spaceIf(commonSettings.SPACE_AFTER_COMMA)
        .before(ProtoTokenTypes.COLON)
        .spaceIf(commonSettings.SPACE_BEFORE_COLON)
        .after(ProtoTokenTypes.COLON)
        .spaceIf(commonSettings.SPACE_AFTER_COLON);
  }

  @Nullable
  @Override
  public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
    return null;
  }
}
