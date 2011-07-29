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
package org.sonar.wsclient.services;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ManualMeasureCreateQueryTest extends QueryTestCase {

  @Test
  public void shouldCreateWithOnlyMandatoryProperties() {
    ManualMeasureCreateQuery query = ManualMeasureCreateQuery.create("foo", "team_size");
    assertThat(query.getUrl(), is("/api/manual_measures?resource=foo&metric=team_size&"));
    assertThat(query.getModelClass().getName(), is(ManualMeasure.class.getName()));
  }

  @Test
  public void shouldCreateWithAllOptionalProperties() {
    ManualMeasureCreateQuery query = ManualMeasureCreateQuery.create("foo", "team_size").setValue(3.14).setTextValue("xxx").setDescription("yyy");
    assertThat(query.getUrl(), is("/api/manual_measures?resource=foo&metric=team_size&val=3.14&text=xxx&desc=yyy&"));
    assertThat(query.getModelClass().getName(), is(ManualMeasure.class.getName()));
  }
}