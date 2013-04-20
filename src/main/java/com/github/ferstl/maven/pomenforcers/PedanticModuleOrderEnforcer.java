/*
 * Copyright (c) 2012 by The Author(s)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ferstl.maven.pomenforcers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import static com.github.ferstl.maven.pomenforcers.ErrorReport.toList;


/**
 * This enforcer makes sure that your <code>modules</code> section is sorted
 * alphabetically. Modules that should occur at a specific position in the
 * <code>&lt;modules&gt;</code> section can be ignored.
 *
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;moduleOrder implementation=&quot;ch.sferstl.maven.pomenforcer.PedanticModuleOrderEnforcer&quot;&gt;
 *         &lt;!-- These modules may occur at any place in the modules section --&gt;
 *         &lt;ignoredModules&gt;dist-deb,dist-rpm&lt;/ignoredModules&gt;
 *        &lt;/moduleOrder&gt;
 *     &lt;/rules&gt;
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#MODULE_ORDER}
 */
public class PedanticModuleOrderEnforcer extends AbstractPedanticEnforcer {

  /** All modules in this set won't be checked for the correct order. */
  private final Set<String> ignoredModules;

  public PedanticModuleOrderEnforcer() {
    this.ignoredModules = Sets.newLinkedHashSet();
  }

  /**
   * Comma-separated list of ignored modules. All modules in this list may occur at any place in the
   * <code>modules</code> section.
   * @param ignoredModules Comma-separated list of ignored modules.
   * @configParam
   */
  public void setIgnoredModules(String ignoredModules) {
    CommaSeparatorUtils.splitAndAddToCollection(ignoredModules, this.ignoredModules);
  }

  @Override
  protected void doEnforce() throws EnforcerRuleException {
    MavenProject project = EnforcerRuleUtils.getMavenProject(getHelper());
    // Do nothing if the project is not a parent project
    if (!isPomProject(project)) {
      return;
    }

    Log log = getLog();
    log.debug("Enforcing alphabetical module order.");
    log.debug("  -> These modules are ignored: " + CommaSeparatorUtils.join(this.ignoredModules));

    // Remove all modules to be ignored.
    List<String> declaredModules = new ArrayList<>(getProjectModel().getModules());
    declaredModules.removeAll(this.ignoredModules);

    // Enforce the module order
    Ordering<String> moduleOrdering = Ordering.natural();
    if (!moduleOrdering.isOrdered(declaredModules)) {
      ErrorReport report = createErrorReport(moduleOrdering.immutableSortedCopy(declaredModules));
      throw new EnforcerRuleException(report.toString());
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  private boolean isPomProject(MavenProject project) {
    return "pom".equals(project.getPackaging());
  }

  private ErrorReport createErrorReport(List<String> orderedModules) {
    ErrorReport report = new ErrorReport(PedanticEnforcerRule.MODULE_ORDER)
      .addLine("You have to sort your modules alphabetically:")
      .addLine(toList(orderedModules));
    if (!this.ignoredModules.isEmpty()) {
      report.emptyLine()
            .addLine("You may place these modules anywhere in your <modules> section:")
            .addLine(toList(this.ignoredModules));
    }
    return report;
  }
}
