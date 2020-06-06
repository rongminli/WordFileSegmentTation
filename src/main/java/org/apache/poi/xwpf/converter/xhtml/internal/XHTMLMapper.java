//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.poi.xwpf.converter.xhtml.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import org.apache.poi.xwpf.converter.core.BorderSide;
import org.apache.poi.xwpf.converter.core.Color;
import org.apache.poi.xwpf.converter.core.IURIResolver;
import org.apache.poi.xwpf.converter.core.ListItemContext;
import org.apache.poi.xwpf.converter.core.TableCellBorder;
import org.apache.poi.xwpf.converter.core.XWPFDocumentVisitor;
import org.apache.poi.xwpf.converter.core.styles.XWPFStylesDocument;
import org.apache.poi.xwpf.converter.core.styles.run.RunFontStyleStrikeValueProvider;
import org.apache.poi.xwpf.converter.core.styles.run.RunTextHighlightingValueProvider;
import org.apache.poi.xwpf.converter.core.utils.DxaUtil;
import org.apache.poi.xwpf.converter.core.utils.StringUtils;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.converter.xhtml.internal.styles.CSSStyle;
import org.apache.poi.xwpf.converter.xhtml.internal.styles.CSSStylesDocument;
import org.apache.poi.xwpf.converter.xhtml.internal.utils.SAXHelper;
import org.apache.poi.xwpf.converter.xhtml.internal.utils.StringEscapeUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STRelFromH.Enum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtrRef;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPTab;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabs;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XHTMLMapper extends XWPFDocumentVisitor<Object, XHTMLOptions, XHTMLMasterPage> {
    static final String TAB_CHAR_SEQUENCE = "&nbsp;&nbsp;&nbsp;&nbsp;";
    private static final String WORD_MEDIA = "word/media/";
    private final ContentHandler contentHandler;
    private XWPFParagraph currentParagraph;
    private boolean generateStyles = true;
    private final IURIResolver resolver;
    private AttributesImpl currentRunAttributes;
    private boolean pageDiv;

    public XHTMLMapper(XWPFDocument document, ContentHandler contentHandler, XHTMLOptions options) throws Exception {
        super(document, options != null ? options : XHTMLOptions.getDefault());
        this.contentHandler = contentHandler;
        this.resolver = ((XHTMLOptions)this.getOptions()).getURIResolver();
        this.pageDiv = false;
    }

    @Override
    protected XWPFStylesDocument createStylesDocument(XWPFDocument document) throws XmlException, IOException {
        return new CSSStylesDocument(document, ((XHTMLOptions)this.options).isIgnoreStylesIfUnused(), ((XHTMLOptions)this.options).getIndent());
    }

    @Override
    protected Object startVisitDocument() throws Exception {
        if (!((XHTMLOptions)this.options).isFragment()) {
            this.contentHandler.startDocument();
            this.startElement("html");
            this.startElement("head");
            if (this.generateStyles) {
                ((CSSStylesDocument)this.stylesDocument).save(this.contentHandler);
            }

            this.endElement("head");
            this.startElement("body");
        }

        return null;
    }

    @Override
    protected void endVisitDocument() throws Exception {
        if (this.pageDiv) {
            this.endElement("div");
        }

        if (!((XHTMLOptions)this.options).isFragment()) {
            this.endElement("body");
            this.endElement("html");
            this.contentHandler.endDocument();
        }

    }

    @Override
    protected Object startVisitParagraph(XWPFParagraph paragraph, ListItemContext itemContext, Object parentContainer) throws Exception {
        AttributesImpl attributes = this.createClassAttribute(paragraph.getStyleID());
        CTPPr pPr = paragraph.getCTP().getPPr();
        CSSStyle cssStyle = this.getStylesDocument().createCSSStyle(pPr);
        attributes = this.createStyleAttribute(cssStyle, attributes);
        this.startElement("p", attributes);
        if (itemContext != null) {
            this.startElement("span", attributes);
            String text = itemContext.getText();
            if (StringUtils.isNotEmpty(text)) {
                text = StringUtils.replaceNonUnicodeChars(text);
                text = text + " ";
                SAXHelper.characters(this.contentHandler, StringEscapeUtils.escapeHtml(text));
            }

            this.endElement("span");
        }

        return null;
    }

    @Override
    protected void endVisitParagraph(XWPFParagraph paragraph, Object parentContainer, Object paragraphContainer) throws Exception {
        this.endElement("p");
    }

    @Override
    protected void visitRun(XWPFRun run, boolean pageNumber, String url, Object paragraphContainer) throws Exception {
        if (run.getParent() instanceof XWPFParagraph) {
            this.currentParagraph = (XWPFParagraph)run.getParent();
        }

        XWPFParagraph paragraph = run.getParagraph();
        this.currentRunAttributes = this.createClassAttribute(paragraph.getStyleID());
        CTRPr rPr = run.getCTR().getRPr();
        CSSStyle cssStyle = this.getStylesDocument().createCSSStyle(rPr);
        this.currentRunAttributes = this.createStyleAttribute(cssStyle, this.currentRunAttributes);
        if (url != null) {
            AttributesImpl hyperlinkAttributes = new AttributesImpl();
            SAXHelper.addAttrValue(hyperlinkAttributes, "href", url);
            this.startElement("a", hyperlinkAttributes);
        }

        super.visitRun(run, pageNumber, url, paragraphContainer);
        if (url != null) {
            this.characters(" ");
            this.endElement("a");
        }

        this.currentRunAttributes = null;
        this.currentParagraph = null;
    }

    @Override
    protected void visitEmptyRun(Object paragraphContainer) throws Exception {
        this.startElement("br");
        this.endElement("br");
    }

    @Override
    protected void visitText(CTText ctText, boolean pageNumber, Object paragraphContainer) throws Exception {
        if (this.currentRunAttributes != null) {
            this.startElement("span", this.currentRunAttributes);
        }

        String text = ctText.getStringValue();
        if (StringUtils.isNotEmpty(text)) {
            this.characters(StringEscapeUtils.escapeHtml(text));
        }

        if (this.currentRunAttributes != null) {
            this.endElement("span");
        }

    }

    @Override
    protected void visitStyleText(XWPFRun run, String text) throws Exception {
        if (run.getFontFamily() == null) {
            run.setFontFamily(this.getStylesDocument().getFontFamilyAscii(run));
        }

        if (run.getFontSize() <= 0) {
            if (this.getStylesDocument().getFontSize(run) == null) {
                run.setFontSize(10);  // 为幂次方标志提供默认字体大小
            }else {
                run.setFontSize(this.getStylesDocument().getFontSize(run).intValue());
            }
        }

        CTRPr rPr = run.getCTR().getRPr();
        AttributesImpl runAttributes = this.createClassAttribute(this.currentParagraph.getStyleID());
        CSSStyle cssStyle = this.getStylesDocument().createCSSStyle(rPr);
        if (cssStyle != null) {
            Color color = RunTextHighlightingValueProvider.INSTANCE.getValue(rPr, this.getStylesDocument());
            if (color != null) {
                cssStyle.addProperty("background-color", StringUtils.toHexString(color));
            }

            if (Boolean.TRUE.equals(RunFontStyleStrikeValueProvider.INSTANCE.getValue(rPr, this.getStylesDocument())) || rPr.getDstrike() != null) {
                cssStyle.addProperty("text-decoration", "line-through");
            }

            if (rPr.getVertAlign() != null) {
                int align = rPr.getVertAlign().getVal().intValue();
                if (2 == align) {
                    cssStyle.addProperty("vertical-align", "super");
                } else if (3 == align) {
                    cssStyle.addProperty("vertical-align", "sub");
                }
            }
        }

        runAttributes = this.createStyleAttribute(cssStyle, runAttributes);
        if (runAttributes != null) {
            this.startElement("span", runAttributes);
        }

        if (StringUtils.isNotEmpty(text)) {
            this.characters(StringEscapeUtils.escapeHtml(text));
        }

        if (runAttributes != null) {
            this.endElement("span");
        }

    }

    @Override
    protected void visitTab(CTPTab o, Object paragraphContainer) throws Exception {
    }

    @Override
    protected void visitTabs(CTTabs tabs, Object paragraphContainer) throws Exception {
        if (this.currentParagraph != null && tabs == null) {
            this.startElement("span", (Attributes)null);
            this.characters("&nbsp;&nbsp;&nbsp;&nbsp;");
            this.endElement("span");
        }
    }

    @Override
    protected void addNewLine(CTBr br, Object paragraphContainer) throws Exception {
        this.startElement("br");
        this.endElement("br");
    }

    @Override
    protected void pageBreak() throws Exception {
    }

    @Override
    protected void visitBookmark(CTBookmark bookmark, XWPFParagraph paragraph, Object paragraphContainer) throws Exception {
        AttributesImpl attributes = new AttributesImpl();
        SAXHelper.addAttrValue(attributes, "id", bookmark.getName());
        this.startElement("span", attributes);
        this.endElement("span");
    }

    @Override
    protected Object startVisitTable(XWPFTable table, float[] colWidths, Object tableContainer) throws Exception {
        AttributesImpl attributes = this.createClassAttribute(table.getStyleID());
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        CSSStyle cssStyle = this.getStylesDocument().createCSSStyle(tblPr);
        if (cssStyle != null) {
            cssStyle.addProperty("border-collapse", "collapse");
        }

        attributes = this.createStyleAttribute(cssStyle, attributes);
        this.startElement("table", attributes);
        return null;
    }

    @Override
    protected void endVisitTable(XWPFTable table, Object parentContainer, Object tableContainer) throws Exception {
        this.endElement("table");
    }

    @Override
    protected void startVisitTableRow(XWPFTableRow row, Object tableContainer, int rowIndex, boolean headerRow) throws Exception {
        XWPFTable table = row.getTable();
        AttributesImpl attributes = this.createClassAttribute(table.getStyleID());
        if (headerRow) {
            this.startElement("th", attributes);
        } else {
            this.startElement("tr", attributes);
        }

    }

    @Override
    protected void endVisitTableRow(XWPFTableRow row, Object tableContainer, boolean firstRow, boolean lastRow, boolean headerRow) throws Exception {
        if (headerRow) {
            this.endElement("th");
        } else {
            this.endElement("tr");
        }

    }

    @Override
    protected Object startVisitTableCell(XWPFTableCell cell, Object tableContainer, boolean firstRow, boolean lastRow, boolean firstCell, boolean lastCell, List<XWPFTableCell> vMergeCells) throws Exception {
        XWPFTableRow row = cell.getTableRow();
        XWPFTable table = row.getTable();
        AttributesImpl attributes = this.createClassAttribute(table.getStyleID());
        CTTcPr tcPr = cell.getCTTc().getTcPr();
        CSSStyle cssStyle = this.getStylesDocument().createCSSStyle(tcPr);
        if (cssStyle != null) {
            TableCellBorder border = this.getStylesDocument().getTableBorder(table, BorderSide.TOP);
            String style;
            if (border != null) {
                style = border.getBorderSize() + "px solid " + StringUtils.toHexString(border.getBorderColor());
                cssStyle.addProperty("border-top", style);
            }

            border = this.getStylesDocument().getTableBorder(table, BorderSide.BOTTOM);
            if (border != null) {
                style = border.getBorderSize() + "px solid " + StringUtils.toHexString(border.getBorderColor());
                cssStyle.addProperty("border-bottom", style);
            }

            border = this.getStylesDocument().getTableBorder(table, BorderSide.LEFT);
            if (border != null) {
                style = border.getBorderSize() + "px solid " + StringUtils.toHexString(border.getBorderColor());
                cssStyle.addProperty("border-left", style);
            }

            border = this.getStylesDocument().getTableBorder(table, BorderSide.RIGHT);
            if (border != null) {
                style = border.getBorderSize() + "px solid " + StringUtils.toHexString(border.getBorderColor());
                cssStyle.addProperty("border-right", style);
            }
        }

        attributes = this.createStyleAttribute(cssStyle, attributes);
        BigInteger gridSpan = this.stylesDocument.getTableCellGridSpan(cell);
        if (gridSpan != null) {
            attributes = SAXHelper.addAttrValue(attributes, "colspan", gridSpan.intValue());
        }

        if (vMergeCells != null) {
            attributes = SAXHelper.addAttrValue(attributes, "rowspan", vMergeCells.size());
        }

        this.startElement("td", attributes);
        return null;
    }

    @Override
    protected void endVisitTableCell(XWPFTableCell cell, Object tableContainer, Object tableCellContainer) throws Exception {
        this.endElement("td");
    }

    @Override
    protected void visitHeader(XWPFHeader header, CTHdrFtrRef headerRef, CTSectPr sectPr, XHTMLMasterPage masterPage) throws Exception {
    }

    @Override
    protected void visitFooter(XWPFFooter footer, CTHdrFtrRef footerRef, CTSectPr sectPr, XHTMLMasterPage masterPage) throws Exception {
    }

    @Override
    protected void visitPicture(CTPicture picture, Float offsetX, Enum relativeFromH, Float offsetY, org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STRelFromV.Enum relativeFromV, org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STWrapText.Enum wrapText, Object parentContainer) throws Exception {
        AttributesImpl attributes = null;
        XWPFPictureData pictureData = super.getPictureData(picture);
        String src;
        float height;
        if (pictureData != null) {
            src = pictureData.getFileName();
            if (StringUtils.isNotEmpty(src)) {
                src = this.resolver.resolve("word/media/" + src);
                attributes = SAXHelper.addAttrValue(attributes, "src", src);
            }

            CTPositiveSize2D ext = picture.getSpPr().getXfrm().getExt();
            float width = DxaUtil.emu2points(ext.getCx());
            attributes = SAXHelper.addAttrValue(attributes, "width", this.getStylesDocument().getValueAsPoint(width));
            height = DxaUtil.emu2points(ext.getCy());
            attributes = SAXHelper.addAttrValue(attributes, "height", this.getStylesDocument().getValueAsPoint(height));
        } else {
            src = picture.getBlipFill().getBlip().getLink();
            src = this.document.getPackagePart().getRelationships().getRelationshipByID(src).getTargetURI().toString();
            attributes = SAXHelper.addAttrValue((AttributesImpl)null, "src", src);
            CTPositiveSize2D ext = picture.getSpPr().getXfrm().getExt();
            height = DxaUtil.emu2points(ext.getCx());
            attributes = SAXHelper.addAttrValue(attributes, "width", this.getStylesDocument().getValueAsPoint(height));
            height = DxaUtil.emu2points(ext.getCy());
            attributes = SAXHelper.addAttrValue(attributes, "height", this.getStylesDocument().getValueAsPoint(height));
        }

        if (attributes != null) {
            this.startElement("img", attributes);
            this.endElement("img");
        }

    }

    public void setActiveMasterPage(XHTMLMasterPage masterPage) {
        if (this.pageDiv) {
            try {
                this.endElement("div");
            } catch (SAXException var13) {
                var13.printStackTrace();
            }
        }

        AttributesImpl attributes = new AttributesImpl();
        CSSStyle style = new CSSStyle("div", (String)null);
        CTSectPr sectPr = masterPage.getSectPr();
        CTPageSz pageSize = sectPr.getPgSz();
        if (pageSize != null) {
            BigInteger width = pageSize.getW();
            if (width != null) {
                style.addProperty("width", this.getStylesDocument().getValueAsPoint(DxaUtil.dxa2points(width)));
            }
        }

        CTPageMar pageMargin = sectPr.getPgMar();
        if (pageMargin != null) {
            BigInteger marginBottom = pageMargin.getBottom();
            if (marginBottom != null) {
                float marginBottomPt = DxaUtil.dxa2points(marginBottom);
                style.addProperty("margin-bottom", this.getStylesDocument().getValueAsPoint(marginBottomPt));
            }

            BigInteger marginTop = pageMargin.getTop();
            if (marginTop != null) {
                float marginTopPt = DxaUtil.dxa2points(marginTop);
                style.addProperty("margin-top", this.getStylesDocument().getValueAsPoint(marginTopPt));
            }

            BigInteger marginLeft = pageMargin.getLeft();
            if (marginLeft != null) {
                float marginLeftPt = DxaUtil.dxa2points(marginLeft);
                style.addProperty("margin-left", this.getStylesDocument().getValueAsPoint(marginLeftPt));
            }

            BigInteger marginRight = pageMargin.getRight();
            if (marginRight != null) {
                float marginRightPt = DxaUtil.dxa2points(marginRight);
                style.addProperty("margin-right", this.getStylesDocument().getValueAsPoint(marginRightPt));
            }
        }

        String s = style.getInlineStyles();
        if (StringUtils.isNotEmpty(s)) {
            SAXHelper.addAttrValue(attributes, "style", s);
        }

        try {
            this.startElement("div", attributes);
        } catch (SAXException var12) {
            var12.printStackTrace();
        }

        this.pageDiv = true;
    }

    public XHTMLMasterPage createMasterPage(CTSectPr sectPr) {
        return new XHTMLMasterPage(sectPr);
    }

    private void startElement(String name) throws SAXException {
        this.startElement(name, (Attributes)null);
    }

    private void startElement(String name, Attributes attributes) throws SAXException {
        SAXHelper.startElement(this.contentHandler, name, attributes);
    }

    private void endElement(String name) throws SAXException {
        SAXHelper.endElement(this.contentHandler, name);
    }

    private void characters(String content) throws SAXException {
        SAXHelper.characters(this.contentHandler, content);
    }

    @Override
    public CSSStylesDocument getStylesDocument() {
        return (CSSStylesDocument)super.getStylesDocument();
    }

    private AttributesImpl createClassAttribute(String styleID) {
        String classNames = this.getStylesDocument().getClassNames(styleID);
        return StringUtils.isNotEmpty(classNames) ? SAXHelper.addAttrValue((AttributesImpl)null, "class", classNames) : null;
    }

    private AttributesImpl createStyleAttribute(CSSStyle cssStyle, AttributesImpl attributes) {
        if (cssStyle != null) {
            String inlineStyles = cssStyle.getInlineStyles();
            if (StringUtils.isNotEmpty(inlineStyles)) {
                attributes = SAXHelper.addAttrValue(attributes, "style", inlineStyles);
            }
        }

        return attributes;
    }
}
