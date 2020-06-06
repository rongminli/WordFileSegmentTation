//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.poi.xwpf.converter.core.styles;

import org.apache.poi.xwpf.converter.core.utils.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocDefaults;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblStylePr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblStyleOverrideType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblStyleOverrideType.Enum;

public abstract class AbstractValueProvider<Value, XWPFElement> implements IValueProvider<Value, XWPFElement> {
    public AbstractValueProvider() {
    }

    public Value getValue(XWPFElement element, XWPFStylesDocument stylesDocument) {
        Value value = this.internalGetValue(element, stylesDocument);
        return value;
    }

    public Value internalGetValue(XWPFElement element, XWPFStylesDocument stylesDocument) {
        Value value = this.getValueFromElement(element, stylesDocument);
        if (value != null) {
            return value;
        } else {
            return stylesDocument == null ? null : this.getValueFromStyles(element, stylesDocument);
        }
    }

    public Value getValueFromStyles(XWPFElement element, XWPFStylesDocument stylesDocument) {
        String key = this.getKey(element, stylesDocument, (String)null, (Enum)null);
        Object defaultValue = stylesDocument.getValue(key);
        if (defaultValue == null) {
            defaultValue = this.getDefaultValue(element, stylesDocument);
            if (defaultValue == null) {
                defaultValue = XWPFStylesDocument.EMPTY_VALUE;
            }

            this.updateValueCache(stylesDocument, key, defaultValue);
        }

        Object result = this.getValueFromStyleIds(element, stylesDocument, defaultValue);
        if (result != null) {
            return this.getValueOrNull(result);
        } else {
            XWPFTableCell cell = this.getParentTableCell(element);
            if (cell != null) {
                XWPFTable table = cell.getTableRow().getTable();
                String tableStyleID = table.getStyleID();
                if (StringUtils.isNotEmpty(tableStyleID)) {
                    TableCellInfo cellInfo = stylesDocument.getTableCellInfo(cell);
                    result = this.getValueFromTableStyleId(element, stylesDocument, tableStyleID, cellInfo);
                    if (result != null) {
                        return this.getValueOrNull(result);
                    }

                    result = this.getValueFromStyleId(element, stylesDocument, tableStyleID, defaultValue);
                    if (result != null) {
                        return this.getValueOrNull(result);
                    }
                }
            }

            this.updateValueCache(stylesDocument, key, defaultValue);
            return this.getValueOrNull(defaultValue);
        }
    }

    private Object getValueFromTableStyleId(XWPFElement element, XWPFStylesDocument stylesDocument, String tableStyleID, TableCellInfo cellInfo) {
        if (StringUtils.isEmpty(tableStyleID)) {
            return null;
        } else {
            Object value = this.getValueFromTableStyleIdRow(element, stylesDocument, tableStyleID, cellInfo);
            if (value != null) {
                return value;
            } else {
                Object result;
                if (cellInfo.canApplyFirstCol()) {
                    result = this.getValueFromTableStyleIdFirstCol(element, stylesDocument, tableStyleID);
                    if (result != null) {
                        return result;
                    }
                } else if (cellInfo.canApplyLastCol()) {
                    result = this.getValueFromTableStyleIdLastCol(element, stylesDocument, tableStyleID);
                    if (result != null) {
                        return result;
                    }
                }

                return null;
            }
        }
    }

    private Object getValueFromTableStyleIdRow(XWPFElement element, XWPFStylesDocument stylesDocument, String tableStyleID, TableCellInfo cellInfo) {
        Object result;
        if (cellInfo.canApplyFirstRow()) {
            result = this.getValueFromTableStyleIdFirstRow(element, stylesDocument, tableStyleID);
            if (result != null) {
                return result;
            }
        } else if (cellInfo.canApplyLastRow()) {
            result = this.getValueFromTableStyleIdLastRow(element, stylesDocument, tableStyleID);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private Object getValueFromTableStyleIdFirstRow(XWPFElement element, XWPFStylesDocument stylesDocument, String tableStyleID) {
        return this.getValueFromTableStyleId(element, stylesDocument, tableStyleID, STTblStyleOverrideType.FIRST_ROW);
    }

    private Object getValueFromTableStyleIdLastRow(XWPFElement element, XWPFStylesDocument stylesDocument, String tableStyleID) {
        return this.getValueFromTableStyleId(element, stylesDocument, tableStyleID, STTblStyleOverrideType.LAST_ROW);
    }

    private Object getValueFromTableStyleIdFirstCol(XWPFElement element, XWPFStylesDocument stylesDocument, String tableStyleID) {
        return this.getValueFromTableStyleId(element, stylesDocument, tableStyleID, STTblStyleOverrideType.FIRST_COL);
    }

    private Object getValueFromTableStyleIdLastCol(XWPFElement element, XWPFStylesDocument stylesDocument, String tableStyleID) {
        return this.getValueFromTableStyleId(element, stylesDocument, tableStyleID, STTblStyleOverrideType.LAST_COL);
    }

    private Object getValueFromTableStyleId(XWPFElement element, XWPFStylesDocument stylesDocument, String tableStyleID, Enum type) {
        Object defaultValue = XWPFStylesDocument.EMPTY_VALUE;
        if (StringUtils.isEmpty(tableStyleID)) {
            return null;
        } else {
            String key = this.getKey(element, stylesDocument, tableStyleID, type);
            Object result = stylesDocument.getValue(key);
            if (result != null) {
                return this.getValueOrNull(result);
            } else {
                CTStyle style = stylesDocument.getStyle(tableStyleID);
                if (style == null) {
                    stylesDocument.setValue(key, defaultValue);
                    return null;
                } else {
                    Object value = null;
                    CTTblStylePr tblStylePr = stylesDocument.getTableStyle(tableStyleID, type);
                    if (tblStylePr != null) {
                        value = this.getValueFromTableStyle(tblStylePr, stylesDocument);
                        if (value != null) {
                            stylesDocument.setValue(key, value);
                            return value;
                        }

                        value = this.getValueFromTableStyleId(element, stylesDocument, this.getBasisStyleID(style), type);
                    }

                    value = value != null ? value : defaultValue;
                    this.updateValueCache(stylesDocument, key, value);
                    return this.getValueOrNull(value);
                }
            }
        }
    }

    private Value getValueOrNull(Object result) {
        return result.equals(XWPFStylesDocument.EMPTY_VALUE) ? null : (Value) result;
    }

    public Object getValueFromStyleIds(XWPFElement element, XWPFStylesDocument stylesDocument, Object defaultValue) {
        String[] styleIds = this.getStyleID(element);
        if (styleIds != null) {
            String styleId = null;

            for(int i = 0; i < styleIds.length; ++i) {
                styleId = styleIds[i];
                Object value = this.getValueFromStyleId(element, stylesDocument, styleId, defaultValue);
                if (value != null) {
                    return this.getValueOrNull(value);
                }
            }
        }

        return null;
    }

    public abstract Value getValueFromElement(XWPFElement var1, XWPFStylesDocument var2);

    protected Value getDefaultValue(XWPFElement element, XWPFStylesDocument stylesDocument) {
        Value value = this.getValueFromDefaultStyle(element, stylesDocument);
        if (value != null) {
            return value;
        } else {
            value = this.getValueFromDocDefaultsStyle(element, stylesDocument);
            return value != null ? value : this.getStaticValue(element, stylesDocument);
        }
    }

    protected Value getStaticValue(XWPFElement element, XWPFStylesDocument stylesDocument) {
        return null;
    }

    private Object getValueFromStyleId(XWPFElement element, XWPFStylesDocument stylesDocument, String styleId, Object defaultValue) {
        if (StringUtils.isEmpty(styleId)) {
            return null;
        } else {
            String key = this.getKey(element, stylesDocument, styleId, (Enum)null);
            Object result = stylesDocument.getValue(key);
            if (result != null) {
                return result;
            } else {
                CTStyle style = stylesDocument.getStyle(styleId);
                if (style == null) {
                    stylesDocument.setValue(key, defaultValue);
                    return null;
                } else {
                    Object value = this.getValueFromStyle(style, stylesDocument);
                    if (value != null) {
                        stylesDocument.setValue(key, value);
                        return value;
                    } else {
                        value = this.getValueFromStyleId(element, stylesDocument, this.getBasisStyleID(style), defaultValue);
                        value = value != null ? value : defaultValue;
                        this.updateValueCache(stylesDocument, key, value);
                        return value;
                    }
                }
            }
        }
    }

    private void updateValueCache(XWPFStylesDocument stylesDocument, String key, Object value) {
        if (value != null) {
            stylesDocument.setValue(key, value);
        } else {
            stylesDocument.setValue(key, XWPFStylesDocument.EMPTY_VALUE);
        }

    }

    protected String getKey(XWPFElement element, XWPFStylesDocument stylesDocument, String styleId, Enum type) {
        return this.getKeyBuffer(element, stylesDocument, styleId, type).toString();
    }

    protected StringBuilder getKeyBuffer(XWPFElement element, XWPFStylesDocument stylesDocument, String styleId, Enum type) {
        StringBuilder key = new StringBuilder(this.getClass().getName());
        if (StringUtils.isNotEmpty(styleId)) {
            key.append("_").append(styleId).toString();
        }

        if (type != null) {
            key.append("_table");
            key.append(type.intValue());
        }

        return key;
    }

    private String getBasisStyleID(CTStyle style) {
        return style.getBasedOn() != null ? style.getBasedOn().getVal() : null;
    }

    protected abstract String[] getStyleID(XWPFElement var1);

    protected abstract Value getValueFromStyle(CTStyle var1, XWPFStylesDocument var2);

    protected abstract Value getValueFromTableStyle(CTTblStylePr var1, XWPFStylesDocument var2);

    protected Value getValueFromDefaultStyle(XWPFElement element, XWPFStylesDocument stylesDocument) {
        Value value = null;
        CTStyle style = this.getDefaultStyle(element, stylesDocument);
        if (style != null) {
            value = this.getValueFromStyle(style, stylesDocument);
        }

        return value;
    }

    protected Value getValueFromDocDefaultsStyle(XWPFElement element, XWPFStylesDocument stylesDocument) {
        CTDocDefaults docDefaults = stylesDocument.getDocDefaults();
        if (docDefaults == null) {
            return null;
        } else {
            Value value = this.getValueFromDocDefaultsStyle(docDefaults, stylesDocument);
            return value != null ? value : null;
        }
    }

    protected abstract Value getValueFromDocDefaultsStyle(CTDocDefaults var1, XWPFStylesDocument var2);

    protected abstract CTStyle getDefaultStyle(XWPFElement var1, XWPFStylesDocument var2);

    protected abstract XWPFTableCell getParentTableCell(XWPFElement var1);
}
