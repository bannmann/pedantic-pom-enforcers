/*
 * Copyright (c) 2012 - 2020 the original author or authors.
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
package com.github.ferstl.maven.pomenforcers.model;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ArtifactModelTest {

  @Test(expected = IllegalArgumentException.class)
  public void constructorWithInvalidWildcard() {
    new ArtifactModel("group", "invalid*wildcard*positions");
  }

  @Test
  public void toStringNoGroupId() {
    ArtifactModel model = new ArtifactModel(null, "artifact", "1.0.0");

    assertEquals(":artifact:1.0.0", model.toString());
  }

  @Test
  public void toStringNoVersion() {
    ArtifactModel model = new ArtifactModel("group", "artifact");

    assertEquals("group:artifact:", model.toString());
  }

  @Test
  public void toStringNoGroupIdAndNoVersion() {
    ArtifactModel model = new ArtifactModel(null, "artifact");

    assertEquals(":artifact:", model.toString());
  }

  @Test
  public void matchesWithFullWildcard() {
    wildcardTest("something", "*", true);
  }

  @Test
  public void matchesWithLeadingWildcard() {
    wildcardTest("prefix-foo", "*-foo", true);
  }

  @Test
  public void matchesWithTrailingWildcard() {
    wildcardTest("foo-suffix", "foo-*", true);
  }

  @Test
  public void matchesWithContainsWildcard() {
    wildcardTest("foo-contains-bar", "*contains*", true);
    wildcardTest("foo-something-bar", "*contains*", false);
  }

  @Test
  public void matchesWithMismatchingGroupId() {
    ArtifactModel model = new ArtifactModel("group", "artifact");
    ArtifactModel pattern = new ArtifactModel("*", "different-artifact");

    assertFalse(model.matches(pattern));
  }

  @Test
  public void matchesWithMismatchingArtifactId() {
    ArtifactModel model = new ArtifactModel("group", "artifact");
    ArtifactModel pattern = new ArtifactModel("different-group", "*");

    assertFalse(model.matches(pattern));
  }

  @Test
  public void matchesOnPatterns() {
    ArtifactModel pattern1 = new ArtifactModel("*", "artifact");
    ArtifactModel pattern1Copy = new ArtifactModel("*", "artifact");
    ArtifactModel pattern2 = new ArtifactModel("group", "*");

    assertFalse(pattern1.matches(pattern2));
    assertTrue(pattern1.matches(pattern1Copy));
  }

  @Test
  public void matchesOnSelf() {
    ArtifactModel pattern = new ArtifactModel("*", "artifact");

    assertTrue(pattern.matches(pattern));
  }

  @Test
  public void matchesOnNull() {
    ArtifactModel pattern = new ArtifactModel("*", "artifact");

    assertFalse(pattern.matches(null));
  }

  private void wildcardTest(String string, String pattern, boolean shouldMatch) {
    ArtifactModel groupIdModel = new ArtifactModel(string, "artifact");
    ArtifactModel groupIdPattern = new ArtifactModel(pattern, "artifact");

    ArtifactModel artifactIdModel = new ArtifactModel("group", string);
    ArtifactModel artifactIdPattern = new ArtifactModel("group", pattern);

    assertEquals(shouldMatch, artifactIdModel.matches(artifactIdPattern));
    assertEquals(shouldMatch, groupIdModel.matches(groupIdPattern));
  }
}
