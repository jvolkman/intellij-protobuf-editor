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
package com.google.devtools.intellij.protoeditor.lang;

import com.intellij.openapi.extensions.ExtensionPointName;

/**
 * Configures protobuf support to be on/off without fully enabling/disabling the parent plugin
 * (useful if this code is merged into another plugin).
 */
public interface ProtoEditorConfigurer {

  ExtensionPointName<ProtoEditorConfigurer> EP_NAME =
      ExtensionPointName.create("com.google.devtools.intellij.protoeditor.configurer");

  /** Return true if the extension wants protoeditor support to remain enabled */
  boolean shouldRemainEnabled();

  static boolean protoEditorEnabled() {
    for (ProtoEditorConfigurer configurer : EP_NAME.getExtensions()) {
      if (!configurer.shouldRemainEnabled()) {
        return false;
      }
    }
    return true;
  }
}
