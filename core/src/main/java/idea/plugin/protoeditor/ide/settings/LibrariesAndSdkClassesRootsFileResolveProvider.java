package idea.plugin.protoeditor.ide.settings;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import idea.plugin.protoeditor.lang.resolve.FileResolveProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class LibrariesAndSdkClassesRootsFileResolveProvider implements FileResolveProvider {
    @Nullable
    @Override
    public VirtualFile findFile(@NotNull String path, @NotNull Project project) {
        for (Library library : LibraryTablesRegistrar.getInstance().getLibraryTable(project).getLibraries()) {
            VirtualFile result = findFileInRoots(path, library.getFiles(OrderRootType.CLASSES));
            if(result != null) return result;
        }
        return null;
    }

    @Nullable
    @Override
    public VirtualFile findFile(@NotNull String path, @NotNull Module module) {
        VirtualFile[] roots = ModuleRootManager.getInstance(module).orderEntries().getAllLibrariesAndSdkClassesRoots();
        return findFileInRoots(path, roots);
    }

    @NotNull
    @Override
    public Collection<ChildEntry> getChildEntries(@NotNull String path, @NotNull Project project) {
        VirtualFile[] roots = Arrays.stream(LibraryTablesRegistrar.getInstance().getLibraryTable(project).getLibraries())
                .flatMap(library -> Arrays.stream(library.getFiles(OrderRootType.CLASSES)))
                .collect(Collectors.toList()).toArray(new VirtualFile[]{});
        return findChildEntriesInRoots(roots);
    }

    @NotNull
    @Override
    public Collection<ChildEntry> getChildEntries(@NotNull String path, @NotNull Module module) {
        VirtualFile[] roots = ModuleRootManager.getInstance(module).orderEntries().getAllLibrariesAndSdkClassesRoots();
        return findChildEntriesInRoots(roots);
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
        return GlobalSearchScope.everythingScope(project);
    }

    private VirtualFile findFileInRoots(String path, VirtualFile[] roots) {
        for (VirtualFile root : roots) {
            VirtualFile file = root.findFileByRelativePath(path);
            if (file != null && file.exists()) {
                return file;
            }
        }
        return null;
    }

    private Collection<ChildEntry> findChildEntriesInRoots(VirtualFile[] roots) {
        return Arrays.stream(roots).flatMap(root -> VfsUtil.getChildren(root, PROTO_AND_DIRECTORY_FILTER)
                .stream()
                .map(child -> new ChildEntry(child.getName(), child.isDirectory()))).collect(Collectors.toList());
    }
}
