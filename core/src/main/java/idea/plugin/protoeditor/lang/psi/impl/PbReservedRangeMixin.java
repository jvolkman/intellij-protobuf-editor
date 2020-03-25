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
import idea.plugin.protoeditor.lang.psi.PbField;
import idea.plugin.protoeditor.lang.psi.PbNumberValue;
import idea.plugin.protoeditor.lang.psi.PbReservedRange;
import idea.plugin.protoeditor.lang.psi.ProtoTokenTypes;
import org.jetbrains.annotations.Nullable;

abstract class PbReservedRangeMixin extends PbElementBase implements PbReservedRange {

  PbReservedRangeMixin(ASTNode node) {
    super(node);
  }

  @Nullable
  @Override
  public Long getFrom() {
    return getFromValue().getLongValue();
  }

  @Nullable
  @Override
  public Long getTo() {
    if (getNode().findChildByType(ProtoTokenTypes.MAX) != null) {
      return PbField.MAX_FIELD_NUMBER;
    }
    PbNumberValue toValue = getToValue();
    return toValue != null ? toValue.getLongValue() : null;
  }
}
