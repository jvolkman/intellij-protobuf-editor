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
import com.intellij.openapi.extensions.ExtensionPoint;
import com.intellij.openapi.extensions.impl.ExtensionPointImpl;
import com.intellij.openapi.module.EmptyModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.HeavyPlatformTestCase;
import idea.plugin.protoeditor.ide.settings.PbProjectSettings.ImportPathEntry;

import java.io.File;
import java.util.UUID;

import static idea.plugin.protoeditor.TestUtils.notNull;

/** Unit tests for {@link ProjectSettingsConfiguratorManager}. */
public class ProjectSettingsConfiguratorManagerTest extends HeavyPlatformTestCase {
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

  public void testManagerTracksProjectRootsChanges() {
    // Create a new project with two modules and a total of 3 roots.
    File projectDir = new File(tempDir, "project");
    assertTrue(projectDir.mkdirs());

    Project project = createProject(projectDir.toPath());
    disposeOnTearDown(project);
    Module module1 = doCreateRealModuleIn("module1", project, EmptyModuleType.getInstance());
    Module module2 = doCreateRealModuleIn("module2", project, EmptyModuleType.getInstance());

    File module1Root1 = new File(projectDir, "module1Root1");
    File module1Root1Src = new File(module1Root1, "src");
    File module2Root1 = new File(projectDir, "module2Root1");
    assertTrue(module1Root1Src.mkdirs());
    assertTrue(module2Root1.mkdirs());

    // Clear extension point and register only the DefaultConfigurator.
    ExtensionPoint<ProjectSettingsConfigurator> extensionPoint =
        project.getExtensionArea().getExtensionPoint(ProjectSettingsConfigurator.EP_NAME);
    ((ExtensionPointImpl) extensionPoint).reset();
    extensionPoint.registerExtension(new DefaultConfigurator(), getTestRootDisposable());

    // No roots currently.
    assertSameElements(
        PbProjectSettings.getInstance(project).getImportPathEntries(),
        DefaultConfigurator.getBuiltInIncludeEntry());

    ApplicationManager.getApplication()
        .runWriteAction(
            () -> {
              ModifiableRootModel model =
                  ModuleRootManager.getInstance(module1).getModifiableModel();
              model.addContentEntry(notNull(VfsUtil.findFileByIoFile(module1Root1, false)));
              model.commit();
            });

    // Still no roots since module has no source directory.
    assertSameElements(
        PbProjectSettings.getInstance(project).getImportPathEntries(),
        DefaultConfigurator.getBuiltInIncludeEntry());

    ApplicationManager.getApplication()
        .runWriteAction(
            () -> {
              ModifiableRootModel model =
                  ModuleRootManager.getInstance(module1).getModifiableModel();
              ContentEntry entry = model.getContentEntries()[0];
              entry.addSourceFolder(entry.getUrl() + "/src", false);
              model.commit();
            });

    // Should have one root now, plus the descriptor.
    assertSameElements(
        PbProjectSettings.getInstance(project).getImportPathEntries(),
        new ImportPathEntry(VfsUtil.pathToUrl(module1Root1Src.getPath()), ""),
        DefaultConfigurator.getBuiltInIncludeEntry());

    ApplicationManager.getApplication()
        .runWriteAction(
            () -> {
              ModifiableRootModel model = ModuleRootManager.getInstance(module2).getModifiableModel();
              ContentEntry entry = model.addContentEntry(notNull(VfsUtil.findFileByIoFile(module2Root1, false)));
              entry.addSourceFolder(entry.getUrl(), false);
              model.commit();
            });

    // And now two, plus the descriptor.
    assertSameElements(
        PbProjectSettings.getInstance(project).getImportPathEntries(),
        new ImportPathEntry(VfsUtil.pathToUrl(module1Root1Src.getPath()), ""),
        new ImportPathEntry(VfsUtil.pathToUrl(module2Root1.getPath()), ""),
        DefaultConfigurator.getBuiltInIncludeEntry());

    ApplicationManager.getApplication()
        .runWriteAction(
            () -> {
              ModifiableRootModel model =
                  ModuleRootManager.getInstance(module2).getModifiableModel();
              for (ContentEntry entry : model.getContentEntries()) {
                model.removeContentEntry(entry);
              }
              model.commit();
            });

    // And now back to one, plus the descriptor.
    assertSameElements(
        PbProjectSettings.getInstance(project).getImportPathEntries(),
        new ImportPathEntry(VfsUtil.pathToUrl(module1Root1Src.getPath()), ""),
        DefaultConfigurator.getBuiltInIncludeEntry());

    ApplicationManager.getApplication()
        .runWriteAction(
            () -> {
              ModifiableRootModel model =
                  ModuleRootManager.getInstance(module1).getModifiableModel();
              for (ContentEntry entry : model.getContentEntries()) {
                for (SourceFolder folder : entry.getSourceFolders()) {
                  entry.removeSourceFolder(folder);
                }
              }
              model.commit();
            });

    // We removed all of the source folders (but left the root), so there should be no paths remaining.
    assertSameElements(
        PbProjectSettings.getInstance(project).getImportPathEntries(),
        DefaultConfigurator.getBuiltInIncludeEntry());
  }

  public void testExtensionConfiguratorsTakePrecedence() {
    File projectDir = new File(tempDir, "project");
    assertTrue(projectDir.mkdirs());

    Project project = createProject(projectDir.toPath());
    disposeOnTearDown(project);
    Module module = doCreateRealModuleIn("module", project, EmptyModuleType.getInstance());

    File module1Root1 = new File(projectDir, "module1Root1");
    File foobarRoot = new File(projectDir, "foobarRoot");
    assertTrue(module1Root1.mkdirs());
    assertTrue(foobarRoot.mkdirs());

    ExtensionPoint<ProjectSettingsConfigurator> extensionPoint =
        project.getExtensionArea().getExtensionPoint(ProjectSettingsConfigurator.EP_NAME);

    // Remove all but the DefaultConfigurator extension. Do this rather than a reset + add to test
    // that the DefaultConfigurator is registered in plugin.xml with order=last.
    extensionPoint.unregisterExtensions(
        (className, adapter) -> DefaultConfigurator.class.getCanonicalName().equals(className), false);


    // Add a configurator that takes over when the "foobarRoot" root is added.
    extensionPoint.registerExtension(
        (project1, settings) -> {
          VirtualFile[] roots = ProjectRootManager.getInstance(project1).getContentRoots();
          for (VirtualFile root : roots) {
            if (root.getPath().equals(foobarRoot.getPath())) {
              settings.getImportPathEntries().clear();
              settings
                  .getImportPathEntries()
                  .add(
                      new ImportPathEntry(
                          VfsUtil.pathToUrl(foobarRoot.getPath()), "some/custom/prefix"));
              settings.setDescriptorPath("some/custom/descriptor.proto");
              return settings;
            }
          }
          return null;
        }, getTestRootDisposable());

    ApplicationManager.getApplication()
        .runWriteAction(
            () -> {
              ModifiableRootModel model =
                  ModuleRootManager.getInstance(module).getModifiableModel();
              ContentEntry entry =
                  model.addContentEntry(notNull(VfsUtil.findFileByIoFile(module1Root1, false)));
              entry.addSourceFolder(entry.getUrl(), false);
              model.commit();
            });

    // Should have one root now, plus the descriptor.
    assertSameElements(
        PbProjectSettings.getInstance(project).getImportPathEntries(),
        new ImportPathEntry(VfsUtil.pathToUrl(module1Root1.getPath()), ""),
        DefaultConfigurator.getBuiltInIncludeEntry());

    ApplicationManager.getApplication()
        .runWriteAction(
            () -> {
              ModifiableRootModel model =
                  ModuleRootManager.getInstance(module).getModifiableModel();
              ContentEntry entry =
                  model.addContentEntry(notNull(VfsUtil.findFileByIoFile(foobarRoot, false)));
              entry.addSourceFolder(entry.getUrl(), false);
              model.commit();
            });

    // Now the custom configurator should have taken precedence. We should have one entry with a
    // prefix, as well as a custom descriptor path.
    assertSameElements(
        PbProjectSettings.getInstance(project).getImportPathEntries(),
        new ImportPathEntry(VfsUtil.pathToUrl(foobarRoot.getPath()), "some/custom/prefix"));
    assertEquals(
        "some/custom/descriptor.proto", PbProjectSettings.getInstance(project).getDescriptorPath());
  }

  public void testGetDescriptorPathSuggestions() {
    assertContainsElements(
        ProjectSettingsConfiguratorManager.getInstance(getProject()).getDescriptorPathSuggestions(),
        "google/protobuf/descriptor.proto");
  }
}
