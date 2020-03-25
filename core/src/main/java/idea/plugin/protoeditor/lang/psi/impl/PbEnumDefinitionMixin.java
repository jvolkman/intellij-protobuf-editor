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

import com.google.common.collect.Multimap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import idea.plugin.protoeditor.ide.util.PbIcons;
import idea.plugin.protoeditor.lang.psi.PbEnumDefinition;
import idea.plugin.protoeditor.lang.psi.PbEnumValue;
import idea.plugin.protoeditor.lang.psi.util.PbPsiImplUtil;
import idea.plugin.protoeditor.lang.stub.PbEnumDefinitionStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

abstract class PbEnumDefinitionMixin extends PbStubbedNamedDefinitionBase<PbEnumDefinitionStub>
    implements PbEnumDefinition {

  PbEnumDefinitionMixin(ASTNode node) {
    super(node);
  }

  PbEnumDefinitionMixin(PbEnumDefinitionStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @NotNull
  @Override
  public Multimap<String, PbEnumValue> getEnumValueMap() {
    return PbPsiImplUtil.getCachedEnumValueMap(this);
  }

  @Nullable
  @Override
  public Icon getIcon(int flags) {
    return PbIcons.ENUM;
  }
}
