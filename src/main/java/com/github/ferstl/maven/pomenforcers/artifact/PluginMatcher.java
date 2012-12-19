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
package com.github.ferstl.maven.pomenforcers.artifact;

import java.util.Collection;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Plugin;

import com.google.common.base.Function;

import static com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils.evaluateProperties;
import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Collections2.transform;

public class PluginMatcher {

  private final MatchFunction matchFunction;

  public PluginMatcher(Collection<Plugin> superset, EnforcerRuleHelper helper) {
    this.matchFunction = new MatchFunction(superset, helper);
  }

  public Collection<Plugin> match(Collection<Plugin> subset) {
    return transform(subset, this.matchFunction);
  }


  private static class MatchFunction implements Function<Plugin, Plugin> {

    private final Collection<Plugin> superset;
    private final EnforcerRuleHelper helper;

    public MatchFunction(Collection<Plugin> superset, EnforcerRuleHelper helper) {
      this.superset = superset;
      this.helper = helper;
    }

    @Override
    public Plugin apply(Plugin plugin) {
      String groupId = evaluateProperties(plugin.getGroupId(), this.helper);
      String artifactId = evaluateProperties(plugin.getArtifactId(), this.helper);
      for (Plugin supdersetDependency : this.superset) {
        if (equal(supdersetDependency.getGroupId(), groupId)
         && equal(supdersetDependency.getArtifactId(), artifactId)) {
          return supdersetDependency;
        }
      }
      throw new IllegalStateException(
          "Could not match plugin '" + plugin + "' with superset '." + this.superset + "'.");
    }
  }
}