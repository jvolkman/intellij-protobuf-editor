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
package com.google.devtools.intellij.protoeditor.fixtures;

import com.intellij.featureStatistics.FeatureUsageTracker;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;

/** Code insight test fixture for protoeditor tests. */
public class PbCodeInsightFixtureTestCase extends LightCodeInsightFixtureTestCase {

  protected final Disposable testDisposable = new TestDisposable();

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    stubOutFeatureUsageTracker(getTestRootDisposable());
  }

  @Override
  protected void tearDown() throws Exception {
    Disposer.dispose(testDisposable);
    super.tearDown();
  }

  /**
   * Work around an issue with the Android Studio plugin API: Some actions like comment line and
   * completions invoke the FeatureUsageTracker.
   *
   * <p>However, Android Studio has two files named ProductivityFeaturesRegistry.xml in different
   * jars (idea.jar and resources.jar). Regular IntelliJ only has the resources.jar copy. The
   * idea.jar copy contains content relevant to NDK support, but we don't load the appropriate
   * message bundle to go with that. Just stub out FeatureUsageTracker since we aren't testing that,
   * rather than try to get the load order correct within tests.
   */
  private static void stubOutFeatureUsageTracker(Disposable parentDisposable) {
    MutablePicoContainer container =
        (MutablePicoContainer) ApplicationManager.getApplication().getPicoContainer();
    Class<?> keyClass = FeatureUsageTracker.class;
    String keyName = keyClass.getName();
    ComponentAdapter old = container.unregisterComponent(keyName);
    container.registerComponentInstance(keyName, new NoopFeatureUsageTracker());
    Disposer.register(
        parentDisposable,
        () -> {
          container.unregisterComponent(keyName);
          if (old != null) {
            container.registerComponent(old);
          }
        });
  }

  private static class NoopFeatureUsageTracker extends FeatureUsageTracker {

    @Override
    public void triggerFeatureUsed(String s) {}

    @Override
    public void triggerFeatureShown(String s) {}

    @Override
    public boolean isToBeShown(String s, Project project) {
      return false;
    }

    @Override
    public boolean isToBeAdvertisedInLookup(String s, Project project) {
      return false;
    }
  }
}
