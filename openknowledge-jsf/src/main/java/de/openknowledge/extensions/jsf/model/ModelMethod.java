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
package de.openknowledge.extensions.jsf.model;

import static org.apache.commons.lang3.Validate.notNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import de.openknowledge.extensions.el.SimpleMethodExpression;
import de.openknowledge.extensions.el.SimpleMethodExpressionParser;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
@FacesComponent(ModelMethod.COMPONENT_TYPE)
public class ModelMethod extends UIInput {

  public static final String COMPONENT_TYPE = "de.openknowledge.ModelMethod";

  public ModelMethod() {
    setRendererType("de.openknowledge.EmtpyRenderer");
  }
  
  @Override
  public void validate(FacesContext context) {
  }
  
  @Override
  public void updateModel(FacesContext context) {
    ValueExpression expression = getValueExpression("value");
    SimpleMethodExpressionParser parser = new SimpleMethodExpressionParser(expression.getExpressionString());
    SimpleMethodExpression methodExpression = parser.parse();
    ExpressionFactory expressionFactory = notNull(context, "context may not be null").getApplication().getExpressionFactory();
    ValueExpression baseExpression = expressionFactory.createValueExpression(context.getELContext(), "#{" + methodExpression.getBase() + '}', Object.class);
    Object baseValue = baseExpression.getValue(context.getELContext());
    Class<? extends Object> baseType = baseValue.getClass();
    UIInput[] parameterComponents = findComponents(methodExpression);
    Class<?>[] parameterTypes = findParameterTypes(context, methodExpression, expressionFactory, parameterComponents);
    Method method = findMethod(baseType, methodExpression.getMethodName(), parameterTypes);
    Object[] parameters = findParameters(context, methodExpression, expressionFactory, parameterComponents, parameterTypes);
    try {
      method.invoke(baseValue, parameters);
    } catch (InvocationTargetException e) {
      throw new ELException(e.getTargetException());
    } catch (Exception e) {
      throw new ELException(e);
    }
  }

  private Object[] findParameters(
      FacesContext context,
      SimpleMethodExpression methodExpression,
      ExpressionFactory expressionFactory,
      UIInput[] parameterComponents,
      Class<?>[] parameterTypes) {
    Object[] parameters = new Object[parameterTypes.length];
    for (int i = 0; i < parameterTypes.length; i++) {
      if (parameterComponents[i] != null) {
        parameters[i] = parameterComponents[i].getSubmittedValue();
        if (parameters[i] == null) {
          parameters[i] = parameterComponents[i].getValue();
        }
      } else {
        parameters[i] = expressionFactory.createValueExpression(methodExpression.getParameters()[i], Object.class).getValue(context.getELContext());
      }
    }
    return parameters;
  }

  private Class<?>[] findParameterTypes(FacesContext context, SimpleMethodExpression methodExpression, ExpressionFactory expressionFactory, UIInput[] parameterComponents) {
    Class<?>[] parameterTypes = new Class[methodExpression.getParameters().length];
    for (int i = 0; i < parameterTypes.length; i++) {
      if (parameterComponents[i] != null) {
        Object value = parameterComponents[i].getSubmittedValue();
        if (value != null) {
          parameterTypes[i] = value.getClass();
        } else {
          value = parameterComponents[i].getValue();
          if (value != null) {
            parameterTypes[i] = value.getClass();
          }
        }
      }
      if (parameterTypes[i] == null) {
        Object paramterValue = expressionFactory.createValueExpression(methodExpression.getParameters()[i], Object.class).getValue(context.getELContext());
        parameterTypes[i] = paramterValue != null? paramterValue.getClass(): null;
      }
    }
    return parameterTypes;
  }

  private UIInput[] findComponents(SimpleMethodExpression methodExpression) {
    String[] parameters = methodExpression.getParameters();
    UIInput[] parameterComponents = new UIInput[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      parameterComponents[i] = findComponent(getParent(), "#{" + parameters[i].trim() + '}');
    }
    return parameterComponents;
  }

  private UIInput findComponent(UIComponent parent, String expression) {
    if (parent instanceof UIInput) {
      UIInput input = (UIInput)parent;
      if (input.getValueExpression("value").getExpressionString().equals(expression)) {
        return input;
      }
    }
    for (UIComponent child: parent.getChildren()) {
      UIInput input = findComponent(child, expression);
      if (input != null) {
        return input;
      }
    }
    return null;
  }

  private Method findMethod(Class<?> type, String name, Class<?>[] parameters) {
    for (Method method: type.getMethods()) {
      if (method.getName().equals(name)) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != parameters.length) {
          continue;
        }
        boolean match = true;
        for (int i = 0; i < parameters.length; i++) {
          if (parameters[i] != null && !parameterTypes[i].isAssignableFrom(parameters[i])) {
            match = false;
            break;
          }
        }
        if (match) {
          // TODO currently we simply take the first match... We should resolve based on java resolution rules...
          return method;
        }
      }
    }
    throw new ELException("method with name '" + name + "' not found in type " + type.getName());
  }
}
