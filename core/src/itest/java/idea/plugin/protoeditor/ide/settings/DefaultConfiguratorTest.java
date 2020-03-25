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
package idea.plugin.protoeditor.ide.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.EmptyModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.testFramework.HeavyPlatformTestCase;
import idea.plugin.protoeditor.ide.settings.PbProjectSettings.ImportPathEntry;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static idea.plugin.protoeditor.TestUtils.notNull;

/** Unit tests for {@link DefaultConfigurator}. */
public class DefaultConfiguratorTest extends HeavyPlatformTestCase {

  private File tempDir = null;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    tempDir = FileUtil.createTempDirectory(getName(), UUID.randomUUID().toString(), false);
  }

  @Override
  public void tearDown() throws Exception {
    FileUtil.delete(tempDir);
    super.tearDown();
  }

  public void testConfigure() {
    // Create a new project with two modules and a total of 3 roots.
    File projectDir = new File(tempDir, "project");
    assertTrue(projectDir.mkdirs());

    Project project = createProject(projectDir.toPath());
    disposeOnTearDown(project);
    Module module1 = doCreateRealModuleIn("module1", project, EmptyModuleType.getInstance());
    Module module2 = doCreateRealModuleIn("module2", project, EmptyModuleType.getInstance());

    File module1Root1 = new File(projectDir, "module1Root1");
    File module1Root2 = new File(projectDir, "module1Root2");
    File module2Root1 = new File(projectDir, "module2Root1");
    assertTrue(module1Root1.mkdirs());
    assertTrue(module1Root2.mkdirs());
    assertTrue(module2Root1.mkdirs());

    ApplicationManager.getApplication()
        .runWriteAction(
            () -> {
              ModifiableRootModel model1 =
                  ModuleRootManager.getInstance(module1).getModifiableModel();
              ModifiableRootModel model2 =
                  ModuleRootManager.getInstance(module2).getModifiableModel();
              model1.addContentEntry(notNull(VfsUtil.findFileByIoFile(module1Root1, false)));
              model1.addContentEntry(notNull(VfsUtil.findFileByIoFile(module1Root2, false)));
              model1.commit();
              model2.addContentEntry(notNull(VfsUtil.findFileByIoFile(module2Root1, false)));
              model2.commit();
            });

    PbProjectSettings settings = new PbProjectSettings();
    // The default project's directory exists in some temp path that shouldn't descend from
    // /google3/.
    settings = new DefaultConfigurator().configure(project, settings);
    assertNotNull(settings);

    assertEquals("google/protobuf/descriptor.proto", settings.getDescriptorPath());
    assertSameElements(
        settings.getImportPathEntries(),
        new ImportPathEntry(VfsUtil.pathToUrl(module1Root1.getPath()), ""),
        new ImportPathEntry(VfsUtil.pathToUrl(module1Root2.getPath()), ""),
        new ImportPathEntry(VfsUtil.pathToUrl(module2Root1.getPath()), ""),
        DefaultConfigurator.getBuiltInIncludeEntry());
  }

  public void testGetDescriptorPathSuggestions() {
    assertContainsElements(
        new DefaultConfigurator().getDescriptorPathSuggestions(getProject()),
        "google/protobuf/descriptor.proto");
  }

  public void testBuiltInDescriptor() throws IOException {
    ImportPathEntry includeEntry = DefaultConfigurator.getBuiltInIncludeEntry();
    assertNotNull(includeEntry);
    assertEquals("", includeEntry.getPrefix());
    VirtualFile descriptorDir =
        VirtualFileManager.getInstance().findFileByUrl(includeEntry.getLocation());
    assertNotNull(descriptorDir);
    VirtualFile descriptorFile = descriptorDir.findFileByRelativePath("google/protobuf/descriptor.proto");
    assertNotNull(descriptorFile);
    String text = VfsUtil.loadText(descriptorFile);
    // Simple check to make sure it's a descriptor.
    assertTrue(text.contains("FileOptions"));
  }
}
