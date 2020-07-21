package idea.plugin.protoeditor.ide.settings;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import idea.plugin.protoeditor.lang.resolve.FileResolveProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BuiltInFileResolveProvider implements FileResolveProvider {
  private final VirtualFile includeDir = VfsUtil.findFileByURL(BuiltInFileResolveProvider.class.getResource("/include"));

  @Nullable
  @Override
  public VirtualFile findFile(@NotNull String path, @NotNull Project project) {
    if (includeDir == null) return null;
    if (!checkInJavaEnv(project)) {
      VirtualFile file = includeDir.findFileByRelativePath(path);
      if (file != null && file.exists()) {
        return file;
      }
    }
    return null;
  }

  @Nullable
  @Override
  public VirtualFile findFile(@NotNull String path, @NotNull Module module) {
    return findFile(path, module.getProject());
  }

  @NotNull
  @Override
  public Collection<ChildEntry> getChildEntries(@NotNull String path, @NotNull Project project) {
    if (includeDir == null) return Collections.emptyList();
    if (!checkInJavaEnv(project)) {
      return findChildEntriesInRoot(includeDir);
    }
    return Collections.emptyList();
  }

  @NotNull
  @Override
  public Collection<ChildEntry> getChildEntries(@NotNull String path, @NotNull Module module) {
    return getChildEntries(path, module.getProject());
  }

  @Nullable
  @Override
  public VirtualFile getDescriptorFile(@NotNull Project project) {
    return findFile(DefaultConfigurator.DESCRIPTOR, project);
  }

  @Nullable
  @Override
  public VirtualFile getDescriptorFile(@NotNull Module module) {
    return findFile(DefaultConfigurator.DESCRIPTOR, module);
  }

  @NotNull
  @Override
  public GlobalSearchScope getSearchScope(@NotNull Project project) {
    return GlobalSearchScope.projectScope(project);
  }

  private boolean checkInJavaEnv(@NotNull Project project) {
    Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
    if (projectSdk == null) return false;
    SdkTypeId projectSdkId = projectSdk.getSdkType();
    return projectSdkId instanceof JavaSdkType;
  }

  private Collection<ChildEntry> findChildEntriesInRoot(VirtualFile root) {
    return VfsUtil.getChildren(root, PROTO_AND_DIRECTORY_FILTER)
        .stream()
        .map(child -> new ChildEntry(child.getName(), child.isDirectory()))
        .collect(Collectors.toList());
  }
}
