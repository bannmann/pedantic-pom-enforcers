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

import com.google.common.base.Function;

import static com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils.evaluateProperties;
import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Collections2.transform;


public class DependencyMatcher {

  private final MatchFunction matchFunction;

  public DependencyMatcher(Collection<DependencyInfo> superset, EnforcerRuleHelper helper) {
    this.matchFunction = new MatchFunction(superset, helper);
  }

  public Collection<DependencyInfo> match(Collection<DependencyInfo> subset) {
    return transform(subset, this.matchFunction);
  }

  private static class MatchFunction implements Function<DependencyInfo, DependencyInfo> {

    private final Collection<DependencyInfo> superset;
    private final EnforcerRuleHelper helper;

    public MatchFunction(Collection<DependencyInfo> superset, EnforcerRuleHelper helper) {
      this.superset = superset;
      this.helper = helper;
    }

    @Override
    public DependencyInfo apply(DependencyInfo dependency) {
      String groupId = evaluateProperties(dependency.getGroupId(), this.helper);
      String artifactId = evaluateProperties(dependency.getArtifactId(), this.helper);
      String classifier = evaluateProperties(dependency.getClassifier(), this.helper);
      for (DependencyInfo supersetDependency : this.superset) {
        if (equal(supersetDependency.getGroupId(), groupId)
         && equal(supersetDependency.getArtifactId(), artifactId)
         && equal(supersetDependency.getClassifier(), classifier)) {
          return new DependencyInfo(
              supersetDependency.getGroupId(),
              supersetDependency.getArtifactId(),
              supersetDependency.getVersion(),
              supersetDependency.getScope(),
              supersetDependency.getClassifier());
        }
      }
      throw new IllegalStateException(
          "Could not match dependency '" + dependency + "' with superset '." + this.superset + "'.");
    }

  }
}
