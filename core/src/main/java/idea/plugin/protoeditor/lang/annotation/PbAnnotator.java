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
package idea.plugin.protoeditor.lang.annotation;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.QualifiedName;
import idea.plugin.protoeditor.ide.highlighter.PbSyntaxHighlighter;
import idea.plugin.protoeditor.lang.PbLangBundle;
import idea.plugin.protoeditor.lang.annotation.OptionOccurrenceTracker.Occurrence;
import idea.plugin.protoeditor.lang.psi.*;
import idea.plugin.protoeditor.lang.psi.PbField.CanonicalFieldLabel;
import idea.plugin.protoeditor.lang.psi.util.PbPsiImplUtil;
import idea.plugin.protoeditor.lang.psi.util.PbPsiUtil;
import idea.plugin.protoeditor.lang.util.BuiltInType;
import idea.plugin.protoeditor.lang.util.ValueTester;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/** General proto element error annotations. */
public class PbAnnotator implements Annotator {

  // Allowable map key types, as defined in descriptor.cc.
  private static final ImmutableSet<BuiltInType> ALLOWED_KEY_TYPES =
      Sets.immutableEnumSet(
          BuiltInType.BOOL,
          BuiltInType.INT32,
          BuiltInType.INT64,
          BuiltInType.SINT32,
          BuiltInType.SINT64,
          BuiltInType.STRING,
          BuiltInType.UINT32,
          BuiltInType.UINT64,
          BuiltInType.FIXED32,
          BuiltInType.FIXED64,
          BuiltInType.SFIXED32,
          BuiltInType.SFIXED64);

  @Override
  public void annotate(PsiElement element, @NotNull final AnnotationHolder holder) {
    element.accept(
        new PbVisitor() {
          @Override
          public void visitSymbolPath(@NotNull PbSymbolPath path) {
            SharedAnnotations.annotateSymbolPath(path, holder);
          }

          @Override
          public void visitImportName(@NotNull PbImportName name) {
            annotateImportName(name, holder);
          }

          @Override
          public void visitOptionName(@NotNull PbOptionName name) {
            annotateOptionName(name, holder);
          }

          @Override
          public void visitTypeName(@NotNull PbTypeName name) {
            annotateTypeName(name, holder);
          }

          @Override
          public void visitMapField(@NotNull PbMapField mapField) {
            annotateMapField(mapField, holder);
            visitField(mapField);
          }

          @Override
          public void visitGroupDefinition(@NotNull PbGroupDefinition group) {
            annotateGroupDefinition(group, holder);
          }

          @Override
          public void visitEnumDefinition(@NotNull PbEnumDefinition enumDefinition) {
            annotateEnumDefinition(enumDefinition, holder);
          }

          @Override
          public void visitEnumValue(@NotNull PbEnumValue value) {
            annotateEnumValue(value, holder);
          }

          @Override
          public void visitEnumReservedStatement(@NotNull PbEnumReservedStatement reservedStatement) {
            annotateEnumReservedStatement(reservedStatement, holder);
          }

          @Override
          public void visitExtendDefinition(@NotNull PbExtendDefinition extendDefinition) {
            annotateExtendDefinition(extendDefinition, holder);
          }

          @Override
          public void visitOneofDefinition(@NotNull PbOneofDefinition oneofDefinition) {
            annotateOneofDefinition(oneofDefinition, holder);
          }

          @Override
          public void visitField(@NotNull PbField field) {
            annotateField(field, holder);
          }

          @Override
          public void visitReservedStatement(@NotNull PbReservedStatement reservedStatement) {
            annotateReservedStatement(reservedStatement, holder);
          }

          @Override
          public void visitExtensionsStatement(@NotNull PbExtensionsStatement extensionsStatement) {
            annotateExtensionsStatement(extensionsStatement, holder);
          }

          @Override
          public void visitOptionExpression(@NotNull PbOptionExpression option) {
            annotateOptionExpression(option, holder);
          }

          @Override
          public void visitStringPart(@NotNull PbStringPart part) {
            SharedAnnotations.annotateStringPart(
                part, holder, PbSyntaxHighlighter.INVALID_STRING_ESCAPE);
          }

          @Override
          public void visitPackageStatement(@NotNull PbPackageStatement packageStatement) {
            annotatePackageStatement(packageStatement, holder);
          }

          @Override
          public void visitElement(PsiElement element) {
            if (ProtoTokenTypes.IDENTIFIER_LITERAL.equals(element.getNode().getElementType())) {
              PsiElement parent = element.getParent();
              if (parent instanceof PbSymbol
                  && element.equals(((PbSymbol) parent).getNameIdentifier())) {
                annotateSymbolName((PbSymbol) parent, holder);
              }
            }
          }
        });
  }

  /** Returns the name annotation element to use for the given symbol. */
  static PsiElement getSymbolNameAnnotationElement(PbSymbol symbol) {
    // Some fields are synthetic (like GroupDefinitionField), so make sure we annotate the
    // non-synthetic element.
    PsiElement navigationElement = symbol.getNavigationElement();
    PsiElement nameIdentifier = null;
    if (navigationElement instanceof PsiNameIdentifierOwner) {
      nameIdentifier = ((PsiNameIdentifierOwner) navigationElement).getNameIdentifier();
    }
    return nameIdentifier != null ? nameIdentifier : navigationElement;
  }

  /** Returns the element to annotate for an option name. */
  private static PsiElement getOptionNameAnnotationElement(PbOptionName name) {
    PbExtensionName extension = name.getExtensionName();
    if (extension != null) {
      return extension.getSymbolPath().getSymbol();
    } else {
      return name.getSymbol();
    }
  }

  private static void annotateOptionExpression(PbOptionExpression option, AnnotationHolder holder) {
    // Handle built-in value types
    annotateBuiltInValue(option, holder);

    // Annotate 'allow_alias' enum option
    EnumTracker.annotateOptionExpression(option, holder);

    PbOptionName name = option.getOptionName();
    PbNamedTypeElement namedType = name.getNamedType();
    if (namedType instanceof PbMessageType) {
      annotateMessageValue(option, name, holder);
    } else if (namedType instanceof PbEnumDefinition) {
      annotateEnumOptionValue(option, holder);
    }
  }

  private static void annotateBuiltInValue(PbOptionExpression option, AnnotationHolder holder) {
    ValueTester tester = option.getOptionName().getBuiltInValueTester();
    if (tester == null) {
      return;
    }
    PbLiteral literal = PsiTreeUtil.getChildOfType(option, PbLiteral.class);
    if (literal != null) {
      String error = tester.testValue(literal);
      if (error != null) {
        holder.createErrorAnnotation(literal.getTextRange(), error);
      }
    }
  }

  private static void annotateMessageValue(
      PbOptionExpression option, PbOptionName name, AnnotationHolder holder) {
    PbLiteral literal = PsiTreeUtil.getChildOfType(option, PbLiteral.class);
    if (literal != null) {
      if (name.getSpecialType() == SpecialOptionType.FIELD_DEFAULT) {
        // Message fields cannot have default values.
        holder.createErrorAnnotation(
            option.getTextRange(), PbLangBundle.message("message.field.cannot.have.default.value"));
      } else if (!(literal instanceof PbAggregateValue)) {
        // Message options must be set to aggregate values.
        holder.createErrorAnnotation(
            literal.getTextRange(), PbLangBundle.message("aggregate.value.expected"));
      }
    }
  }

  private static void annotateEnumOptionValue(PbOptionExpression option, AnnotationHolder holder) {
    PbLiteral literal = PsiTreeUtil.getChildOfType(option, PbLiteral.class);
    if (literal instanceof PbIdentifierValue) {
      SharedAnnotations.annotateEnumOptionValue((PbIdentifierValue) literal, holder);
    } else if (literal != null) {
      holder.createErrorAnnotation(
          literal.getTextRange(), PbLangBundle.message("enum.value.expected"));
    }
  }

  private static void annotateMapField(PbMapField mapField, AnnotationHolder holder) {
    annotateMapFieldKeyType(mapField, holder);
    annotateMapFieldEnumValueType(mapField, holder);
  }

  private static void annotateMapFieldKeyType(PbMapField mapField, AnnotationHolder holder) {
    PbTypeName keyType = mapField.getKeyType();
    if (keyType == null) {
      return;
    }
    BuiltInType keyBuiltInType = keyType.getBuiltInType();
    if (keyBuiltInType == null || !ALLOWED_KEY_TYPES.contains(keyBuiltInType)) {
      holder.createErrorAnnotation(keyType, PbLangBundle.message("map.key.type"));
    }
  }

  private static void annotateMapFieldEnumValueType(PbMapField mapField, AnnotationHolder holder) {
    PbTypeName valueType = mapField.getValueType();
    if (valueType == null) {
      return;
    }
    PbEnumDefinition enumDef =
        PbPsiUtil.resolveRefToType(valueType.getEffectiveReference(), PbEnumDefinition.class);
    if (enumDef == null) {
      return;
    }
    PbEnumValue firstValue = enumDef.getEnumValues().stream().findFirst().orElse(null);
    if (firstValue == null) {
      return;
    }
    PbNumberValue numberValue = firstValue.getNumberValue();
    if (numberValue == null) {
      return;
    }
    Long enumNumber = numberValue.getLongValue();
    if (enumNumber != null && enumNumber != 0) {
      holder.createErrorAnnotation(
          valueType.getSymbolPath().getSymbol(),
          PbLangBundle.message("map.value.first.enum.value.zero"));
    }
  }

  private static void annotateGroupDefinition(
      PbGroupDefinition groupDefinition, AnnotationHolder holder) {
    // Groups are a message type + a field. However, the field is not part of the PSI tree and
    // aren't traversed by the annotator normally. Check the field here.
    for (PbSymbol sibling : groupDefinition.getAdditionalSiblings()) {
      if (sibling instanceof PbField) {
        annotateField((PbField) sibling, holder);
      }
    }
  }

  private static void annotateEnumDefinition(
      PbEnumDefinition enumDefinition, AnnotationHolder holder) {
    PsiElement name = enumDefinition.getNameIdentifier();
    if (name == null) {
      return;
    }
    // Enum must contain at least one value.
    if (enumDefinition.getEnumValues().isEmpty()) {
      TextRange range =
          TextRange.create(
              enumDefinition.getTextRange().getStartOffset(), name.getTextRange().getEndOffset());
      holder.createErrorAnnotation(range, PbLangBundle.message("enum.at.least.one.value"));
    }
  }

  private static void annotateEnumReservedStatement(
      PbEnumReservedStatement reservedStatement, AnnotationHolder holder) {
    EnumTracker.annotateReservedStatement(reservedStatement, holder);
  }

  private static void annotateEnumValue(PbEnumValue value, AnnotationHolder holder) {
    EnumTracker.annotateEnumValue(value, holder);
  }

  private static void annotateExtendDefinition(
      PbExtendDefinition extendDefinition, AnnotationHolder holder) {
    PbTypeName type = extendDefinition.getTypeName();
    if (type == null) {
      return;
    }
    PbExtendBody body = extendDefinition.getBody();
    if (body == null) {
      return;
    }
    // Extend definition must contain at least one field.
    if (body.getGroupDefinitionList().isEmpty() && body.getSimpleFieldList().isEmpty()) {
      TextRange range =
          TextRange.create(
              extendDefinition.getTextRange().getStartOffset(),
              extendDefinition.getTypeName().getTextRange().getEndOffset());
      holder.createErrorAnnotation(range, PbLangBundle.message("extend.at.least.one.field"));
    }
  }

  private static void annotateOneofDefinition(
      PbOneofDefinition oneofDefinition, AnnotationHolder holder) {
    PsiElement name = oneofDefinition.getNameIdentifier();
    if (name == null) {
      return;
    }
    PbOneofBody body = oneofDefinition.getBody();
    if (body == null) {
      return;
    }
    // Oneof must contain at least one field.
    if (body.getGroupDefinitionList().isEmpty() && body.getSimpleFieldList().isEmpty()) {
      TextRange range =
          TextRange.create(
              oneofDefinition.getTextRange().getStartOffset(), name.getTextRange().getEndOffset());
      holder.createErrorAnnotation(range, PbLangBundle.message("oneof.at.least.one.field"));
    }
  }

  private static void annotateReservedStatement(
      PbReservedStatement reservedStatement, AnnotationHolder holder) {
    MessageFieldTracker.annotateReservedStatement(reservedStatement, holder);
  }

  private static void annotateExtensionsStatement(
      PbExtensionsStatement extensionsStatement, AnnotationHolder holder) {
    MessageFieldTracker.annotateExtensionsStatement(extensionsStatement, holder);
  }

  private static void annotateField(PbField field, AnnotationHolder holder) {
    annotateInvalidFieldInMessageSet(field, holder);
    annotateMessageSetExtensionField(field, holder);
    annotateFieldLabel(field.getDeclaredLabel(), holder);
    MessageFieldTracker.annotateField(field, holder);
  }

  private static void annotateInvalidFieldInMessageSet(PbField field, AnnotationHolder holder) {
    if (field.isExtension()) {
      return;
    }
    PbSymbolOwner owner = field.getSymbolOwner();
    if (!(owner instanceof PbMessageType)) {
      return;
    }
    if (!((PbMessageType) owner).isMessageSet()) {
      return;
    }
    holder.createErrorAnnotation(field, PbLangBundle.message("message.set.fields"));
  }

  private static void annotateMessageSetExtensionField(PbField field, AnnotationHolder holder) {
    PbTypeName extendeeType = field.getExtendee();
    if (extendeeType == null) {
      return;
    }
    PbMessageType extendee =
        PbPsiUtil.resolveRefToType(extendeeType.getEffectiveReference(), PbMessageType.class);
    if (extendee == null || !extendee.isMessageSet()) {
      return;
    }
    // Label must be `optional`.
    PbFieldLabel label = field.getDeclaredLabel();
    if (label != null) {
      if (field.getCanonicalLabel() != CanonicalFieldLabel.OPTIONAL) {
        holder.createErrorAnnotation(
            label, PbLangBundle.message("message.set.extensions.optional.messages"));
      }
    }
    // Type must be a message.
    PbTypeName type = field.getTypeName();
    if (type != null
        && PbPsiUtil.resolveRefToType(type.getEffectiveReference(), PbMessageType.class) == null) {
      holder.createErrorAnnotation(
          type, PbLangBundle.message("message.set.extensions.optional.messages"));
    }
  }

  private static void annotateFieldLabel(PbFieldLabel label, AnnotationHolder holder) {
    if (label == null) {
      return;
    }
    PbStatement statement = PsiTreeUtil.getParentOfType(label, PbStatement.class);
    if (statement == null) {
      return;
    }
    if (statement instanceof PbMapField) {
      holder.createErrorAnnotation(label, PbLangBundle.message("map.fields.cannot.have.labels"));
    } else if (statement.getStatementOwner() instanceof PbOneofDefinition) {
      holder.createErrorAnnotation(label, PbLangBundle.message("oneof.fields.cannot.have.labels"));
    }
  }

  private static void annotateOptionName(PbOptionName name, AnnotationHolder holder) {
    PbExtensionName extensionName = name.getExtensionName();
    if (extensionName != null) {
      // This is an extension option (e.g., "(custom_option)").
      annotateExtensionName(extensionName, name, holder);
    } else {
      // This is a normal field name.
      annotateOptionNameReference(name, holder);
    }
    annotateRepeatedMessageFieldInitialization(name, holder);
    annotateOptionOccurrences(name, holder);
    annotateSpecialOption(name, holder);
  }

  private static void annotateExtensionName(
      PbExtensionName name, PbOptionName option, AnnotationHolder holder) {
    PbSymbolPath path = name.getSymbolPath();
    SharedAnnotations.annotateExtensionName(
        path,
        path.getReference(),
        name.getReferenceString(),
        option.getQualifierType(),
        holder,
        /* allowMembers= */ true);
  }

  private static void annotateOptionNameReference(PbOptionName name, AnnotationHolder holder) {
    PsiElement symbol = name.getSymbol();
    switch (SharedAnnotations.getReferenceState(name.getEffectiveReference())) {
      case VALID:
      case NULL:
        break;
      case UNRESOLVED:
      default:
        TextRange range = symbol != null ? symbol.getTextRange() : name.getTextRange();
        String symbolName = symbol != null ? symbol.getText() : name.getText();
        holder
            .createErrorAnnotation(range, PbLangBundle.message("cannot.resolve.option", symbolName))
            .setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
    }
  }

  private static void annotateTypeName(PbTypeName name, AnnotationHolder holder) {
    // A type name must be either a built-in type or a NamedTypeElement (message or enum).
    if (name instanceof PbMessageTypeName) {
      // Must be a message type.
      if (PbPsiUtil.resolveRefToType(name.getEffectiveReference(), PbMessageType.class) == null) {
        holder.createErrorAnnotation(
            name.getSymbolPath().getSymbol(), PbLangBundle.message("message.type.expected"));
      }
    } else {
      // If it's not a built-in type, it must be a message or enum.
      if (!name.isBuiltInType()
          && PbPsiUtil.resolveRefToType(name.getEffectiveReference(), PbNamedTypeElement.class)
              == null) {
        holder.createErrorAnnotation(
            name.getSymbolPath().getSymbol(),
            PbLangBundle.message("message.enum.or.builtin.type.expected"));
      }
    }
  }

  // Annotate a repeated message field option used as a qualifier name.
  // For example, in "option (myopt).foo.bar = 3;", "foo" would be given an error annotation if
  // it referred to a repeated message. Repeated messages must be initialized using aggregate
  // values: "option (myopt).foo = { bar: 3 };"
  private static void annotateRepeatedMessageFieldInitialization(
      PbOptionName name, AnnotationHolder holder) {

    if (!(name.getParent() instanceof PbOptionName)) {
      // This check only applies to qualifier names. E.g., "foo" and "bar" in "foo.bar.baz".
      return;
    }

    PbField field = PbPsiUtil.resolveRefToType(name.getEffectiveReference(), PbField.class);
    if (field == null) {
      return;
    }

    PbNamedTypeElement type = resolveType(field);
    if (type instanceof PbMessageType && field.isRepeated()) {
      // Repeated message fields must be defined using aggregate syntax. So, a repeated message
      // field cannot be a qualifier.
      holder.createErrorAnnotation(
          getOptionNameAnnotationElement(name),
          PbLangBundle.message("repeated.message.aggregate.value", field.getName()));
    }
  }

  private static void annotateSpecialOption(PbOptionName name, AnnotationHolder holder) {
    SpecialOptionType specialType = name.getSpecialType();
    if (specialType == null) {
      return;
    }

    // Ensure this special type hasn't been specified previously.
    PbOptionOwner owner = PsiTreeUtil.getParentOfType(name, PbOptionOwner.class);
    if (owner != null) {
      SpecialOptionTracker.forOptionOwner(owner).annotateOptionName(name, holder);
    }

    // Repeated fields cannot have default values.
    if (specialType == SpecialOptionType.FIELD_DEFAULT) {
      PbField parent = PsiTreeUtil.getParentOfType(name, PbField.class);
      if (parent != null && parent.isRepeated()) {
        holder.createErrorAnnotation(
            getOptionNameAnnotationElement(name),
            PbLangBundle.message("repeated.field.default.value"));
      }
    }

    // 'json_name' is not permitted on extension fields.
    if (specialType == SpecialOptionType.FIELD_JSON_NAME) {
      PbField parentField = PsiTreeUtil.getParentOfType(name, PbField.class);
      if (parentField != null && parentField.isExtension()) {
        holder.createErrorAnnotation(
            name, PbLangBundle.message("extension.field.json.name.not.allowed"));
      }
    }
  }

  private static void annotateImportName(PbImportName name, AnnotationHolder holder) {
    switch (SharedAnnotations.getReferenceState(name.getReference())) {
      case AMBIGUOUS:
        holder.createErrorAnnotation(
            name.getStringValue().getTextRangeNoQuotes(),
            PbLangBundle.message("ambiguous.import", name.getStringValue().getAsString()));
        break;
      case VALID:
      case NULL:
        break;
      case UNRESOLVED:
      default:
        holder
            .createErrorAnnotation(
                name.getStringValue().getTextRangeNoQuotes(),
                PbLangBundle.message("cannot.resolve.import", name.getStringValue().getAsString()))
            .setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
    }
  }

  private static void annotateOptionOccurrences(PbOptionName name, AnnotationHolder holder) {
    PbOptionOwner owner = PbPsiImplUtil.getOptionOwner(name);
    if (owner == null) {
      return;
    }

    OptionOccurrenceTracker tracker = OptionOccurrenceTracker.forOptionOwner(owner);
    Occurrence occurrence = tracker.getOccurrence(name);
    if (occurrence != null) {
      occurrence.annotate(holder, getOptionNameAnnotationElement(name));
    }
  }

  private static void annotateSymbolName(PbSymbol symbol, AnnotationHolder holder) {
    PbFile file = symbol.getPbFile();
    if (file == null) {
      return;
    }
    QualifiedName qualifiedName = symbol.getQualifiedName();
    if (qualifiedName == null) {
      return;
    }

    Annotation createdAnnotation = createConflictAnnotation(file, symbol, qualifiedName, holder);
    if (createdAnnotation == null && symbol instanceof PbSymbolContributor) {
      // No conflicts as defined in source, but let's make sure none of the contributed symbols
      // conflict as well.
      for (PbSymbol contributed : ((PbSymbolContributor) symbol).getAdditionalSiblings()) {
        annotateSymbolName(contributed, holder);
      }
    }
  }

  private static void annotatePackageStatement(
      PbPackageStatement packageStatement, AnnotationHolder holder) {
    PbFile file = packageStatement.getPbFile();
    if (file == null) {
      return;
    }
    PbPackageStatement firstPackageStatement = file.getPackageStatement();
    if (firstPackageStatement == null) {
      return;
    }
    if (!packageStatement.equals(firstPackageStatement)) {
      holder.createErrorAnnotation(
          packageStatement, PbLangBundle.message("duplicate.package.statement"));
    }
  }

  private static Annotation createConflictAnnotation(
      PbFile file, PbSymbol symbol, QualifiedName qualifiedName, AnnotationHolder holder) {
    PsiElement annotationElement = getSymbolNameAnnotationElement(symbol);
    int symbolOffset = annotationElement.getTextRange().getStartOffset();
    Set<PbFile> conflictFiles = new HashSet<>();

    // Iterate over all of the symbols for this qualified name looking for one to use as the first
    // definition, in this order of preference:
    // * a symbol in the same file at a smaller text offset
    // * a symbol in a different file
    ImmutableCollection<PbSymbol> symbols = file.getFullQualifiedSymbolMap().get(qualifiedName);
    for (PbSymbol otherSymbol : symbols) {
      if (symbol.equals(otherSymbol)) {
        continue;
      }
      if (symbol instanceof PbPackageName && otherSymbol instanceof PbPackageName) {
        // Two package names don't conflict with each other.
        continue;
      }
      PbFile otherFile = otherSymbol.getPbFile();
      if (otherFile == null) {
        continue;
      }
      if (file.equals(otherFile)) {
        PsiElement otherAnnotationElement = getSymbolNameAnnotationElement(otherSymbol);
        if (otherAnnotationElement.getTextRange().getStartOffset() < symbolOffset) {
          return holder.createErrorAnnotation(
              annotationElement,
              PbLangBundle.message(
                  "symbol.already.defined.in.scope",
                  qualifiedName.getLastComponent(),
                  qualifiedName.removeLastComponent().toString()));
        }
      } else {
        conflictFiles.add(otherFile);
      }
    }
    if (!conflictFiles.isEmpty()) {
      // Figure out the import path (as written in the import statement) for the first-listed
      // conflicting import.
      String importName = getFirstImportName(file, conflictFiles);
      return holder.createErrorAnnotation(
          annotationElement,
          PbLangBundle.message(
              "symbol.already.defined.in.other.file", qualifiedName.toString(), importName));
    }
    return null;
  }

  private static String getFirstImportName(PbFile file, Set<PbFile> conflictFiles) {
    for (PbImportStatement importStatement : file.getImportStatements()) {
      PbImportName importName = importStatement.getImportName();
      if (importName == null) {
        continue;
      }
      PbFile resolved = PbPsiUtil.resolveRefToType(importName.getReference(), PbFile.class);
      if (resolved == null) {
        continue;
      }
      if (conflictFiles.contains(resolved)) {
        return importName.getStringValue().getAsString();
      }
    }
    return null;
  }

  private static PbNamedTypeElement resolveType(PbField field) {
    PbTypeName typeName = field.getTypeName();
    if (typeName == null) {
      return null;
    }
    return PbPsiUtil.resolveRefToType(typeName.getEffectiveReference(), PbNamedTypeElement.class);
  }
}
