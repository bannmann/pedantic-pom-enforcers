/*
 * Copyright (c) 2012 by The Author(s)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ferstl.maven.pomenforcers.priority;

import java.util.ArrayList;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.github.ferstl.maven.pomenforcers.priority.PriorityOrdering;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;


import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;


public class PriorityOrderingTest {


  @Test
  public void testCompare() {
    ArrayList<String> priorizedItems = Lists.newArrayList("z", "y", "x");
    Function<String, String> transformer = Functions.identity();
    PriorityOrdering<String, String> testComparator = new PriorityOrdering<>(priorizedItems, transformer);

    // x is in the priority list, a isn't -> a > x
    assertThat(testComparator.compare("a", "x"), greaterThan(0));
    // x is located after y in the priority list -> x > y
    assertThat(testComparator.compare("x", "y"), greaterThan(0));
    // x is located after y in the priority list -> y < x
    assertThat(testComparator.compare("y", "x"), lessThan(0));
    // equality applies to values in the priority list
    assertThat(testComparator.compare("x", "x"), Matchers.is(0));
    // regular comparison for values that are not in the priority list
    assertThat(testComparator.compare("b", "c"), lessThan(0));
    assertThat(testComparator.compare("b", "b"), is(0));
    assertThat(testComparator.compare("c", "b"), greaterThan(0));
  }

  @Test
  public void testCompareWithoutPriorities() {
    ArrayList<String> priorizedItems = Lists.newArrayList();
    Function<String, String> identity = Functions.identity();
    PriorityOrdering<String, String> testComparator = new PriorityOrdering<>(priorizedItems, identity);

    assertThat(testComparator.compare("a", "b"), lessThan(0));
    assertThat(testComparator.compare("a", "a"), is(0));
    assertThat(testComparator.compare("b", "a"), greaterThan(0));
  }


}