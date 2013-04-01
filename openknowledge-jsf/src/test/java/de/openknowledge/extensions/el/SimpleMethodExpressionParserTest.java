/*
 * Copyright 2013 Arne Limburg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.openknowledge.extensions.el;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SimpleMethodExpressionParserTest {

  @Test
  public void simpleMethodExpression() {
    SimpleMethodExpression methodExpression = new SimpleMethodExpressionParser("#{pb.livingInStreet(pb.street.name, pb.street.number)}").parse();
    assertThat(methodExpression.getBase(), is("pb"));
    assertThat(methodExpression.getMethodName(), is("livingInStreet"));
    assertThat(methodExpression.getParameters().length, is(2));
    assertThat(methodExpression.getParameters()[0], is("pb.street.name"));
    assertThat(methodExpression.getParameters()[1], is("pb.street.number"));
  }
}
