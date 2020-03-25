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
package idea.plugin.protoeditor.python;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.QualifiedName;
import com.jetbrains.python.PythonLanguage;
import com.jetbrains.python.psi.PyFile;
import idea.plugin.protoeditor.gencode.ProtoFromSourceComments;
import idea.plugin.protoeditor.lang.psi.PbElement;
import idea.plugin.protoeditor.lang.psi.PbFile;
import idea.plugin.protoeditor.lang.psi.PbSymbol;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/** Handles goto declaration from python generated code -> .proto files. */
public final class PbPythonGotoDeclarationHandler implements GotoDeclarationHandler {

  @Nullable
  @Override
  public String getActionText(DataContext context) {
    return null;
  }

  @Nullable
  @Override
  public PsiElement[] getGotoDeclarationTargets(
      @Nullable PsiElement sourceElement, int offset, Editor editor) {
    if (sourceElement == null) {
      return null;
    }
    if (!sourceElement.getLanguage().is(PythonLanguage.INSTANCE)) {
      return null;
    }
    PyFileReferenceContext context = PyFileReferenceContext.findContext(sourceElement);
    if (context == null) {
      return null;
    }
    Collection<? extends PbElement> matches = pythonToProto(context);
    if (matches.isEmpty()) {
      return null;
    }
    return matches.toArray(new PsiElement[0]);
  }

  private static ImmutableCollection<? extends PbElement> pythonToProto(
      PyFileReferenceContext referenceContext) {
    PyFile file = referenceContext.getFile();
    String fileName = file.getName();
    Integer apiVersion = null;
    PbFile protoSource = null;
    if (fileName.endsWith("_pb2.py")) {
      protoSource = findSourceOfGeneratedPy(file);
      apiVersion = 2;
    } else if (fileName.endsWith("_pb.py")) {
      protoSource = findSourceOfGeneratedPy(file);
      apiVersion = 1;
    }
    if (protoSource == null) {
      return ImmutableList.of();
    }
    ImmutableCollection<? extends PbElement> results =
        locateSymbolInProtoFile(protoSource, referenceContext.getFileLocalSymbol());
    if (results.isEmpty() && apiVersion == 1) {
      return locateWithNormalizedNames(protoSource, referenceContext.getFileLocalSymbol());
    }
    return results;
  }

  @Nullable
  private static PbFile findSourceOfGeneratedPy(PyFile file) {
    return ProtoFromSourceComments.findProtoOfGeneratedCode("#", file);
  }

  private static ImmutableCollection<? extends PbElement> locateSymbolInProtoFile(
      PbFile pbFile, QualifiedName fileLocalSymbol) {
    if (fileLocalSymbol.getComponents().isEmpty()) {
      return ImmutableList.of(pbFile);
    }
    QualifiedName qualifiedName = pbFile.getPackageQualifiedName().append(fileLocalSymbol);
    Multimap<QualifiedName, PbSymbol> fileSymbols = pbFile.getLocalQualifiedSymbolMap();
    return ImmutableList.copyOf(fileSymbols.get(qualifiedName));
  }

  // For API v1, the code generator converts nested messages like Foo.Bar.Baz to Foo_Bar_Baz.
  // Try to match with the '.' separators normalized to '_'.
  private static ImmutableCollection<? extends PbElement> locateWithNormalizedNames(
      PbFile pbFile, QualifiedName fileLocalName) {
    String fileLocalSymbol = fileLocalName.toString();
    // Should have been an exact match if '_' didn't come into play.
    if (!fileLocalSymbol.contains("_")) {
      return ImmutableList.of();
    }
    String desiredSymbol = fileLocalSymbol.replace('.', '_');
    Multimap<QualifiedName, PbSymbol> fileSymbols = pbFile.getLocalQualifiedSymbolMap();
    int numPackageComponents = pbFile.getPackageQualifiedName().getComponentCount();
    List<PbSymbol> matches =
        fileSymbols
            .entries()
            .stream()
            .filter(
                entry -> {
                  QualifiedName qualifiedName = entry.getKey();
                  String candidateLastComponent = qualifiedName.getLastComponent();
                  if (candidateLastComponent == null
                      || !desiredSymbol.endsWith(candidateLastComponent)) {
                    return false;
                  }
                  return qualifiedName
                      .removeHead(numPackageComponents)
                      .join("_")
                      .equals(desiredSymbol);
                })
            .map(Map.Entry::getValue)
            .collect(toList());
    return ImmutableList.copyOf(matches);
  }
}
