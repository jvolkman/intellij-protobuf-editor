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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import idea.plugin.protoeditor.lang.psi.ProtoSymbolPath;
import idea.plugin.protoeditor.lang.psi.ProtoSymbolPathContainer;
import idea.plugin.protoeditor.lang.psi.ProtoSymbolPathDelegate;
import idea.plugin.protoeditor.lang.psi.ProtoTokenTypes;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PbSymbolPathMixin extends PbElementBase implements ProtoSymbolPath {

  PbSymbolPathMixin(ASTNode node) {
    super(node);
  }

  @NotNull
  @Override
  public PsiElement getSymbol() {
    return findNotNullChildByType(ProtoTokenTypes.IDENTIFIER_LITERAL);
  }

  @Override
  public int getTextOffset() {
    PsiElement name = getNameIdentifier();
    return name != null ? name.getTextOffset() : super.getTextOffset();
  }

  @Nullable
  @Override
  public PsiReference getReference() {
    ProtoSymbolPathDelegate delegate = getPathDelegate();
    if (delegate != null) {
      return delegate.getReference(this);
    }
    return null;
  }

  @Nullable
  @Override
  public PsiElement getNameIdentifier() {
    ProtoSymbolPathDelegate delegate = getPathDelegate();
    if (delegate != null) {
      return delegate.getNameIdentifier(this);
    }
    return null;
  }

  @Nullable
  @Override
  public String getName() {
    ProtoSymbolPathDelegate delegate = getPathDelegate();
    if (delegate != null) {
      return delegate.getName(this);
    }
    return null;
  }

  @Nullable
  @Override
  public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
    ProtoSymbolPathDelegate delegate = getPathDelegate();
    if (delegate != null) {
      return delegate.setName(this, name);
    }
    throw new IncorrectOperationException();
  }

  @Nullable
  private ProtoSymbolPathDelegate getPathDelegate() {
    ProtoSymbolPathContainer container = getPathContainer();
    if (container != null) {
      return container.getPathDelegate();
    }
    return null;
  }
}
