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

package org.apache.streampipes.connect.transformer.groovy.sandbox.ast;

import org.apache.streampipes.connect.transformer.groovy.sandbox.error.SandboxViolationException;
import org.apache.streampipes.connect.transformer.groovy.sandbox.SandboxPolicy;

import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.control.SourceUnit;

public final class AstChecksVisitor extends ClassCodeVisitorSupport {

  private final SourceUnit sourceUnit;

  public AstChecksVisitor(SourceUnit sourceUnit) {
    this.sourceUnit = sourceUnit;
  }

  @Override
  protected SourceUnit getSourceUnit() {
    return sourceUnit;
  }

  @Override
  public void visitConstructorCallExpression(ConstructorCallExpression call) {
    rejectForbiddenType(call.getType(), "constructor access");
    super.visitConstructorCallExpression(call);
  }

  @Override
  public void visitClassExpression(ClassExpression expression) {
    rejectForbiddenType(expression.getType(), "class access");
    super.visitClassExpression(expression);
  }

  @Override
  public void visitMethodCallExpression(MethodCallExpression call) {
    rejectForbiddenExpression(call.getObjectExpression(), "method access");
    super.visitMethodCallExpression(call);
  }

  @Override
  public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
    rejectForbiddenType(call.getOwnerType(), "static method access");
    super.visitStaticMethodCallExpression(call);
  }

  @Override
  public void visitPropertyExpression(PropertyExpression expression) {
    String propertyName = expression.getPropertyAsString();
    if (propertyName != null && SandboxPolicy.disallowedPropertyNames().contains(propertyName)) {
      throw new SandboxViolationException(
          SandboxPolicy.VIOLATION_MESSAGE + ": access to property '" + propertyName + "' is forbidden");
    }

    rejectForbiddenExpression(expression.getObjectExpression(), "property access");
    super.visitPropertyExpression(expression);
  }

  private void rejectForbiddenExpression(Expression expression, String operation) {
    if (expression instanceof ClassExpression classExpression) {
      rejectForbiddenType(classExpression.getType(), operation);
    } else if (expression instanceof VariableExpression variableExpression) {
      if (SandboxPolicy.blocksType(variableExpression.getName())) {
        throw new SandboxViolationException(
            SandboxPolicy.VIOLATION_MESSAGE + ": access to class '" + variableExpression.getName() + "' is forbidden");
      }
    } else {
      rejectForbiddenType(expression.getType(), operation);
    }
  }

  private void rejectForbiddenType(ClassNode classNode, String operation) {
    if (classNode != null) {
      rejectForbiddenType(classNode.getName(), operation);
    }
  }

  private void rejectForbiddenType(String typeName, String operation) {
    if (SandboxPolicy.blocksType(typeName)) {
      throw new SandboxViolationException(
          SandboxPolicy.VIOLATION_MESSAGE + ": " + operation + " to '" + typeName + "' is forbidden");
    }
  }
}
