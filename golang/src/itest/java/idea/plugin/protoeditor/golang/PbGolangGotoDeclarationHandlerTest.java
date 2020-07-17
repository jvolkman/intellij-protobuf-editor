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

import com.goide.psi.GoFile;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import idea.plugin.protoeditor.TestUtils;
import idea.plugin.protoeditor.fixtures.PbCodeInsightFixtureTestCase;
import idea.plugin.protoeditor.gencodeutils.GotoExpectationMarker;
import idea.plugin.protoeditor.gencodeutils.ReferenceGotoExpectation;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static idea.plugin.protoeditor.TestUtils.notNull;

/**
 * Tests for {@link PbGolangGotoDeclarationHandler}.
 *
 * <p>The general form of the test is for each generated code variant:
 *
 * <ul>
 *   <li>Have a .proto file
 *   <li>Have the generated code from that .proto file, in the form of a .pb.go.
 *   <li>Have a "proto_versionX_user.go" that uses the generated code
 * </ul>
 *
 * Each user.go file is annotated with {@link #CARET_MARKER}, and {@link GotoExpectationMarker}
 * annotations. We parse the user.go file to determine what to test (goto action from the caret
 * marker) and the expected outcome of the goto action for the next caret marker (expected target
 * .proto file + element). There must be at least one {@link #CARET_MARKER} after each {@link
 * GotoExpectationMarker}.
 */
public class PbGolangGotoDeclarationHandlerTest extends PbCodeInsightFixtureTestCase {
  // TEST_PROTO_PATH is equal to the Bazel package name containing the .proto files.
  private static final String TEST_PROTO_PATH = System.getProperty("test.resources.package.name");
  private static final String IMPORT_PREFIX = System.getProperty("test.resources.import.prefix");

  // Marker that determines which caret positions to test.
  private static final String CARET_MARKER = "caretAfterThis";
  // CARET_MARKER will be in a /* */ comment, scoot past the " */ "
  private static final int CARET_BUMP = 4;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    TestUtils.addTestFileResolveProvider(getProject(), getTestRootDisposable());
  }

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(this);
    return discoveredPath == null ? "" : discoveredPath;
  }

  public void testProto2() {
    copyProtoAndGenCode("proto2.proto", "proto2.pb.go", "proto2_go_proto");
    GoFile userFile = copyProtoUserFile("proto2_user.go", "proto2_user.go");

    assertWithMessage("Number of markers (" + GotoExpectationMarker.EXPECT_MARKER + ")")
        .that(checkExpectations(userFile))
        .isEqualTo(62);
  }

  public void testProto2Gogo() {
    copyProtoAndGenCode("proto2_gogo.proto", "proto2_gogo.pb.go", "proto2_gogo_go_proto");
    GoFile userFile = copyProtoUserFile("proto2_gogo_user.go", "proto2_gogo_user.go");

    assertWithMessage("Number of markers (" + GotoExpectationMarker.EXPECT_MARKER + ")")
            .that(checkExpectations(userFile))
            .isEqualTo(62);
  }

  public void testProto3() {
    copyProtoAndGenCode("proto3.proto", "proto3.pb.go", "proto3_go_proto");
    GoFile userFile = copyProtoUserFile("proto3_user.go", "proto3_user.go");

    assertWithMessage("Number of markers (" + GotoExpectationMarker.EXPECT_MARKER + ")")
        .that(checkExpectations(userFile))
        .isEqualTo(62);
  }

  public void testProto3Gogo() {
    copyProtoAndGenCode("proto3_gogo.proto", "proto3_gogo.pb.go", "proto3_gogo_go_proto");
    GoFile userFile = copyProtoUserFile("proto3_gogo_user.go", "proto3_gogo_user.go");

    assertWithMessage("Number of markers (" + GotoExpectationMarker.EXPECT_MARKER + ")")
            .that(checkExpectations(userFile))
            .isEqualTo(62);
  }

  private void copyProtoAndGenCode(String protoFile, String generatedGoFile, String goPackageName) {
    // Proto file has to go to the regular place so the proto declaration resolving works.
    myFixture.copyFileToProject(protoFile, new File(TEST_PROTO_PATH, protoFile).getPath());
    // Go file has to go into a packageName subdirectory so importPath resolving works.
    File packagePath = Paths.get(IMPORT_PREFIX, TEST_PROTO_PATH, goPackageName).toFile();
    myFixture.copyFileToProject(generatedGoFile, new File(packagePath, generatedGoFile).getPath());
  }

  private GoFile copyProtoUserFile(String relativeSourcePath, String destPath) {
    VirtualFile virtualFile = myFixture.copyFileToProject(relativeSourcePath, destPath);
    myFixture.configureFromExistingVirtualFile(virtualFile);
    PsiFile psiFile = getFile();
    assertThat(psiFile).isInstanceOf(GoFile.class);
    return (GoFile) psiFile;
  }

  /**
   * Parses the userFile for {@link GotoExpectationMarker} annotations, and {@link #CARET_MARKER}.
   * Performs a "goto" action on the element highlighted by a caret marker, and checks that the
   * target matches the expectation. Returns the number of checks performed, so that we know that
   * the checks aren't accidentally skipped (grep the file yourself to sanity check)
   */
  private int checkExpectations(GoFile psiFile) {
    List<GotoExpectationMarker> expectations = GotoExpectationMarker.parseExpectations(psiFile);

    Project project = getProject();
    Editor editor = getEditor();
    String text = psiFile.getText();

    for (GotoExpectationMarker expectation : expectations) {
      String subtestText = text.substring(expectation.startIndex, expectation.endIndex);
      int caretOffset = subtestText.indexOf(CARET_MARKER);
      assertWithMessage(
              String.format(
                  "Caret to check is in %s within range %s",
                  subtestText, expectation.rangeString()))
          .that(caretOffset)
          .isNotEqualTo(-1);
      final int bumpedCaret =
          expectation.startIndex + caretOffset + CARET_MARKER.length() + CARET_BUMP;
      ApplicationManager.getApplication()
          .runReadAction(
              () -> {
                PsiElement srcElement = notNull(psiFile.findElementAt(bumpedCaret));
                ReferenceGotoExpectation referenceGotoExpectation =
                    ReferenceGotoExpectation.create(srcElement.getText(), expectation);
                PsiElement[] elements =
                    GotoDeclarationAction.findAllTargetElements(project, editor, bumpedCaret);
                referenceGotoExpectation.assertCorrectTarget(elements);
              });
    }
    return expectations.size();
  }
}
