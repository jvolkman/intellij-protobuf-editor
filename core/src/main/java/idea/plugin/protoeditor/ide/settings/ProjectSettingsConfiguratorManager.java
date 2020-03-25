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

import com.intellij.ProjectTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * A project component that enumerates {@link ProjectSettingsConfigurator} providers and performs
 * autoconfiguration when possible.
 *
 * <p>This component will automatically update {@link PbProjectSettings} configuration if {@link
 * PbProjectSettings#isAutoConfigEnabled()} returns <code>true</code>. Configuration is updated
 * whenever the project is opened or the project's roots change.
 */
public class ProjectSettingsConfiguratorManager implements ProjectComponent {

  private final Project project;
  private MessageBusConnection bus;

  public ProjectSettingsConfiguratorManager(Project project) {
    this.project = project;
  }

  public static ProjectSettingsConfiguratorManager getInstance(Project project) {
    return project.getComponent(ProjectSettingsConfiguratorManager.class);
  }

  /**
   * Creates a new configuration based on the given settings.
   *
   * <p>Extension {@link ProjectSettingsConfigurator} instances take precedence, if they exist. If
   * none exist, or none are able to determine a configuration for the project, {@link
   * DefaultConfigurator} is used.
   *
   * @param settings the initial settings
   * @return updated settings, or <code>null</code> if no updates were made
   */
  @Nullable
  public PbProjectSettings configure(PbProjectSettings settings) {
    for (ProjectSettingsConfigurator configurator : getConfigurators()) {
      PbProjectSettings configured = configurator.configure(project, settings.copy());
      if (configured != null) {
        return configured;
      }
    }
    return null;
  }

  /**
   * Return descriptor path suggestions to present to the user.
   *
   * @return descriptor path suggestions
   */
  @NotNull
  public Collection<String> getDescriptorPathSuggestions() {
    Set<String> suggestions = new TreeSet<>();
    for (ProjectSettingsConfigurator configurator : getConfigurators()) {
      suggestions.addAll(configurator.getDescriptorPathSuggestions(project));
    }
    return suggestions;
  }

  @Override
  public void initComponent() {
    bus = project.getMessageBus().connect();
    bus.subscribe(
        ProjectTopics.PROJECT_ROOTS,
        new ModuleRootListener() {
          @Override
          public void rootsChanged(ModuleRootEvent event) {
            configureSettingsIfNecessary();
          }
        });
  }

  @Override
  public void disposeComponent() {
    bus.disconnect();
    bus = null;
  }

  @Override
  public void projectOpened() {
    configureSettingsIfNecessary();
  }

  private ProjectSettingsConfigurator[] getConfigurators() {
    return project.getExtensions(ProjectSettingsConfigurator.EP_NAME);
  }

  private void configureSettingsIfNecessary() {
    PbProjectSettings settings = PbProjectSettings.getInstance(project);
    if (settings.isAutoConfigEnabled()) {
      PbProjectSettings newSettings = configure(settings);
      if (newSettings != null && !settings.equals(newSettings)) {
        settings.copyState(newSettings);
        // Using ModalityState.NON_MODAL here ensures the caches are invalidated in a
        // write-safe context, regardless of what context we were invoked from.
        ApplicationManager.getApplication()
            .invokeLater(() -> PbProjectSettings.notifyUpdated(project), ModalityState.NON_MODAL);
      }
    }
  }
}
