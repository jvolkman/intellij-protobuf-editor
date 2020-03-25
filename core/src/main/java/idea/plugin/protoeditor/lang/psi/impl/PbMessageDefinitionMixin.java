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
import com.intellij.psi.stubs.IStubElementType;
import idea.plugin.protoeditor.ide.util.PbIcons;
import idea.plugin.protoeditor.lang.psi.PbMessageDefinition;
import idea.plugin.protoeditor.lang.stub.PbMessageDefinitionStub;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

abstract class PbMessageDefinitionMixin extends PbStubbedSymbolOwnerBase<PbMessageDefinitionStub>
    implements PbMessageDefinition {

  PbMessageDefinitionMixin(ASTNode node) {
    super(node);
  }

  PbMessageDefinitionMixin(PbMessageDefinitionStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @Nullable
  @Override
  public Icon getIcon(int flags) {
    return PbIcons.MESSAGE;
  }
}
