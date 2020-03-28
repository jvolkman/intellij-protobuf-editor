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
package idea.plugin.protoeditor.lang.findusages;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import idea.plugin.protoeditor.lang.PbLangBundle;
import idea.plugin.protoeditor.lang.psi.*;
import org.jetbrains.annotations.Nullable;

/**
 * Classify some usages within .proto files. Otherwise, they are treated as "unclassified" and could
 * get lost in a sea of unclassified generated code usages.
 */
public class PbUsageTypeProvider implements UsageTypeProvider {

  static final UsageType EXTEND_DEFINITION =
      new UsageType(() -> PbLangBundle.message("usage.extend.type.reference"));
  static final UsageType FIELD_DECLARATION =
      new UsageType(() -> PbLangBundle.message("usage.field.type.reference"));
  static final UsageType OPTION_EXPRESSION =
      new UsageType(() -> PbLangBundle.message("usage.option.expr.reference"));
  static final UsageType SERVICE_TYPE =
      new UsageType(() -> PbLangBundle.message("usage.service.type.reference"));

  @Nullable
  @Override
  public UsageType getUsageType(PsiElement element) {
    PbTypeName typeParent = PsiTreeUtil.getParentOfType(element, PbTypeName.class);
    if (typeParent != null) {
      if (PsiTreeUtil.getParentOfType(typeParent, PbField.class) != null) {
        return FIELD_DECLARATION;
      }
      PbDefinition owner = PsiTreeUtil.getParentOfType(typeParent, PbDefinition.class);
      if (owner instanceof PbExtendDefinition) {
        return EXTEND_DEFINITION;
      }
      if (owner instanceof PbServiceDefinition) {
        return SERVICE_TYPE;
      }
    }
    if (PsiTreeUtil.getParentOfType(element, PbOptionExpression.class) != null) {
      return OPTION_EXPRESSION;
    }
    return null;
  }
}
