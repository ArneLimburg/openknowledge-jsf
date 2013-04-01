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

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.List;

import javax.el.ELException;

public class SimpleMethodExpressionParser {

  private static final String[] TOKENS = new String[] {"#{", "${", "}", "(", ")", ".", ","};
  private String input;
  
  public SimpleMethodExpressionParser(String input) {
    this.input = notNull(input);
  }

  public SimpleMethodExpression parse() {
    // we parse from right to left
    consume("}");
    consume(")");
    String[] parameters = parseParameters();
    consume("(");
    String methodName = parseMethodName();
    consume(".");
    String base = parseExpression();
    return new SimpleMethodExpression(base, methodName, parameters);
  }

  private String[] parseParameters() {
    List<String> parameters = new ArrayList<String>();
    while (true) {
      parameters.add(0, parseExpression());
      if (lookAhead(",")) {
        consume(",");
      } else {
        break;
      }
    }
    return parameters.toArray(new String[parameters.size()]);
  }

  private String parseExpression() {
    if (lookAhead(")")) {
      return parseMethodExpression();
    } else {
      return parseValueExpression();
    }
  }

  private String parseMethodName() {
    return parseToken();
  }

  private String parseMethodExpression() {
    StringBuilder methodExpression = new StringBuilder();
    consume(")");
    String[] parameters = parseParameters();
    consume("(");
    String methodName = parseMethodName();
    consume(".");
    methodExpression.append(parseExpression()).append(".").append(methodName).append("(");
    for (String parameter: parameters) {
      methodExpression.append(parameter).append(", ");
    }
    methodExpression.deleteCharAt(methodExpression.length() - 1);
    methodExpression.setCharAt(methodExpression.length() - 1, ')');
    return methodExpression.toString().trim();
  }

  private String parseValueExpression() {
    String token = parseToken();
    if (!lookAhead(".")) {
      return token;
    }
    consume(".");
    return (parseExpression() + "." + token).trim();
  }

  private String parseToken() {
    int start = -1;
    String token = null;
    for (String t: TOKENS) {
      int index = input.lastIndexOf(t);
      if (index > start) {
        start = index;
        token = t;
      }
    }
    if (token == null) {
      throw new ELException("expected text, but found " + input);
    }
    String text = input.substring(start + token.length());
    input = input.substring(0, start + 1);
    return text;
  }

  private boolean lookAhead(String token) {
    return input.trim().endsWith(token);
  }

  private void consume(String token) {
    input = input.trim();
    if (!input.endsWith(token)) {
      throw new ELException("expected " + token + ", but found " + input);
    }
    input = input.substring(0, input.lastIndexOf(token));
  }
}
