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
package idea.plugin.protoeditor.golang;

import com.goide.project.GoRootsProvider;
import com.google.common.collect.ImmutableList;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.ex.temp.TempFileSystem;

import javax.annotation.Nullable;
import java.util.Collection;

/** A {@link GoRootsProvider} for unit testing. */
public class GoProtoTestRootsProvider implements GoRootsProvider {
  @Override
  public Collection<VirtualFile> getGoPathRoots(
      @Nullable Project project, @Nullable Module module) {
    return ImmutableList.of();
  }

  @Override
  public Collection<VirtualFile> getGoPathSourcesRoots(
      @Nullable Project project, @Nullable Module module) {
    VirtualFile root = TempFileSystem.getInstance().findFileByPath("/src");
    return root == null ? ImmutableList.of() : ImmutableList.of(root);
  }

  @Override
  public Collection<VirtualFile> getGoPathBinRoots(
      @Nullable Project project, @Nullable Module module) {
    return ImmutableList.of();
  }

  @Override
  public boolean isExternal() {
    return false;
  }
}
