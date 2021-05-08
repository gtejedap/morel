/*
 * Licensed to Julian Hyde under one or more contributor license
 * agreements.  See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Julian Hyde licenses this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.  You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package net.hydromatic.morel.ast;

import org.apache.calcite.util.Util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

import net.hydromatic.morel.eval.Applicable;
import net.hydromatic.morel.eval.Unit;
import net.hydromatic.morel.type.DataType;
import net.hydromatic.morel.type.FnType;
import net.hydromatic.morel.type.ListType;
import net.hydromatic.morel.type.PrimitiveType;
import net.hydromatic.morel.type.RecordLikeType;
import net.hydromatic.morel.type.RecordType;
import net.hydromatic.morel.type.Type;
import net.hydromatic.morel.type.TypeSystem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/** Builds parse tree nodes. */
public enum CoreBuilder {
  /** The singleton instance of the CORE builder.
   * The short name is convenient for use via 'import static',
   * but checkstyle does not approve. */
  // CHECKSTYLE: IGNORE 1
  core;

  private final Core.LiteralPat truePat =
      literalPat(Op.BOOL_LITERAL_PAT, PrimitiveType.BOOL, Boolean.TRUE);

  private final Core.WildcardPat boolWildcardPat =
      wildcardPat(PrimitiveType.BOOL);

  /** Creates a {@code boolean} literal. */
  public Core.Literal boolLiteral(boolean b) {
    return new Core.Literal(Op.BOOL_LITERAL, PrimitiveType.BOOL, b);
  }

  /** Creates a {@code char} literal. */
  public Core.Literal charLiteral(char c) {
    return new Core.Literal(Op.CHAR_LITERAL, PrimitiveType.CHAR, c);
  }

  /** Creates an {@code int} literal. */
  public Core.Literal intLiteral(BigDecimal value) {
    return new Core.Literal(Op.INT_LITERAL, PrimitiveType.INT, value);
  }

  /** Creates a {@code float} literal. */
  public Core.Literal realLiteral(BigDecimal value) {
    return new Core.Literal(Op.REAL_LITERAL, PrimitiveType.REAL, value);
  }

  /** Creates a string literal. */
  public Core.Literal stringLiteral(String value) {
    return new Core.Literal(Op.STRING_LITERAL, PrimitiveType.STRING, value);
  }

  /** Creates a unit literal. */
  public Core.Literal unitLiteral() {
    return new Core.Literal(Op.UNIT_LITERAL, PrimitiveType.UNIT, Unit.INSTANCE);
  }

  /** Creates a function literal. */
  public Core.Literal functionLiteral(Op op) {
    return new Core.Literal(Op.FN_LITERAL, PrimitiveType.UNIT, op);
  }

  /** Creates a reference to a value. */
  public Core.Id id(Type type, String name) {
    return new Core.Id(name, type);
  }

  public Core.RecordSelector recordSelector(FnType fnType, String name) {
    return new Core.RecordSelector(fnType, name);
  }

  public Core.Pat idPat(Type type, String name) {
    return new Core.IdPat(type, name);
  }

  @SuppressWarnings("rawtypes")
  public Core.LiteralPat literalPat(Op op, Type type, Comparable value) {
    return new Core.LiteralPat(op, type, value);
  }

  public Core.WildcardPat wildcardPat(Type type) {
    return new Core.WildcardPat(type);
  }

  public Core.ConPat consPat(Type type, String tyCon, Core.Pat pat) {
    return new Core.ConPat(Op.CONS_PAT, type, tyCon, pat);
  }

  public Core.ConPat conPat(Type type, String tyCon, Core.Pat pat) {
    return new Core.ConPat(type, tyCon, pat);
  }

  public Core.Con0Pat con0Pat(DataType type, String tyCon) {
    return new Core.Con0Pat(type, tyCon);
  }

  public Core.TuplePat tuplePat(Type type, Iterable<? extends Core.Pat> args) {
    return new Core.TuplePat(type, ImmutableList.copyOf(args));
  }

  public Core.TuplePat tuplePat(Type type, Core.Pat... args) {
    return new Core.TuplePat(type, ImmutableList.copyOf(args));
  }

  public Core.ListPat listPat(Type type, Iterable<? extends Core.Pat> args) {
    return new Core.ListPat(type, ImmutableList.copyOf(args));
  }

  public Core.ListPat listPat(Type type, Core.Pat... args) {
    return new Core.ListPat(type, ImmutableList.copyOf(args));
  }

  public Core.RecordPat recordPat(RecordType type,
      List<? extends Core.Pat> args) {
    return new Core.RecordPat(type, ImmutableList.copyOf(args));
  }

  public Core.Tuple tuple(RecordLikeType type, Iterable<? extends Core.Exp> args) {
    return new Core.Tuple(type, ImmutableList.copyOf(args));
  }

  public Core.Tuple tuple(RecordLikeType type, Core.Exp... args) {
    return new Core.Tuple(type, ImmutableList.copyOf(args));
  }

  /** As {@link #tuple(RecordLikeType, Iterable)}, but derives type. */
  public Core.Tuple tuple(TypeSystem typeSystem, Iterable<? extends Core.Exp> args) {
    final ImmutableList<Core.Exp> argList = ImmutableList.copyOf(args);
    final RecordLikeType tupleType =
        typeSystem.tupleType(Util.transform(argList, a -> a.type));
    return new Core.Tuple(tupleType, argList);
  }

  public Core.LetExp let(Core.Decl decl, Core.Exp e) {
    return new Core.LetExp(decl, e);
  }

  public Core.ValDecl valDecl(boolean rec, Core.Pat pat, Core.Exp e) {
    return new Core.ValDecl(rec, pat, e);
  }

  public Core.Match match(Core.Pat pat, Core.Exp e) {
    return new Core.Match(pat, e);
  }

  public Core.Case caseOf(Type type, Core.Exp e,
      Iterable<? extends Core.Match> matchList) {
    return new Core.Case(type, e, ImmutableList.copyOf(matchList));
  }

  public Core.From from(ListType type, Map<Core.Pat, Core.Exp> sources,
      List<Core.FromStep> steps, Core.Exp yieldExp) {
    return new Core.From(type, ImmutableMap.copyOf(sources),
        ImmutableList.copyOf(steps), yieldExp);
  }

  public Core.Fn fn(FnType type, Core.Match... matchList) {
    return new Core.Fn(type, ImmutableList.copyOf(matchList));
  }

  public Core.Fn fn(FnType type, Iterable<? extends Core.Match> matchList) {
    return new Core.Fn(type, ImmutableList.copyOf(matchList));
  }

  public Core.Apply apply(Type type, Core.Exp fn, Core.Exp arg) {
    return new Core.Apply(type, fn, arg);
  }

  public Core.Case ifThenElse(Core.Exp condition, Core.Exp ifTrue,
      Core.Exp ifFalse) {
    // Translate "if c then a else b"
    // as if user had written "case c of true => a | _ => b"
    return new Core.Case(ifTrue.type, condition,
        ImmutableList.of(match(truePat, ifTrue),
            match(boolWildcardPat, ifFalse)));
  }

  public Core.DatatypeDecl datatypeDecl(Iterable<DataType> dataTypes) {
    return new Core.DatatypeDecl(ImmutableList.copyOf(dataTypes));
  }

  public Core.Aggregate aggregate(Type type, Core.Exp aggregate,
      @Nullable Core.Exp argument) {
    return new Core.Aggregate(type, aggregate, argument);
  }

  public Core.Order order(Iterable<Core.OrderItem> orderItems) {
    return new Core.Order(ImmutableList.copyOf(orderItems));
  }

  public Core.OrderItem orderItem(Core.Exp exp, Ast.Direction direction) {
    return new Core.OrderItem(exp, direction);
  }

  public Core.Group group(Map<String, Core.Exp> groupExps,
      Map<String, Core.Aggregate> aggregates) {
    return new Core.Group(ImmutableSortedMap.copyOf(groupExps),
        ImmutableSortedMap.copyOf(aggregates));
  }

  public Core.FromStep where(Core.Exp exp) {
    return new Core.Where(exp);
  }

  public Core.ApplicableExp wrapApplicable(FnType fnType, Applicable applicable) {
    return new Core.ApplicableExp(applicable);
  }

}

// End CoreBuilder.java
