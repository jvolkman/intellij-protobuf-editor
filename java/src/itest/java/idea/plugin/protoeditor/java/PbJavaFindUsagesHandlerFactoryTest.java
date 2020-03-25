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
package idea.plugin.protoeditor.java;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.truth.StandardSubjectBuilder;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.QualifiedName;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.intellij.usageView.UsageInfo;
import idea.plugin.protoeditor.TestUtils;
import idea.plugin.protoeditor.java.names.NameGenerator;
import idea.plugin.protoeditor.lang.psi.PbFile;
import idea.plugin.protoeditor.lang.psi.PbSymbol;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static idea.plugin.protoeditor.TestUtils.notNull;

/**
 * Test for {@link PbJavaFindUsagesHandlerFactory}.
 *
 * <p>The general form of the test is for each generated code variant:
 *
 * <ul>
 *   <li>Have a .proto file
 *   <li>Have the generated code from that .proto file, in the form of a library jar.
 *   <li>Have a "ProtoVersionXUser.java" that uses the generated code
 * </ul>
 *
 * We look up a {@link PbSymbol} in the .proto file, find its usages, and check that it returns some
 * references in the User.java file.
 *
 * <p>This shares some test data with {@link PbJavaGotoDeclarationHandlerTest}, and we assume that
 * we share implementation code between the two, so this is not as exhaustive (namely, shares {@link
 * NameGenerator}).
 */
public class PbJavaFindUsagesHandlerFactoryTest extends LightJavaCodeInsightFixtureTestCase {

  protected final Disposable testDisposable = new TestDisposable();

  @Override
  protected void tearDown() throws Exception {
    Disposer.dispose(testDisposable);
    super.tearDown();
  }

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(this);
    return discoveredPath == null ? "" : discoveredPath;
  }

  private PsiFile configureRootFile(String filename) {
    String root = TestUtils.getTestRootPath(this);
    String path = new File(root, filename).getPath();
    VirtualFile vFile = myFixture.copyFileToProject(path, filename);
    myFixture.configureFromExistingVirtualFile(vFile);
    return myFixture.getFile();
  }

  public void testProto2() {
    String root = TestUtils.getTestRootPath(this);
    JavaTestData.addGenCodeJar(
        myFixture.getModule(), root, "libProto2Lib-speed.jar", testDisposable);
    String expectedJavaFile = "Proto2User.java";
    JavaTestData.copyJavaProtoUser(myFixture, new File("java", expectedJavaFile));
    PsiFile protoFile = configureRootFile("Proto2.proto");

    // Message
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "M1.NestedM1", 2),
        expectedJavaFile,
        10,
        "NestedM1 messageType",
        "NestedM1 x)",
        "NestedM1OrBuilder getSingle",
        "NestedM1.Builder");

    // Fields
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "M1.single_primitive", 0),
        expectedJavaFile,
        5,
        "hasSinglePrimitive",
        "getSinglePrimitive",
        "setSinglePrimitive",
        "clearSinglePrimitive",
        "SINGLE_PRIMITIVE_FIELD_NUMBER");
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "M1.repeated_primitive", 0),
        expectedJavaFile,
        7,
        "getRepeatedPrimitiveCount",
        "getRepeatedPrimitiveList",
        "getRepeatedPrimitive(index)");

    // Enums / EnumValues
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "Shapes", 2),
        expectedJavaFile,
        7,
        "Shapes;",
        "Shapes getSingle",
        "Shapes enumForValue",
        "Shapes.");
    checkJavaUsages(findJavaUsesForSymbol(protoFile, "CIRCLE", 0), expectedJavaFile, 1, "CIRCLE);");

    // Group
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "M1.SingleGroupField", 0),
        expectedJavaFile,
        2,
        "SingleGroupField getSingle",
        "getSingleGroupField");

    // Oneof (enum, message member, enum values)
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "M1.test_oneof", 0),
        expectedJavaFile,
        10,
        "TestOneofCase;",
        "TestOneofCase getOneof",
        "getTestOneofCase",
        "clearTestOneof",
        "TestOneofCase oneofEnum",
        "TestOneofCase.",
        "TestOneofCase.",
        "TESTONEOF_NOT_SET");
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "M1.string_choice", 0),
        expectedJavaFile,
        2,
        "STRING_CHOICE",
        "setStringChoiceBytes");
  }

  public void testProtoSyntax3() {
    String root = TestUtils.getTestRootPath(this);
    JavaTestData.addGenCodeJar(
        myFixture.getModule(), root, "libProtoSyntax3Lib-speed.jar", testDisposable);
    String expectedJavaFile = "ProtoSyntax3User.java";
    JavaTestData.copyJavaProtoUser(myFixture, new File("java", expectedJavaFile));
    PsiFile protoFile = configureRootFile("ProtoSyntax3.proto");

    // Message
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "M1.NestedM1", 2),
        expectedJavaFile,
        2,
        "NestedM1.Builder setSingle",
        "NestedM1.Builder caret");

    // Fields
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "M1.single_primitive", 0),
        expectedJavaFile,
        2,
        "getSinglePrimitive",
        "setSinglePrimitive");

    // Enums / EnumValues
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "Shapes", 2), expectedJavaFile, 2, "Shapes;", "Shapes.");
    checkJavaUsages(findJavaUsesForSymbol(protoFile, "CIRCLE", 0), expectedJavaFile, 1, "CIRCLE);");

    // Oneof
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "M1.test_oneof", 0),
        expectedJavaFile,
        5,
        "TestOneofCase;",
        "TestOneofCase oneofEnum",
        "TestOneofCase.",
        "TestOneofCase.",
        "TESTONEOF_NOT_SET");
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "M1.string_choice", 0),
        expectedJavaFile,
        1,
        "STRING_CHOICE");
  }

  public void testProto2Lite() {
    String root = TestUtils.getTestRootPath(this);
    JavaTestData.addGenCodeJar(
        myFixture.getModule(), root, "libProto2LiteLib-lite.jar", testDisposable);
    String expectedJavaFile = "Proto2LiteUser.java";
    JavaTestData.copyJavaProtoUser(myFixture, new File("java", expectedJavaFile));
    PsiFile protoFile = configureRootFile("Proto2Lite.proto");

    // Message
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "M1.NestedM1", 2),
        expectedJavaFile,
        2,
        "NestedM1.Builder setSingle",
        "NestedM1.Builder caret");

    // Fields
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "M1.single_primitive", 0),
        expectedJavaFile,
        2,
        "getSinglePrimitive",
        "setSinglePrimitive");

    // Enums / EnumValues
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "Shapes", 2), expectedJavaFile, 2, "Shapes;", "Shapes.");
    checkJavaUsages(findJavaUsesForSymbol(protoFile, "CIRCLE", 0), expectedJavaFile, 1, "CIRCLE);");

    // Oneof
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "M1.test_oneof", 0),
        expectedJavaFile,
        5,
        "TestOneofCase;",
        "TestOneofCase oneofEnum",
        "TestOneofCase.",
        "TestOneofCase.",
        "TESTONEOF_NOT_SET");
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "M1.string_choice", 0),
        expectedJavaFile,
        1,
        "STRING_CHOICE");
  }

  public void testClashingEnum() {
    String root = TestUtils.getTestRootPath(this);
    JavaTestData.addGenCodeJar(
        myFixture.getModule(), root, "libClashingEnum-speed.jar", testDisposable);
    String expectedJavaFile = "ClashingEnumUser.java";
    JavaTestData.copyJavaProtoUser(myFixture, new File("java", expectedJavaFile));
    PsiFile protoFile = myFixture.configureByFile("proto/clashing_enum.proto");

    // Enums / EnumValues
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "ClashingEnum", 0),
        expectedJavaFile,
        3,
        "ClashingEnum.ZERO;",
        "ClashingEnum;",
        "ClashingEnum test()");
    checkJavaUsages(
        findJavaUsesForSymbol(protoFile, "ZERO", 0), expectedJavaFile, 2, "ZERO;", "ZERO;");
  }

  private List<UsageInfo> findJavaUsesForSymbol(
      PsiFile file, String localDottedName, int expectedProtoUsages) {
    assertThat(file).isInstanceOf(PbFile.class);
    PbFile pbFile = (PbFile) file;
    QualifiedName fullyQualifiedName =
        pbFile.getPackageQualifiedName().append(QualifiedName.fromDottedString(localDottedName));
    Collection<PbSymbol> symbols = pbFile.getLocalQualifiedSymbolMap().get(fullyQualifiedName);
    assertWithMessage("Find symbol: " + localDottedName).that(symbols).hasSize(1);
    List<UsageInfo> allUses =
        symbols.stream()
            .findFirst()
            .map(symbol -> ImmutableList.copyOf(myFixture.findUsages(symbol)))
            .orElse(ImmutableList.of());
    List<UsageInfo> protoUses = protoUsages(allUses);
    assertWithUsages(protoUses).that(protoUses).hasSize(expectedProtoUsages);
    List<UsageInfo> javaUses = javaUsages(allUses);
    // Sort by use position to make sure they are stable.
    javaUses.sort(
        (u1, u2) ->
            ComparisonChain.start()
                .compare(u1.getVirtualFile().getPath(), u2.getVirtualFile().getPath())
                .compare(u1.getNavigationOffset(), u2.getNavigationOffset())
                .result());
    return javaUses;
  }

  private static List<UsageInfo> protoUsages(List<UsageInfo> allUsages) {
    return allUsages.stream()
        .filter(use -> use.getFile() instanceof PbFile)
        .collect(Collectors.toList());
  }

  private static List<UsageInfo> javaUsages(List<UsageInfo> allUsages) {
    return allUsages.stream()
        .filter(use -> use.getFile() instanceof PsiJavaFile)
        .collect(Collectors.toList());
  }

  private void checkJavaUsages(
      List<UsageInfo> uses,
      String expectedFile,
      int expectedJavaUsages,
      String... expectedContexts) {
    assertWithUsages(uses).that(uses).hasSize(expectedJavaUsages);
    // Do a "zip" between uses and expectedContents (though expectedContents is a prefix of uses).
    assertWithUsages(uses).that(uses.size()).isAtLeast(expectedContexts.length);
    for (int i = 0; i < expectedContexts.length; ++i) {
      String expectedContext = expectedContexts[i];
      UsageInfo use = uses.get(i);
      PsiFile file = notNull(use.getFile());
      assertWithMessage("usage #" + i).that(file.getName()).isEqualTo(expectedFile);
      String actualContext =
          file.getText()
              .substring(
                  use.getNavigationOffset(), use.getNavigationOffset() + expectedContext.length());
      assertWithMessage("usage #" + i).that(actualContext).isEqualTo(expectedContext);
    }
  }

  private StandardSubjectBuilder assertWithUsages(List<UsageInfo> uses) {
    return assertWithMessage(describeUsages(uses));
  }

  private static String describeUsages(List<UsageInfo> uses) {
    return String.join(
        ", ",
        uses.stream()
            .map(
                use ->
                    String.format(
                        "%s:%s %s",
                        notNull(use.getFile()).getName(),
                        use.getNavigationOffset(),
                        notNull(use.getReference()).getCanonicalText()))
            .collect(Collectors.toList()));
  }
}
