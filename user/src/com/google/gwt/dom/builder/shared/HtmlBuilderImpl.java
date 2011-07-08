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

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.AreaElement;
import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.dom.client.BRElement;
import com.google.gwt.dom.client.BaseElement;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.DListElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FieldSetElement;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.FrameSetElement;
import com.google.gwt.dom.client.HRElement;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.LegendElement;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.MapElement;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.OListElement;
import com.google.gwt.dom.client.OptGroupElement;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.ParamElement;
import com.google.gwt.dom.client.PreElement;
import com.google.gwt.dom.client.QuoteElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.SourceElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.StyleElement;
import com.google.gwt.dom.client.TableCaptionElement;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableColElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.dom.client.TitleElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.dom.client.VideoElement;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * Implementation of methods in {@link ElementBuilderBase} used to render HTML
 * as a string, using innerHtml to generate an element.
 */
class HtmlBuilderImpl extends ElementBuilderImpl {

  /*
   * Common element builders, and those most likely to appear in a loop, are
   * created on initialization to avoid null checks. Less common element
   * builders are created lazily to avoid unnecessary object creation.
   */
  private HtmlAnchorBuilder anchorBuilder;
  private HtmlAreaBuilder areaBuilder;
  private HtmlAudioBuilder audioBuilder;
  private HtmlBaseBuilder baseBuilder;
  private HtmlBodyBuilder bodyBuilder;
  private HtmlBRBuilder brBuilder;
  private HtmlButtonBuilder buttonBuilder;
  private HtmlCanvasBuilder canvasBuilder;
  private final HtmlDivBuilder divBuilder = new HtmlDivBuilder(this);
  private HtmlDListBuilder dListBuilder;
  private final HtmlElementBuilder elementBuilder = new HtmlElementBuilder(this);
  private HtmlFieldSetBuilder fieldSetBuilder;
  private HtmlFormBuilder formBuilder;
  private HtmlFrameBuilder frameBuilder;
  private HtmlFrameSetBuilder frameSetBuilder;
  private HtmlHeadBuilder headBuilder;
  private HtmlHeadingBuilder headingBuilder;
  private HtmlHRBuilder hrBuilder;
  private HtmlIFrameBuilder iFrameBuilder;
  private HtmlImageBuilder imageBuilder;
  private final HtmlInputBuilder inputBuilder = new HtmlInputBuilder(this);
  private HtmlLabelBuilder labelBuilder;
  private HtmlLegendBuilder legendBuilder;
  private final HtmlLIBuilder liBuilder = new HtmlLIBuilder(this);
  private HtmlLinkBuilder linkBuilder;
  private HtmlMapBuilder mapBuilder;
  private HtmlMetaBuilder metaBuilder;
  private HtmlOListBuilder oListBuilder;
  private final HtmlOptionBuilder optionBuilder = new HtmlOptionBuilder(this);
  private HtmlOptGroupBuilder optGroupBuilder;
  private HtmlParagraphBuilder paragraphBuilder;
  private HtmlParamBuilder paramBuilder;
  private HtmlPreBuilder preBuilder;
  private HtmlQuoteBuilder quoteBuilder;
  private HtmlScriptBuilder scriptBuilder;
  private HtmlSelectBuilder selectBuilder;
  private HtmlSourceBuilder sourceBuilder;
  private final HtmlSpanBuilder spanBuilder = new HtmlSpanBuilder(this);
  private HtmlStyleBuilder styleBuilder;
  private final StylesBuilder stylesBuilder = new HtmlStylesBuilder(this);
  private HtmlTableBuilder tableBuilder;
  private final HtmlTableCellBuilder tableCellBuilder = new HtmlTableCellBuilder(this);
  private HtmlTableCaptionBuilder tableCaptionBuilder;
  private HtmlTableColBuilder tableColBuilder;
  private final HtmlTableRowBuilder tableRowBuilder = new HtmlTableRowBuilder(this);
  private HtmlTableSectionBuilder tableSectionBuilder;
  private HtmlTextAreaBuilder textAreaBuilder;
  private HtmlTitleBuilder titleBuilder;
  private HtmlUListBuilder uListBuilder;
  private HtmlVideoBuilder videoBuilder;

  /**
   * Used to builder the HTML string. We cannot use
   * {@link com.google.gwt.safehtml.shared.SafeHtmlBuilder} because it does some
   * rudimentary checks that the HTML tags are complete. Instead, we escape
   * values before appending them.
   */
  private final StringBuilder sb = new StringBuilder();

  /**
   * Return the HTML as a {@link SafeHtml} string.
   */
  public SafeHtml asSafeHtml() {
    // End all open tags.
    endAllTags();

    /*
     * sb is trusted because we only append trusted strings or escaped strings
     * to it.
     */
    return SafeHtmlUtils.fromTrustedString(sb.toString());
  }

  public void attribute(String name, String value) {
    assertCanAddAttributeImpl();
    sb.append(" ").append(escape(name)).append("=\"").append(escape(value)).append("\"");
  }

  public HtmlAnchorBuilder startAnchor() {
    if (anchorBuilder == null) {
      anchorBuilder = new HtmlAnchorBuilder(this);
    }
    return trustedStart(AnchorElement.TAG, anchorBuilder);
  }

  public HtmlAreaBuilder startArea() {
    if (areaBuilder == null) {
      areaBuilder = new HtmlAreaBuilder(this);
    }
    return trustedStart(AreaElement.TAG, areaBuilder);
  }

  public HtmlAudioBuilder startAudio() {
    if (audioBuilder == null) {
      audioBuilder = new HtmlAudioBuilder(this);
    }
    return trustedStart(AudioElement.TAG, audioBuilder);
  }

  public HtmlBaseBuilder startBase() {
    if (baseBuilder == null) {
      baseBuilder = new HtmlBaseBuilder(this);
    }
    return trustedStart(BaseElement.TAG, baseBuilder);
  }

  public HtmlQuoteBuilder startBlockQuote() {
    return startQuote(QuoteElement.TAG_BLOCKQUOTE);
  }

  public HtmlBodyBuilder startBody() {
    if (bodyBuilder == null) {
      bodyBuilder = new HtmlBodyBuilder(this);
    }
    return trustedStart(BodyElement.TAG, bodyBuilder);
  }

  public HtmlBRBuilder startBR() {
    if (brBuilder == null) {
      brBuilder = new HtmlBRBuilder(this);
    }
    return trustedStart(BRElement.TAG, brBuilder);
  }

  public InputBuilder startButtonInput() {
    return startInput(ButtonElement.TAG);
  }

  public HtmlCanvasBuilder startCanvas() {
    if (canvasBuilder == null) {
      canvasBuilder = new HtmlCanvasBuilder(this);
    }
    return trustedStart(CanvasElement.TAG, canvasBuilder);
  }

  public InputBuilder startCheckInput() {
    return startInput("check");
  }

  public HtmlTableColBuilder startCol() {
    return startTableCol(TableColElement.TAG_COL);
  }

  public HtmlTableColBuilder startColGroup() {
    return startTableCol(TableColElement.TAG_COLGROUP);
  }

  public HtmlDivBuilder startDiv() {
    return trustedStart(DivElement.TAG, divBuilder);
  }

  public HtmlDListBuilder startDList() {
    if (dListBuilder == null) {
      dListBuilder = new HtmlDListBuilder(this);
    }
    return trustedStart(DListElement.TAG, dListBuilder);
  }

  public HtmlFieldSetBuilder startFieldSet() {
    if (fieldSetBuilder == null) {
      fieldSetBuilder = new HtmlFieldSetBuilder(this);
    }
    return trustedStart(FieldSetElement.TAG, fieldSetBuilder);
  }

  public InputBuilder startFileInput() {
    return startInput("file");
  }

  public HtmlFormBuilder startForm() {
    if (formBuilder == null) {
      formBuilder = new HtmlFormBuilder(this);
    }
    return trustedStart(FormElement.TAG, formBuilder);
  }

  public HtmlFrameBuilder startFrame() {
    if (frameBuilder == null) {
      frameBuilder = new HtmlFrameBuilder(this);
    }
    return trustedStart(FrameElement.TAG, frameBuilder);
  }

  public HtmlFrameSetBuilder startFrameSet() {
    if (frameSetBuilder == null) {
      frameSetBuilder = new HtmlFrameSetBuilder(this);
    }
    return trustedStart(FrameSetElement.TAG, frameSetBuilder);
  }

  public HtmlHeadingBuilder startH1() {
    return startHeading(1);
  }

  public HtmlHeadingBuilder startH2() {
    return startHeading(2);
  }

  public HtmlHeadingBuilder startH3() {
    return startHeading(3);
  }

  public HtmlHeadingBuilder startH4() {
    return startHeading(4);
  }

  public HtmlHeadingBuilder startH5() {
    return startHeading(5);
  }

  public HtmlHeadingBuilder startH6() {
    return startHeading(6);
  }

  public HtmlHeadBuilder startHead() {
    if (headBuilder == null) {
      headBuilder = new HtmlHeadBuilder(this);
    }
    return trustedStart(HeadElement.TAG, headBuilder);
  }

  public InputBuilder startHiddenInput() {
    return startInput("hidden");
  }

  public HtmlHRBuilder startHR() {
    if (hrBuilder == null) {
      hrBuilder = new HtmlHRBuilder(this);
    }
    return trustedStart(HRElement.TAG, hrBuilder);
  }

  public HtmlIFrameBuilder startIFrame() {
    if (iFrameBuilder == null) {
      iFrameBuilder = new HtmlIFrameBuilder(this);
    }
    return trustedStart(IFrameElement.TAG, iFrameBuilder);
  }

  public HtmlImageBuilder startImage() {
    if (imageBuilder == null) {
      imageBuilder = new HtmlImageBuilder(this);
    }
    return trustedStart(ImageElement.TAG, imageBuilder);
  }

  public InputBuilder startImageInput() {
    return startInput("image");
  }

  public HtmlLabelBuilder startLabel() {
    if (labelBuilder == null) {
      labelBuilder = new HtmlLabelBuilder(this);
    }
    return trustedStart(LabelElement.TAG, labelBuilder);
  }

  public HtmlLegendBuilder startLegend() {
    if (legendBuilder == null) {
      legendBuilder = new HtmlLegendBuilder(this);
    }
    return trustedStart(LegendElement.TAG, legendBuilder);
  }

  public HtmlLIBuilder startLI() {
    return trustedStart(LIElement.TAG, liBuilder);
  }

  public HtmlLinkBuilder startLink() {
    if (linkBuilder == null) {
      linkBuilder = new HtmlLinkBuilder(this);
    }
    return trustedStart(LinkElement.TAG, linkBuilder);
  }

  public HtmlMapBuilder startMap() {
    if (mapBuilder == null) {
      mapBuilder = new HtmlMapBuilder(this);
    }
    return trustedStart(MapElement.TAG, mapBuilder);
  }

  public HtmlMetaBuilder startMeta() {
    if (metaBuilder == null) {
      metaBuilder = new HtmlMetaBuilder(this);
    }
    return trustedStart(MetaElement.TAG, metaBuilder);
  }

  public HtmlOListBuilder startOList() {
    if (oListBuilder == null) {
      oListBuilder = new HtmlOListBuilder(this);
    }
    return trustedStart(OListElement.TAG, oListBuilder);
  }

  public HtmlOptGroupBuilder startOptGroup() {
    if (optGroupBuilder == null) {
      optGroupBuilder = new HtmlOptGroupBuilder(this);
    }
    return trustedStart(OptGroupElement.TAG, optGroupBuilder);
  }

  public HtmlOptionBuilder startOption() {
    return trustedStart(OptionElement.TAG, optionBuilder);
  }

  public HtmlParagraphBuilder startParagraph() {
    if (paragraphBuilder == null) {
      paragraphBuilder = new HtmlParagraphBuilder(this);
    }
    return trustedStart(ParagraphElement.TAG, paragraphBuilder);
  }

  public HtmlParamBuilder startParam() {
    if (paramBuilder == null) {
      paramBuilder = new HtmlParamBuilder(this);
    }
    return trustedStart(ParamElement.TAG, paramBuilder);
  }

  public InputBuilder startPasswordInput() {
    return startInput("password");
  }

  public HtmlPreBuilder startPre() {
    if (preBuilder == null) {
      preBuilder = new HtmlPreBuilder(this);
    }
    return trustedStart(PreElement.TAG, preBuilder);
  }

  public HtmlButtonBuilder startPushButton() {
    return startButton("button");
  }

  public HtmlQuoteBuilder startQuote() {
    return startQuote(QuoteElement.TAG_Q);
  }

  public InputBuilder startRadioInput(String name) {
    InputBuilder builder = startInput("radio");
    attribute("name", name);
    return builder;
  }

  public HtmlButtonBuilder startResetButton() {
    return startButton("reset");
  }

  public InputBuilder startResetInput() {
    return startInput("reset");
  }

  public HtmlScriptBuilder startScript() {
    if (scriptBuilder == null) {
      scriptBuilder = new HtmlScriptBuilder(this);
    }
    return trustedStart(ScriptElement.TAG, scriptBuilder);
  }

  public HtmlSelectBuilder startSelect() {
    if (selectBuilder == null) {
      selectBuilder = new HtmlSelectBuilder(this);
    }
    return trustedStart(SelectElement.TAG, selectBuilder);
  }

  public HtmlSourceBuilder startSource() {
    if (sourceBuilder == null) {
      sourceBuilder = new HtmlSourceBuilder(this);
    }
    return trustedStart(SourceElement.TAG, sourceBuilder);
  }

  public HtmlSpanBuilder startSpan() {
    return trustedStart(SpanElement.TAG, spanBuilder);
  }

  public HtmlStyleBuilder startStyle() {
    if (styleBuilder == null) {
      styleBuilder = new HtmlStyleBuilder(this);
    }
    return trustedStart(StyleElement.TAG, styleBuilder);
  }

  public HtmlButtonBuilder startSubmitButton() {
    return startButton("submit");
  }

  public InputBuilder startSubmitInput() {
    return startInput("submit");
  }

  public HtmlTableBuilder startTable() {
    if (tableBuilder == null) {
      tableBuilder = new HtmlTableBuilder(this);
    }
    return trustedStart(TableElement.TAG, tableBuilder);
  }

  public HtmlTableCaptionBuilder startTableCaption() {
    if (tableCaptionBuilder == null) {
      tableCaptionBuilder = new HtmlTableCaptionBuilder(this);
    }
    return trustedStart(TableCaptionElement.TAG, tableCaptionBuilder);
  }

  public HtmlTableSectionBuilder startTBody() {
    return startTableSection(TableSectionElement.TAG_TBODY);
  }

  public HtmlTableCellBuilder startTD() {
    return trustedStart(TableCellElement.TAG_TD, tableCellBuilder);
  }

  public HtmlTextAreaBuilder startTextArea() {
    if (textAreaBuilder == null) {
      textAreaBuilder = new HtmlTextAreaBuilder(this);
    }
    return trustedStart(TextAreaElement.TAG, textAreaBuilder);
  }

  public InputBuilder startTextInput() {
    return startInput("text");
  }

  public HtmlTableSectionBuilder startTFoot() {
    return startTableSection(TableSectionElement.TAG_TFOOT);
  }

  public HtmlTableCellBuilder startTH() {
    return trustedStart(TableCellElement.TAG_TH, tableCellBuilder);
  }

  public HtmlTableSectionBuilder startTHead() {
    return startTableSection(TableSectionElement.TAG_THEAD);
  }

  public HtmlTitleBuilder startTitle() {
    if (titleBuilder == null) {
      titleBuilder = new HtmlTitleBuilder(this);
    }
    return trustedStart(TitleElement.TAG, titleBuilder);
  }

  public HtmlTableRowBuilder startTR() {
    return trustedStart(TableRowElement.TAG, tableRowBuilder);
  }

  public HtmlUListBuilder startUList() {
    if (uListBuilder == null) {
      uListBuilder = new HtmlUListBuilder(this);
    }
    return trustedStart(UListElement.TAG, uListBuilder);
  }

  public HtmlVideoBuilder startVideo() {
    if (videoBuilder == null) {
      videoBuilder = new HtmlVideoBuilder(this);
    }
    return trustedStart(VideoElement.TAG, videoBuilder);
  }

  @Override
  public StylesBuilder style() {
    return stylesBuilder;
  }

  public StylesBuilder styleProperty(SafeStyles style) {
    assertCanAddStylePropertyImpl();
    sb.append(style.asString());
    return style();
  }

  public HtmlElementBuilder trustedStart(String tagName) {
    return trustedStart(tagName, elementBuilder);
  }

  @Override
  protected void doCloseStartTagImpl() {
    sb.append(">");
  }

  @Override
  protected void doCloseStyleAttributeImpl() {
    sb.append("\"");
  }

  @Override
  protected void doEndStartTagImpl() {
    sb.append(" />");
  }

  @Override
  protected void doEndTagImpl(String tagName) {
    /*
     * Add an end tag.
     * 
     * Some browsers do not behave correctly if you self close (ex <select />)
     * certain tags, so we always add the end tag unless the element
     * specifically forbids an end tag (see doEndStartTagImpl()).
     * 
     * The tag name is safe because it comes from the stack, and tag names are
     * checked before they are added to the stack.
     */
    sb.append("</").append(tagName).append(">");
  }

  @Override
  protected Element doFinishImpl() {
    Element tmp = Document.get().createDivElement();
    tmp.setInnerHTML(asSafeHtml().asString());
    return tmp.getFirstChildElement();
  }

  @Override
  protected void doHtmlImpl(SafeHtml html) {
    sb.append(html.asString());
  }

  @Override
  protected void doOpenStyleImpl() {
    sb.append(" style=\"");
  }

  @Override
  protected void doTextImpl(String text) {
    sb.append(escape(text));
  }

  /**
   * Escape a string.
   * 
   * @param s the string to escape
   */
  private String escape(String s) {
    return SafeHtmlUtils.htmlEscape(s);
  }

  /**
   * Start a button with the specified type.
   */
  private HtmlButtonBuilder startButton(String type) {
    if (buttonBuilder == null) {
      buttonBuilder = new HtmlButtonBuilder(this);
    }
    HtmlButtonBuilder builder = trustedStart("button", buttonBuilder);
    builder.attribute("type", type);
    return builder;
  }

  /**
   * Start one of the many heading elements.
   */
  private HtmlHeadingBuilder startHeading(int level) {
    if (headingBuilder == null) {
      headingBuilder = new HtmlHeadingBuilder(this);
    }
    return trustedStart("h" + level, headingBuilder);
  }

  /**
   * Start an input with the specified type.
   */
  private HtmlInputBuilder startInput(String type) {
    trustedStart("input", inputBuilder);
    attribute("type", type);
    return inputBuilder;
  }

  /**
   * Start a quote or blockquote.
   */
  private HtmlQuoteBuilder startQuote(String tagName) {
    if (quoteBuilder == null) {
      quoteBuilder = new HtmlQuoteBuilder(this);
    }
    return trustedStart(tagName, quoteBuilder);
  }

  /**
   * Start a table col or colgroup.
   */
  private HtmlTableColBuilder startTableCol(String tagName) {
    if (tableColBuilder == null) {
      tableColBuilder = new HtmlTableColBuilder(this);
    }
    return trustedStart(tagName, tableColBuilder);
  }

  /**
   * Start a table section of the specified tag name.
   */
  private HtmlTableSectionBuilder startTableSection(String tagName) {
    if (tableSectionBuilder == null) {
      tableSectionBuilder = new HtmlTableSectionBuilder(this);
    }
    return trustedStart(tagName, tableSectionBuilder);
  }

  /**
   * Start a tag using the specified builder. The tagName is not checked or
   * escaped.
   * 
   * @return the builder
   */
  private <B extends ElementBuilderBase<?>> B trustedStart(String tagName, B builder) {
    onStart(tagName, builder);
    sb.append("<").append(tagName);
    return builder;
  }
}
