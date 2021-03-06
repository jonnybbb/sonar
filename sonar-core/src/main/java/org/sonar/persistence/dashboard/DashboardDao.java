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
package org.sonar.persistence.dashboard;

import org.apache.ibatis.session.SqlSession;
import org.sonar.api.BatchComponent;
import org.sonar.api.ServerComponent;
import org.sonar.persistence.MyBatis;

public class DashboardDao implements BatchComponent, ServerComponent {

  private MyBatis mybatis;

  public DashboardDao(MyBatis mybatis) {
    this.mybatis = mybatis;
  }

  public DashboardDto selectGlobalDashboard(String name) {
    SqlSession sqlSession = mybatis.openSession();
    try {
      DashboardMapper mapper = sqlSession.getMapper(DashboardMapper.class);
      return mapper.selectGlobalDashboard(name);
    } finally {
      sqlSession.close();
    }
  }

  public void insert(DashboardDto dashboardDto) {
    SqlSession session = mybatis.openSession();
    DashboardMapper dashboardMapper = session.getMapper(DashboardMapper.class);
    WidgetMapper widgetMapper = session.getMapper(WidgetMapper.class);
    WidgetPropertyMapper widgetPropertyMapper = session.getMapper(WidgetPropertyMapper.class);
    try {
      dashboardMapper.insert(dashboardDto);
      for (WidgetDto widgetDto : dashboardDto.getWidgets()) {
        widgetDto.setDashboardId(dashboardDto.getId());
        widgetMapper.insert(widgetDto);
        for (WidgetPropertyDto widgetPropertyDto : widgetDto.getWidgetProperties()) {
          widgetPropertyDto.setWidgetId(widgetDto.getId());
          widgetPropertyMapper.insert(widgetPropertyDto);
        }
      }
      session.commit();
    } finally {
      session.close();
    }
  }

}
