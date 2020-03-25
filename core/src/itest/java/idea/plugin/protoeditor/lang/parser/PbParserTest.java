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
package idea.plugin.protoeditor.lang.parser;

import com.intellij.lang.LanguageBraceMatching;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.testFramework.ParsingTestCase;
import idea.plugin.protoeditor.TestUtils;
import idea.plugin.protoeditor.ide.editing.ProtoBraceMatcher;
import idea.plugin.protoeditor.lang.PbParserDefinition;
import idea.plugin.protoeditor.lang.PbTextLanguage;
import idea.plugin.protoeditor.lang.PbTextParserDefinition;

public class PbParserTest extends ParsingTestCase {

  public PbParserTest() {
    super("lang/parser", "proto.testdata", new PbParserDefinition());
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    // GeneratedParserUtilBase uses brace matchers to assist in recovery.
    addExplicitExtension(LanguageBraceMatching.INSTANCE, myLanguage, new ProtoBraceMatcher());

    // Needed because proto files directly include prototext elements.
    addExplicitExtension(
        LanguageParserDefinitions.INSTANCE, PbTextLanguage.INSTANCE, new PbTextParserDefinition());
  }

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(this);
    return discoveredPath == null ? "" : discoveredPath;
  }

  /** Test against Unittest.proto from the official protoc release. */
  public void testUnittest() {
    doTest(true);
  }

  /** Test against unittest_proto3.proto from the official protoc release. */
  public void testUnittestProto3() {
    doTest(true);
  }

  /** Test against unittest_custom_options.proto from the official protoc release. */
  public void testUnittestCustomOptions() {
    doTest(true);
  }

  /** Test against Recovery.proto. */
  public void testRecovery() {
    doTest(true);
  }

  public void testUnittestExtensionRange() {
    doTest(true);
  }

  public void testMapAsTypeName() {
    doTest(true);
  }

  public void testMaxRanges() {
    doTest(true);
  }

  public void testEnumReservedRange() {
    doTest(true);
  }
}
