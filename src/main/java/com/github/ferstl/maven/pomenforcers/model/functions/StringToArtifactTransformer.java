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
package com.github.ferstl.maven.pomenforcers.model.functions;

import java.util.ArrayList;

import com.github.ferstl.maven.pomenforcers.model.ArtifactModel;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class StringToArtifactTransformer implements Function<String, ArtifactModel> {

  private static final Splitter COLON_SPLITTER = Splitter.on(":");
  public static final StringToArtifactTransformer INSTANCE = new StringToArtifactTransformer();

  public static Function<String, ArtifactModel> stringToArtifactModel() {
    return INSTANCE;
  }

 @Override
 public ArtifactModel apply(String input) {
   ArrayList<String> artifactElements = Lists.newArrayList(COLON_SPLITTER.split(input));

   if(artifactElements.size() != 2) {
     throw new IllegalArgumentException("Cannot read POM information: " + input);
   }

   return new ArtifactModel(artifactElements.get(0), artifactElements.get(1));
  }


  private StringToArtifactTransformer() {}
}