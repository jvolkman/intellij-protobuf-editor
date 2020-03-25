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

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import idea.plugin.protoeditor.lang.psi.PbElement;
import idea.plugin.protoeditor.lang.psi.PbFile;
import idea.plugin.protoeditor.lang.stub.PbElementStub;
import org.jetbrains.annotations.NotNull;

abstract class PbStubbedElementBase<T extends PbElementStub<?>> extends StubBasedPsiElementBase<T>
    implements PbElement, PbOverridableElement, StubBasedPsiElement<T> {

  PbStubbedElementBase(ASTNode node) {
    super(node);
  }

  PbStubbedElementBase(T stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @Override
  public PbFile getPbFile() {
    // In the case of elements generated by PbElementFactory, getContainingFile returns an instance
    // of DummyHolder, but getContainingFile().getOriginalFile() returns the original PbFile.
    // For physical files, getOriginalFile() returns itself.
    return (PbFile) getContainingFile().getOriginalFile();
  }

  @Override
  public PsiElement getParent() {
    PsiElement override = getParentOverride();
    return override != null ? override : super.getParent();
  }

  @NotNull
  @Override
  public PsiElement getNavigationElement() {
    PsiElement override = getNavigationElementOverride();
    return override != null ? override : super.getNavigationElement();
  }

  @Override
  public TextRange getTextRange() {
    TextRange override = getTextRangeOverride();
    return override != null ? override : super.getTextRange();
  }

  @Override
  public String toString() {
    return String.format("%s(%s)", getClass().getSimpleName(), getElementType());
  }
}
