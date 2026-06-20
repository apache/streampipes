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

import org.apache.streampipes.connect.transformer.groovy.sandbox.error.SandboxViolationClassNotFoundException;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

public final class RestrictedGroovyClassLoader extends GroovyClassLoader {

  public RestrictedGroovyClassLoader(ClassLoader parent, CompilerConfiguration configuration) {
    super(parent, configuration);
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    rejectForbiddenClass(name);
    return super.loadClass(name);
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve)
      throws ClassNotFoundException, CompilationFailedException {
    rejectForbiddenClass(name);
    return super.loadClass(name, resolve);
  }

  @Override
  public Class loadClass(String name, boolean lookupScriptFiles, boolean preferClassOverScript, boolean resolve)
      throws ClassNotFoundException, CompilationFailedException {
    rejectForbiddenClass(name);
    return super.loadClass(name, lookupScriptFiles, preferClassOverScript, resolve);
  }

  private void rejectForbiddenClass(String className) throws ClassNotFoundException {
    if (SandboxPolicy.blocksType(className)) {
      throw new SandboxViolationClassNotFoundException(
          SandboxPolicy.VIOLATION_MESSAGE + ": access to class '" + className + "' is forbidden");
    }
  }
}
