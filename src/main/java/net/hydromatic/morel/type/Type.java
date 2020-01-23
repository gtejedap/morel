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
package net.hydromatic.morel.type;

import net.hydromatic.morel.ast.Op;

import java.util.function.Function;

/** Type. */
public interface Type {
  /** Description of the type, e.g. "{@code int}", "{@code int -> int}". */
  String description();

  /** Type operator. */
  Op op();

  /** Copies this type, applying a given transform to component types,
   * and returning the original type if the component types are unchanged. */
  Type copy(TypeSystem typeSystem, Function<Type, Type> transform);
}

// End Type.java