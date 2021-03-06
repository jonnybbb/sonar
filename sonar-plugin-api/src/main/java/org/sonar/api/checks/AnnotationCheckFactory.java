/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2011 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.api.checks;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.api.utils.SonarException;
import org.sonar.check.Check;
import org.sonar.check.CheckProperty;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * @since 2.3
 */
public final class AnnotationCheckFactory extends CheckFactory {

  private Map<String, Class> checkClassesByKey = Maps.newHashMap();

  private AnnotationCheckFactory(RulesProfile profile, String repositoryKey, Collection<Class> checkClasses) {
    super(profile, repositoryKey);
    groupClassesByKey(checkClasses);
  }

  public static AnnotationCheckFactory create(RulesProfile profile, String repositoryKey, Collection<Class> checkClasses) {
    AnnotationCheckFactory factory = new AnnotationCheckFactory(profile, repositoryKey, checkClasses);
    factory.init();
    return factory;
  }

  private void groupClassesByKey(Collection<Class> checkClasses) {
    for (Class checkClass : checkClasses) {
      String key = getRuleKey(checkClass);
      if (key != null) {
        checkClassesByKey.put(key, checkClass);
      }
    }
  }

  protected Object createCheck(ActiveRule activeRule) {
    Class clazz = checkClassesByKey.get(activeRule.getConfigKey());
    if (clazz != null) {
      return instantiate(activeRule, clazz);
    }
    return null;
  }

  private Object instantiate(ActiveRule activeRule, Class clazz) {
    try {
      Object check = clazz.newInstance();
      configureFields(activeRule, check);
      return check;

    } catch (InstantiationException e) {
      throw new SonarException("Can not instantiate the check related to the rule " + activeRule, e);

    } catch (IllegalAccessException e) {
      throw new SonarException("Can not instantiate the check related to the rule " + activeRule, e);
    }
  }

  private void configureFields(ActiveRule activeRule, Object check) {
    for (ActiveRuleParam param : activeRule.getActiveRuleParams()) {
      Field field = getField(check, param.getKey());
      if (field == null) {
        throw new SonarException("The field " + param.getKey() + " does not exist or is not annotated with @RuleProperty in the class " + check.getClass().getName());
      }
      if (StringUtils.isNotBlank(param.getValue())) {
        configureField(check, field, param.getValue());
      }
    }

  }

  private void configureField(Object check, Field field, String value) {
    try {
      field.setAccessible(true);

      if (field.getType().equals(String.class)) {
        field.set(check, value);

      } else if ("int".equals(field.getType().getSimpleName())) {
        field.setInt(check, Integer.parseInt(value));

      } else if ("short".equals(field.getType().getSimpleName())) {
        field.setShort(check, Short.parseShort(value));

      } else if ("long".equals(field.getType().getSimpleName())) {
        field.setLong(check, Long.parseLong(value));

      } else if ("double".equals(field.getType().getSimpleName())) {
        field.setDouble(check, Double.parseDouble(value));

      } else if ("boolean".equals(field.getType().getSimpleName())) {
        field.setBoolean(check, Boolean.parseBoolean(value));

      } else if ("byte".equals(field.getType().getSimpleName())) {
        field.setByte(check, Byte.parseByte(value));

      } else if (field.getType().equals(Integer.class)) {
        field.set(check, Integer.parseInt(value));

      } else if (field.getType().equals(Long.class)) {
        field.set(check, Long.parseLong(value));

      } else if (field.getType().equals(Double.class)) {
        field.set(check, Double.parseDouble(value));

      } else if (field.getType().equals(Boolean.class)) {
        field.set(check, Boolean.parseBoolean(value));

      } else {
        throw new SonarException("The type of the field " + field + " is not supported: " + field.getType());
      }
    } catch (IllegalAccessException e) {
      throw new SonarException("Can not set the value of the field " + field + " in the class: " + check.getClass().getName(), e);
    }
  }

  private Field getField(Object check, String key) {
    Field[] fields = check.getClass().getDeclaredFields();
    for (Field field : fields) {
      RuleProperty propertyAnnotation = field.getAnnotation(RuleProperty.class);
      if (propertyAnnotation != null) {
        if (StringUtils.equals(key, field.getName()) || StringUtils.equals(key, propertyAnnotation.key())) {
          return field;
        }
      } else {
        CheckProperty checkAnnotation = field.getAnnotation(CheckProperty.class);
        if (checkAnnotation != null) {
          if (StringUtils.equals(key, field.getName()) || StringUtils.equals(key, checkAnnotation.key())) {
            return field;
          }
        }
      }
    }
    return null;
  }

  private String getRuleKey(Class annotatedClass) {
    String key = null;
    Rule ruleAnnotation = (Rule) annotatedClass.getAnnotation(Rule.class);
    if (ruleAnnotation != null) {
      key = ruleAnnotation.key();
    } else {
      Check checkAnnotation = (Check) annotatedClass.getAnnotation(Check.class);
      if (checkAnnotation != null) {
        key = checkAnnotation.key();

      }
    }
    return StringUtils.defaultIfEmpty(key, annotatedClass.getCanonicalName());
  }
}
