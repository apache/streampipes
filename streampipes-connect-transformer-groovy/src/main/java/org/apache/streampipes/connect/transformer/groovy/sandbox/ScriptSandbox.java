/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.connect.transformer.groovy.sandbox;

import org.apache.streampipes.connect.transformer.groovy.sandbox.ast.AstChecksCompilationCustomizer;
import org.apache.streampipes.connect.transformer.groovy.sandbox.ast.SecureCustomizerFactory;
import org.apache.streampipes.connect.transformer.groovy.sandbox.error.SandboxCompilationMessageResolver;

import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;

public final class ScriptSandbox {

  private ScriptSandbox() {
  }

  public static GroovyShell createShell(ClassLoader parent) {
    CompilerConfiguration configuration = new CompilerConfiguration();
    configuration.setDisabledGlobalASTTransformations(SandboxPolicy.disabledGlobalAstTransformations());
    configuration.addCompilationCustomizers(
        SecureCustomizerFactory.create(),
        new AstChecksCompilationCustomizer()
    );

    return new GroovyShell(
        new RestrictedGroovyClassLoader(parent, configuration),
        SandboxPolicy.createBinding(),
        configuration
    );
  }

  public static String resolveCompilationErrorMessage(Exception error) {
    return SandboxCompilationMessageResolver.resolve(error);
  }
}
