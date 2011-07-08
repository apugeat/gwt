/*
 * Copyright 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dom.builder.shared;

/**
 * HTML-based implementation of {@link VideoBuilder}.
 */
public class HtmlVideoBuilder extends HtmlMediaBuilderBase<VideoBuilder> implements VideoBuilder {

  HtmlVideoBuilder(HtmlBuilderImpl delegate) {
    super(delegate);
  }

  @Override
  public VideoBuilder height(int height) {
    return attribute("height", height);
  }

  @Override
  public VideoBuilder poster(String url) {
    return attribute("url", url);
  }

  @Override
  public VideoBuilder width(int width) {
    return attribute("width", width);
  }
}
