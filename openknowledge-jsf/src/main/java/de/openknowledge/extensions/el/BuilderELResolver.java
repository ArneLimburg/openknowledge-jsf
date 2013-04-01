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

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.el.BeanELResolver;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;

public class BuilderELResolver extends BeanELResolver {

  private ConcurrentMap<Class<?>, Map<String, FeatureDescriptor>> features = new ConcurrentHashMap<Class<?>, Map<String, FeatureDescriptor>>();

  @Override
  public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object object) {
    if (object == null) {
      return null;
    }
    if (features.containsKey(object.getClass())) {
      return features.get(object.getClass()).values().iterator();
    }
    try {
      BeanInfo info = Introspector.getBeanInfo(object.getClass());
      Map<String, FeatureDescriptor> featureDescriptors = new HashMap<String, FeatureDescriptor>();
      for (PropertyDescriptor propertyDescriptor : info.getPropertyDescriptors()) {
        propertyDescriptor.setValue("type", propertyDescriptor.getPropertyType());
        propertyDescriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        if (propertyDescriptor.getWriteMethod() == null) {
          for (MethodDescriptor methodDescriptor : info.getMethodDescriptors()) {
            Class<?>[] parameterTypes = methodDescriptor.getMethod().getParameterTypes();
            if (parameterTypes.length != 1) {
              continue;
            }
            if (!parameterTypes[0].equals(propertyDescriptor.getPropertyType())) {
              continue;
            }
            if (!methodDescriptor.getName().startsWith("_") && methodDescriptor.getName().toLowerCase().endsWith(propertyDescriptor.getName().toLowerCase())) {
              propertyDescriptor.setWriteMethod(methodDescriptor.getMethod());
              break;
            }
          }
        }
        featureDescriptors.put(propertyDescriptor.getName(), propertyDescriptor);
      }
      features.put(object.getClass(), featureDescriptors);
      return featureDescriptors.values().iterator();
    } catch (IntrospectionException e) {
      return null;
    }
  }

  @Override
  public boolean isReadOnly(ELContext context, Object object, Object property)
      throws NullPointerException, PropertyNotFoundException, ELException {
    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(object, property);
    if (propertyDescriptor == null) {
      return super.isReadOnly(context, object, property);
    }
    context.setPropertyResolved(true);
    return propertyDescriptor.getWriteMethod() == null;
  }

  @Override
  public void setValue(ELContext context, Object object, Object property, Object value)
      throws NullPointerException, PropertyNotFoundException, PropertyNotWritableException, ELException {
    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(object, property);
    if (propertyDescriptor == null || propertyDescriptor.getWriteMethod() == null) {
      super.setValue(context, object, property, value);
    }
    try {
      Method writeMethod = propertyDescriptor.getWriteMethod();
      Method method = object.getClass().getMethod(writeMethod.getName(), writeMethod.getParameterTypes());
      method.invoke(object, new Object[] { value });
      context.setPropertyResolved(true);
    } catch (ELException e) {
      throw e;
    } catch (InvocationTargetException e) {
      throw new ELException(e.getCause());
    } catch (Exception e) {
      new ELException(e);
    }
  }

  private PropertyDescriptor getPropertyDescriptor(Object object, Object property) {
    Map<String, FeatureDescriptor> properties = features.get(object.getClass());
    if (properties == null) {
      getFeatureDescriptors(null, object);
      properties = features.get(object.getClass());
      if (properties == null) {
        return null;
      }
    }
    FeatureDescriptor feature = properties.get(property);
    if (!(feature instanceof PropertyDescriptor)) {
      return null;
    }
    return (PropertyDescriptor) feature;
  }
}
