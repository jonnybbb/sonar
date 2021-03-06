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
package org.sonar.plugins.core.sensors;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.rules.RulePriority;

final class SeverityUtils {
  private SeverityUtils() {
    // only static methods
  }

  static Metric severityToViolationMetric(RulePriority severity) {
    Metric metric;
    if (severity.equals(RulePriority.BLOCKER)) {
      metric = CoreMetrics.BLOCKER_VIOLATIONS;
    } else if (severity.equals(RulePriority.CRITICAL)) {
      metric = CoreMetrics.CRITICAL_VIOLATIONS;
    } else if (severity.equals(RulePriority.MAJOR)) {
      metric = CoreMetrics.MAJOR_VIOLATIONS;
    } else if (severity.equals(RulePriority.MINOR)) {
      metric = CoreMetrics.MINOR_VIOLATIONS;
    } else if (severity.equals(RulePriority.INFO)) {
      metric = CoreMetrics.INFO_VIOLATIONS;
    } else {
      throw new IllegalArgumentException("Unsupported severity: " + severity);
    }
    return metric;
  }
}
