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
package idea.plugin.protoeditor.lang;

import com.intellij.openapi.fileTypes.ExtensionFileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.fileTypes.WildcardFileNameMatcher;
import org.jetbrains.annotations.NotNull;

/** {@link FileTypeFactory} for prototext files. */
public class PbTextFileTypeFactory extends FileTypeFactory {

  @Override
  public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
    if (ProtoEditorConfigurer.protoEditorEnabled()) {
      // There's no single standard filename extension. These are some of the most common, as seen
      // in existing files.
      fileTypeConsumer.consume(
          PbTextFileType.INSTANCE,
          new ExtensionFileNameMatcher("pb"),
          new ExtensionFileNameMatcher("textpb"),
          new ExtensionFileNameMatcher("textproto"),
          new ExtensionFileNameMatcher("pbtxt"),
          new ExtensionFileNameMatcher("prototext"),
          new ExtensionFileNameMatcher("asciipb"),
          new ExtensionFileNameMatcher("ascii"),
          new WildcardFileNameMatcher("*.pb.txt") // Wildcard due to two dots in the ext.
          );
    }
  }
}
