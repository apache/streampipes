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

import groovy.lang.Binding;

import java.util.List;
import java.util.Set;

public final class SandboxPolicy {

  public static final String VIOLATION_MESSAGE = "Groovy script violates sandbox restrictions";

  private static final List<String> DISALLOWED_IMPORTS = List.of(
      "java.io.File",
      "java.io.FileInputStream",
      "java.io.FileOutputStream",
      "java.io.RandomAccessFile",
      "java.net.InetAddress",
      "java.net.ServerSocket",
      "java.net.Socket",
      "java.net.URI",
      "java.net.URL",
      "java.lang.Class",
      "java.lang.ClassLoader",
      "java.lang.Process",
      "java.lang.ProcessBuilder",
      "java.lang.Runtime",
      "java.lang.System",
      "java.lang.Thread",
      "java.nio.channels.FileChannel",
      "java.nio.file.Files",
      "java.nio.file.Path",
      "java.nio.file.Paths",
      "groovy.lang.GroovyClassLoader",
      "groovy.lang.GroovyShell",
      "groovy.transform.ASTTest",
      "groovy.util.Eval"
  );
  private static final List<String> DISALLOWED_STAR_IMPORTS = List.of(
      "java.io",
      "java.net",
      "java.nio",
      "java.nio.channels",
      "java.nio.file",
      "java.lang.reflect",
      "groovy.io"
  );
  private static final List<String> DISALLOWED_STATIC_IMPORTS = List.of(
      "java.lang.System",
      "java.lang.Runtime",
      "java.nio.file.Files",
      "java.nio.file.Paths"
  );
  private static final Set<String> DISABLED_GLOBAL_AST_TRANSFORMATIONS = Set.of(
      "groovy.grape.GrabAnnotationTransformation",
      "org.codehaus.groovy.transform.ASTTestTransformation",
      "org.codehaus.groovy.transform.AnnotationCollectorTransform"
  );
  private static final Set<String> DISALLOWED_EXACT_TYPES = Set.of(
      "java.io.File",
      "java.io.FileInputStream",
      "java.io.FileOutputStream",
      "java.io.RandomAccessFile",
      "java.net.InetAddress",
      "java.net.ServerSocket",
      "java.net.Socket",
      "java.net.URI",
      "java.net.URL",
      "java.lang.Class",
      "java.lang.ClassLoader",
      "java.lang.Process",
      "java.lang.ProcessBuilder",
      "java.lang.Runtime",
      "java.lang.System",
      "java.lang.Thread",
      "java.nio.channels.FileChannel",
      "java.nio.file.Files",
      "java.nio.file.Path",
      "java.nio.file.Paths",
      "groovy.lang.GroovyClassLoader",
      "groovy.lang.GroovyShell",
      "groovy.transform.ASTTest",
      "groovy.util.Eval"
  );
  private static final Set<String> DISALLOWED_SIMPLE_TYPES = Set.of(
      "File",
      "FileInputStream",
      "FileOutputStream",
      "RandomAccessFile",
      "InetAddress",
      "ServerSocket",
      "Socket",
      "URI",
      "URL",
      "Class",
      "ClassLoader",
      "Process",
      "ProcessBuilder",
      "Runtime",
      "System",
      "Thread",
      "FileChannel",
      "Files",
      "Path",
      "Paths",
      "GroovyClassLoader",
      "GroovyShell",
      "ASTTest",
      "Eval"
  );
  private static final List<String> DISALLOWED_TYPE_PREFIXES = List.of(
      "java.io.",
      "java.net.",
      "java.nio.",
      "java.nio.channels.",
      "java.nio.file.",
      "java.lang.reflect.",
      "groovy.io."
  );
  private static final Set<String> DISALLOWED_PROPERTY_NAMES = Set.of(
      "class",
      "classLoader",
      "metaClass"
  );

  private SandboxPolicy() {
  }

  public static Binding createBinding() {
    return new Binding();
  }

  public static List<String> disallowedImports() {
    return DISALLOWED_IMPORTS;
  }

  public static List<String> disallowedStarImports() {
    return DISALLOWED_STAR_IMPORTS;
  }

  public static List<String> disallowedStaticImports() {
    return DISALLOWED_STATIC_IMPORTS;
  }

  public static Set<String> disabledGlobalAstTransformations() {
    return DISABLED_GLOBAL_AST_TRANSFORMATIONS;
  }

  public static Set<String> disallowedPropertyNames() {
    return DISALLOWED_PROPERTY_NAMES;
  }

  public static boolean blocksType(String typeName) {
    if (typeName == null || typeName.isBlank()) {
      return false;
    }

    if (DISALLOWED_EXACT_TYPES.contains(typeName) || DISALLOWED_SIMPLE_TYPES.contains(typeName)) {
      return true;
    }

    for (String prefix : DISALLOWED_TYPE_PREFIXES) {
      if (typeName.startsWith(prefix)) {
        return true;
      }
    }

    return false;
  }
}
