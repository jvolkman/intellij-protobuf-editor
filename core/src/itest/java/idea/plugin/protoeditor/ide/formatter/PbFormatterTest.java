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
package idea.plugin.protoeditor.ide.formatter;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.codeStyle.CodeStyleManager;
import idea.plugin.protoeditor.TestUtils;
import idea.plugin.protoeditor.fixtures.PbCodeInsightFixtureTestCase;

public class PbFormatterTest extends PbCodeInsightFixtureTestCase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    TestUtils.registerTestdataFileExtension();
  }

  @Override
  public String getTestDataPath() {
    String discoveredPath = TestUtils.getTestdataPath(this);
    String path = discoveredPath == null ? "" : discoveredPath;
    return path + "/ide/formatter/";
  }

  public void testFormatter() {
    myFixture.configureByFiles("FormatterTestBefore.proto.testdata");
    new WriteCommandAction.Simple(getProject()) {
      @Override
      protected void run() throws Throwable {
        CodeStyleManager.getInstance(getProject()).reformat(myFixture.getFile());
      }
    }.execute();
    myFixture.checkResultByFile("FormatterTestAfter.proto.testdata");
  }
}
