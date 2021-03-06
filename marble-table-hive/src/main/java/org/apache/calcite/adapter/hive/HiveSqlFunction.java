/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.adapter.hive;

import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlOperandCountRanges;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.util.Util;

import java.util.Objects;

/**
 * hive function with no argument checker,since a hive function is NOT
 * deterministic (i.e. it can accept different types of input operands).
 */
public class HiveSqlFunction extends SqlFunction {

  public HiveSqlFunction(String name,
      SqlReturnTypeInference returnTypeInference) {
    this(new SqlIdentifier(name, SqlParserPos.ZERO), SqlKind.OTHER_FUNCTION,
        returnTypeInference, SqlFunctionCategory.USER_DEFINED_FUNCTION);
  }

  public HiveSqlFunction(SqlIdentifier sqlIdentifier, SqlKind kind,
      SqlReturnTypeInference returnTypeInference,
      SqlFunctionCategory category) {
    super(Util.last(sqlIdentifier.names), sqlIdentifier, kind,
        returnTypeInference, null, ArgChecker.INSTANCE,
        null, category);
  }

  @Override public boolean equals(Object obj) {
    if (!(obj instanceof SqlOperator)) {
      return false;
    }
    if (!obj.getClass().equals(this.getClass())) {
      return false;
    }
    SqlOperator other = (SqlOperator) obj;
    return getName().equals(other.getName())
        && kind == other.kind
        && getSyntax().equals(other.getSyntax());
  }

  @Override public int hashCode() {
    return Objects.hash(kind, getName(), getSyntax());
  }

  @Override public boolean isDeterministic() {
    return false;
  }

  /**
   * Argument Checker for variable number of arguments
   */
  public static class ArgChecker implements SqlOperandTypeChecker {

    public static final ArgChecker INSTANCE = new ArgChecker();

    private SqlOperandCountRange range = SqlOperandCountRanges.any();

    @Override public boolean checkOperandTypes(SqlCallBinding callBinding,
        boolean throwOnFailure) {
      return true;
    }

    @Override public SqlOperandCountRange getOperandCountRange() {
      return range;
    }

    @Override public String getAllowedSignatures(SqlOperator op,
        String opName) {
      return opName + "(HiveOperator)";
    }

    @Override public Consistency getConsistency() {
      return Consistency.NONE;
    }

    @Override public boolean isOptional(int arg) {
      return false;
    }
  }
}

// End HiveSqlFunction.java
