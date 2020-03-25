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
package idea.plugin.protoeditor.ide.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import idea.plugin.protoeditor.ide.util.PbIcons;
import idea.plugin.protoeditor.ide.util.ResourceUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

/** A {@link ColorSettingsPage} for standalone prototext files. */
public class PbTextColorSettingsPage implements ColorSettingsPage {

  private static final AttributesDescriptor[] DESCRIPTORS =
      new AttributesDescriptor[] {
        new AttributesDescriptor("Identifier", PbTextSyntaxHighlighter.IDENTIFIER),
        new AttributesDescriptor("Keyword", PbTextSyntaxHighlighter.KEYWORD),
        new AttributesDescriptor("Number", PbTextSyntaxHighlighter.NUMBER),
        new AttributesDescriptor("String", PbTextSyntaxHighlighter.STRING),
        new AttributesDescriptor("Enum value", PbTextSyntaxHighlighter.ENUM_VALUE),
        new AttributesDescriptor("Line comment", PbTextSyntaxHighlighter.LINE_COMMENT),
        new AttributesDescriptor("Operator", PbTextSyntaxHighlighter.OPERATION_SIGN),
        new AttributesDescriptor("Braces", PbTextSyntaxHighlighter.BRACES),
        new AttributesDescriptor("Brackets", PbTextSyntaxHighlighter.BRACKETS),
        new AttributesDescriptor("Dot", PbTextSyntaxHighlighter.DOT),
        new AttributesDescriptor("Semicolon", PbTextSyntaxHighlighter.SEMICOLON),
        new AttributesDescriptor("Comma", PbTextSyntaxHighlighter.COMMA),
        new AttributesDescriptor(
            "Valid escape sequence", PbTextSyntaxHighlighter.VALID_STRING_ESCAPE),
        new AttributesDescriptor(
            "Invalid escape sequence", PbTextSyntaxHighlighter.INVALID_STRING_ESCAPE),
        new AttributesDescriptor("Comment directive", PbTextSyntaxHighlighter.COMMENT_DIRECTIVE),
      };

  @Nullable
  @Override
  public Icon getIcon() {
    return PbIcons.TEXT_FILE;
  }

  @NotNull
  @Override
  public SyntaxHighlighter getHighlighter() {
    return new PbTextSyntaxHighlighter();
  }

  @NotNull
  @Override
  public String getDemoText() {
    try {
      return ResourceUtil.readUrlAsString(getClass().getResource("/example.pb"));
    } catch (IOException e) {
      return "Error loading example.";
    }
  }

  @Nullable
  @Override
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return null;
  }

  @NotNull
  @Override
  public AttributesDescriptor[] getAttributeDescriptors() {
    return DESCRIPTORS;
  }

  @NotNull
  @Override
  public ColorDescriptor[] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Protocol Buffer Text";
  }
}
