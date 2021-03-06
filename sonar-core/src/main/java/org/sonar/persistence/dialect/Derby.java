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
package org.sonar.persistence.dialect;

import org.apache.commons.lang.StringUtils;
import org.hibernate.dialect.DerbyDialect;
import org.hibernate.id.IdentityGenerator;
import org.sonar.api.database.DatabaseProperties;

import java.sql.Types;

/**
 * @since 1.12
 */
public class Derby implements Dialect {

  public static final String ID = "derby";

  public String getId() {
    return ID;
  }

  public String getActiveRecordDialectCode() {
    return "derby";
  }

  public String getActiveRecordJdbcAdapter() {
    return "jdbc";
  }

  public Class<? extends org.hibernate.dialect.Dialect> getHibernateDialectClass() {
    return DerbyWithDecimalDialect.class;
  }

  public boolean matchesJdbcURL(String jdbcConnectionURL) {
    return StringUtils.startsWithIgnoreCase(jdbcConnectionURL, "jdbc:derby:");
  }

  public String getDefaultDriverClassName() {
    return "org.apache.derby.jdbc.ClientDriver";
  }

  public String getConnectionInitStatement(String schema) {
    return null;
  }

  public static class DerbyWithDecimalDialect extends DerbyDialect {
    public DerbyWithDecimalDialect() {
      super();
      registerColumnType(Types.DOUBLE, "decimal");
      registerColumnType(Types.VARCHAR, DatabaseProperties.MAX_TEXT_SIZE, "clob");
      registerColumnType(Types.VARBINARY, "blob");

      // Not possible to do alter column types in Derby
      registerColumnType(Types.BIGINT, "integer");

      registerColumnType(Types.BIT, "boolean");
    }

    @Override
    public String toBooleanValueString(boolean bool) {
      return bool ? "true" : "false";
    }

    /**
     * To be compliant with Oracle, we define on each model (ch.hortis.sonar.model classes)
     * a sequence generator. It works on mySQL because strategy = GenerationType.AUTO, so
     * it equals GenerationType.IDENTITY.
     * But on derby, AUTO becomes TABLE instead of IDENTITY. So we explicitly change this behavior.
     */
    @Override
    public Class getNativeIdentifierGeneratorClass() {
      return IdentityGenerator.class;
    }
  }

}
