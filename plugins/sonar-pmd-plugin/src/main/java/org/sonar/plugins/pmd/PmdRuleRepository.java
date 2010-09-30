/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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
package org.sonar.plugins.pmd;

import org.sonar.api.platform.ServerFileSystem;
import org.sonar.api.resources.Java;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.rules.XMLRuleParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class PmdRuleRepository extends RuleRepository {

  // for user extensions
  private ServerFileSystem fileSystem;

  public PmdRuleRepository(ServerFileSystem fileSystem) {
    super(PmdConstants.REPOSITORY_KEY, Java.KEY);
    setName(PmdConstants.REPOSITORY_NAME);
    this.fileSystem = fileSystem;
  }

  @Override
  public List<Rule> createRules() {
    List<Rule> rules = new ArrayList<Rule>();
    rules.addAll(XMLRuleParser.parseXML(getClass().getResourceAsStream("/org/sonar/plugins/pmd/rules.xml")));
    for (File userExtensionXml : fileSystem.getExtensions(PmdConstants.REPOSITORY_KEY, "xml")) {
      rules.addAll(XMLRuleParser.parseXML(userExtensionXml));
    }
    return rules;
  }
}
