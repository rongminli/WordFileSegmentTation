//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.poi.xwpf.converter.xhtml.internal.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class StringEscapeUtils {
    public StringEscapeUtils() {
    }

    public static String escapeHtml(String str) {
        if (str == null) {
            return null;
        } else {
            try {
                StringWriter writer = new StringWriter((int)((double)str.length() * 1.5D));
                escapeHtml(writer, str);
                return writer.toString();
            } catch (IOException var2) {
                var2.printStackTrace();
                return null;
            }
        }
    }

    public static void escapeHtml(Writer writer, String string) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("The Writer must not be null.");
        } else if (string != null) {
            StringEscapeUtils.Entities.HTML40.escape(writer, string);
        }
    }

    static class IntHashMap {
        private transient StringEscapeUtils.IntHashMap.Entry[] table;
        private transient int count;
        private int threshold;
        private float loadFactor;

        public IntHashMap() {
            this(20, 0.75F);
        }

        public IntHashMap(int initialCapacity) {
            this(initialCapacity, 0.75F);
        }

        public IntHashMap(int initialCapacity, float loadFactor) {
            if (initialCapacity < 0) {
                throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
            } else if (loadFactor <= 0.0F) {
                throw new IllegalArgumentException("Illegal Load: " + loadFactor);
            } else {
                if (initialCapacity == 0) {
                    initialCapacity = 1;
                }

                this.loadFactor = loadFactor;
                this.table = new StringEscapeUtils.IntHashMap.Entry[initialCapacity];
                this.threshold = (int)((float)initialCapacity * loadFactor);
            }
        }

        public int size() {
            return this.count;
        }

        public boolean isEmpty() {
            return this.count == 0;
        }

        public boolean contains(Object value) {
            if (value == null) {
                throw new NullPointerException();
            } else {
                StringEscapeUtils.IntHashMap.Entry[] tab = this.table;
                int i = tab.length;

                while(i-- > 0) {
                    for(StringEscapeUtils.IntHashMap.Entry e = tab[i]; e != null; e = e.next) {
                        if (e.value.equals(value)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        public boolean containsValue(Object value) {
            return this.contains(value);
        }

        public boolean containsKey(int key) {
            StringEscapeUtils.IntHashMap.Entry[] tab = this.table;
            int hash = key;
            int index = (key & 2147483647) % tab.length;

            for(StringEscapeUtils.IntHashMap.Entry e = tab[index]; e != null; e = e.next) {
                if (e.hash == hash) {
                    return true;
                }
            }

            return false;
        }

        public Object get(int key) {
            StringEscapeUtils.IntHashMap.Entry[] tab = this.table;
            int hash = key;
            int index = (key & 2147483647) % tab.length;

            for(StringEscapeUtils.IntHashMap.Entry e = tab[index]; e != null; e = e.next) {
                if (e.hash == hash) {
                    return e.value;
                }
            }

            return null;
        }

        protected void rehash() {
            int oldCapacity = this.table.length;
            StringEscapeUtils.IntHashMap.Entry[] oldMap = this.table;
            int newCapacity = oldCapacity * 2 + 1;
            StringEscapeUtils.IntHashMap.Entry[] newMap = new StringEscapeUtils.IntHashMap.Entry[newCapacity];
            this.threshold = (int)((float)newCapacity * this.loadFactor);
            this.table = newMap;
            int i = oldCapacity;

            StringEscapeUtils.IntHashMap.Entry e;
            int index;
            while(i-- > 0) {
                for(StringEscapeUtils.IntHashMap.Entry old = oldMap[i]; old != null; newMap[index] = e) {
                    e = old;
                    old = old.next;
                    index = (e.hash & 2147483647) % newCapacity;
                    e.next = newMap[index];
                }
            }

        }

        public Object put(int key, Object value) {
            StringEscapeUtils.IntHashMap.Entry[] tab = this.table;
            int hash = key;
            int index = (key & 2147483647) % tab.length;

            StringEscapeUtils.IntHashMap.Entry e;
            for(e = tab[index]; e != null; e = e.next) {
                if (e.hash == hash) {
                    Object old = e.value;
                    e.value = value;
                    return old;
                }
            }

            if (this.count >= this.threshold) {
                this.rehash();
                tab = this.table;
                index = (hash & 2147483647) % tab.length;
            }

            e = new StringEscapeUtils.IntHashMap.Entry(hash, key, value, tab[index]);
            tab[index] = e;
            ++this.count;
            return null;
        }

        public Object remove(int key) {
            StringEscapeUtils.IntHashMap.Entry[] tab = this.table;
            int hash = key;
            int index = (key & 2147483647) % tab.length;
            StringEscapeUtils.IntHashMap.Entry e = tab[index];

            for(StringEscapeUtils.IntHashMap.Entry prev = null; e != null; e = e.next) {
                if (e.hash == hash) {
                    if (prev != null) {
                        prev.next = e.next;
                    } else {
                        tab[index] = e.next;
                    }

                    --this.count;
                    Object oldValue = e.value;
                    e.value = null;
                    return oldValue;
                }

                prev = e;
            }

            return null;
        }

        public synchronized void clear() {
            StringEscapeUtils.IntHashMap.Entry[] tab = this.table;
            int index = tab.length;

            while(true) {
                --index;
                if (index < 0) {
                    this.count = 0;
                    return;
                }

                tab[index] = null;
            }
        }

        private static class Entry {
            int hash;
            Object value;
            StringEscapeUtils.IntHashMap.Entry next;

            protected Entry(int hash, int key, Object value, StringEscapeUtils.IntHashMap.Entry next) {
                this.hash = hash;
                this.value = value;
                this.next = next;
            }
        }
    }

    public static class Entities {
        private static final String[][] BASIC_ARRAY = new String[][]{{"quot", "34"}, {"amp", "38"}, {"lt", "60"}, {"gt", "62"}};
        private static final String[][] APOS_ARRAY = new String[][]{{"apos", "39"}};
        static final String[][] ISO8859_1_ARRAY = new String[][]{{"nbsp", "160"}, {"iexcl", "161"}, {"cent", "162"}, {"pound", "163"}, {"curren", "164"}, {"yen", "165"}, {"brvbar", "166"}, {"sect", "167"}, {"uml", "168"}, {"copy", "169"}, {"ordf", "170"}, {"laquo", "171"}, {"not", "172"}, {"shy", "173"}, {"reg", "174"}, {"macr", "175"}, {"deg", "176"}, {"plusmn", "177"}, {"sup2", "178"}, {"sup3", "179"}, {"acute", "180"}, {"micro", "181"}, {"para", "182"}, {"middot", "183"}, {"cedil", "184"}, {"sup1", "185"}, {"ordm", "186"}, {"raquo", "187"}, {"frac14", "188"}, {"frac12", "189"}, {"frac34", "190"}, {"iquest", "191"}, {"Agrave", "192"}, {"Aacute", "193"}, {"Acirc", "194"}, {"Atilde", "195"}, {"Auml", "196"}, {"Aring", "197"}, {"AElig", "198"}, {"Ccedil", "199"}, {"Egrave", "200"}, {"Eacute", "201"}, {"Ecirc", "202"}, {"Euml", "203"}, {"Igrave", "204"}, {"Iacute", "205"}, {"Icirc", "206"}, {"Iuml", "207"}, {"ETH", "208"}, {"Ntilde", "209"}, {"Ograve", "210"}, {"Oacute", "211"}, {"Ocirc", "212"}, {"Otilde", "213"}, {"Ouml", "214"}, {"times", "215"}, {"Oslash", "216"}, {"Ugrave", "217"}, {"Uacute", "218"}, {"Ucirc", "219"}, {"Uuml", "220"}, {"Yacute", "221"}, {"THORN", "222"}, {"szlig", "223"}, {"agrave", "224"}, {"aacute", "225"}, {"acirc", "226"}, {"atilde", "227"}, {"auml", "228"}, {"aring", "229"}, {"aelig", "230"}, {"ccedil", "231"}, {"egrave", "232"}, {"eacute", "233"}, {"ecirc", "234"}, {"euml", "235"}, {"igrave", "236"}, {"iacute", "237"}, {"icirc", "238"}, {"iuml", "239"}, {"eth", "240"}, {"ntilde", "241"}, {"ograve", "242"}, {"oacute", "243"}, {"ocirc", "244"}, {"otilde", "245"}, {"ouml", "246"}, {"divide", "247"}, {"oslash", "248"}, {"ugrave", "249"}, {"uacute", "250"}, {"ucirc", "251"}, {"uuml", "252"}, {"yacute", "253"}, {"thorn", "254"}, {"yuml", "255"}};
        static final String[][] HTML40_ARRAY = new String[][]{{"fnof", "402"}, {"Alpha", "913"}, {"Beta", "914"}, {"Gamma", "915"}, {"Delta", "916"}, {"Epsilon", "917"}, {"Zeta", "918"}, {"Eta", "919"}, {"Theta", "920"}, {"Iota", "921"}, {"Kappa", "922"}, {"Lambda", "923"}, {"Mu", "924"}, {"Nu", "925"}, {"Xi", "926"}, {"Omicron", "927"}, {"Pi", "928"}, {"Rho", "929"}, {"Sigma", "931"}, {"Tau", "932"}, {"Upsilon", "933"}, {"Phi", "934"}, {"Chi", "935"}, {"Psi", "936"}, {"Omega", "937"}, {"alpha", "945"}, {"beta", "946"}, {"gamma", "947"}, {"delta", "948"}, {"epsilon", "949"}, {"zeta", "950"}, {"eta", "951"}, {"theta", "952"}, {"iota", "953"}, {"kappa", "954"}, {"lambda", "955"}, {"mu", "956"}, {"nu", "957"}, {"xi", "958"}, {"omicron", "959"}, {"pi", "960"}, {"rho", "961"}, {"sigmaf", "962"}, {"sigma", "963"}, {"tau", "964"}, {"upsilon", "965"}, {"phi", "966"}, {"chi", "967"}, {"psi", "968"}, {"omega", "969"}, {"thetasym", "977"}, {"upsih", "978"}, {"piv", "982"}, {"bull", "8226"}, {"hellip", "8230"}, {"prime", "8242"}, {"Prime", "8243"}, {"oline", "8254"}, {"frasl", "8260"}, {"weierp", "8472"}, {"image", "8465"}, {"real", "8476"}, {"trade", "8482"}, {"alefsym", "8501"}, {"larr", "8592"}, {"uarr", "8593"}, {"rarr", "8594"}, {"darr", "8595"}, {"harr", "8596"}, {"crarr", "8629"}, {"lArr", "8656"}, {"uArr", "8657"}, {"rArr", "8658"}, {"dArr", "8659"}, {"hArr", "8660"}, {"forall", "8704"}, {"part", "8706"}, {"exist", "8707"}, {"empty", "8709"}, {"nabla", "8711"}, {"isin", "8712"}, {"notin", "8713"}, {"ni", "8715"}, {"prod", "8719"}, {"sum", "8721"}, {"minus", "8722"}, {"lowast", "8727"}, {"radic", "8730"}, {"prop", "8733"}, {"infin", "8734"}, {"ang", "8736"}, {"and", "8743"}, {"or", "8744"}, {"cap", "8745"}, {"cup", "8746"}, {"int", "8747"}, {"there4", "8756"}, {"sim", "8764"}, {"cong", "8773"}, {"asymp", "8776"}, {"ne", "8800"}, {"equiv", "8801"}, {"le", "8804"}, {"ge", "8805"}, {"sub", "8834"}, {"sup", "8835"}, {"sube", "8838"}, {"supe", "8839"}, {"oplus", "8853"}, {"otimes", "8855"}, {"perp", "8869"}, {"sdot", "8901"}, {"lceil", "8968"}, {"rceil", "8969"}, {"lfloor", "8970"}, {"rfloor", "8971"}, {"lang", "9001"}, {"rang", "9002"}, {"loz", "9674"}, {"spades", "9824"}, {"clubs", "9827"}, {"hearts", "9829"}, {"diams", "9830"}, {"OElig", "338"}, {"oelig", "339"}, {"Scaron", "352"}, {"scaron", "353"}, {"Yuml", "376"}, {"circ", "710"}, {"tilde", "732"}, {"ensp", "8194"}, {"emsp", "8195"}, {"thinsp", "8201"}, {"zwnj", "8204"}, {"zwj", "8205"}, {"lrm", "8206"}, {"rlm", "8207"}, {"ndash", "8211"}, {"mdash", "8212"}, {"lsquo", "8216"}, {"rsquo", "8217"}, {"sbquo", "8218"}, {"ldquo", "8220"}, {"rdquo", "8221"}, {"bdquo", "8222"}, {"dagger", "8224"}, {"Dagger", "8225"}, {"permil", "8240"}, {"lsaquo", "8249"}, {"rsaquo", "8250"}, {"euro", "8364"}};
        public static final StringEscapeUtils.Entities XML = new StringEscapeUtils.Entities();
        public static final StringEscapeUtils.Entities HTML32;
        public static final StringEscapeUtils.Entities HTML40;
        StringEscapeUtils.Entities.EntityMap map = new StringEscapeUtils.Entities.LookupEntityMap();

        public Entities() {
        }

        static void fillWithHtml40Entities(StringEscapeUtils.Entities entities) {
            entities.addEntities(BASIC_ARRAY);
            entities.addEntities(ISO8859_1_ARRAY);
            entities.addEntities(HTML40_ARRAY);
        }

        public void addEntities(String[][] entityArray) {
            for(int i = 0; i < entityArray.length; ++i) {
                this.addEntity(entityArray[i][0], Integer.parseInt(entityArray[i][1]));
            }

        }

        public void addEntity(String name, int value) {
            this.map.add(name, value);
        }

        public String entityName(int value) {
            return this.map.name(value);
        }

        public int entityValue(String name) {
            return this.map.value(name);
        }

        public String escape(String str) {
            StringWriter stringWriter = this.createStringWriter(str);

            try {
                this.escape(stringWriter, str);
            } catch (IOException var4) {
                throw new RuntimeException(var4);
            }

            return stringWriter.toString();
        }

        // 取消和编码转换
        public void escape(Writer writer, String str) throws IOException {
            int len = str.length();

            for(int i = 0; i < len; ++i) {
                char c = str.charAt(i);
                String entityName = this.entityName(c);
                if (entityName == null) {
                    if (c > 127) {
                        writer.write(c);
                    } else {
                        writer.write(c);
                    }
                } else {
                    writer.write(38);
                    writer.write(entityName);
                    writer.write(59);
                }
            }

        }

        public String unescape(String str) {
            int firstAmp = str.indexOf(38);
            if (firstAmp < 0) {
                return str;
            } else {
                StringWriter stringWriter = this.createStringWriter(str);

                try {
                    this.doUnescape(stringWriter, str, firstAmp);
                } catch (IOException var5) {
                    throw new RuntimeException(var5);
                }

                return stringWriter.toString();
            }
        }

        private StringWriter createStringWriter(String str) {
            return new StringWriter((int)((double)str.length() + (double)str.length() * 0.1D));
        }

        public void unescape(Writer writer, String str) throws IOException {
            int firstAmp = str.indexOf(38);
            if (firstAmp < 0) {
                writer.write(str);
            } else {
                this.doUnescape(writer, str, firstAmp);
            }
        }

        private void doUnescape(Writer writer, String str, int firstAmp) throws IOException {
            writer.write(str, 0, firstAmp);
            int len = str.length();

            for(int i = firstAmp; i < len; ++i) {
                char c = str.charAt(i);
                if (c == '&') {
                    int nextIdx = i + 1;
                    int semiColonIdx = str.indexOf(59, nextIdx);
                    if (semiColonIdx == -1) {
                        writer.write(c);
                    } else {
                        int amphersandIdx = str.indexOf(38, i + 1);
                        if (amphersandIdx != -1 && amphersandIdx < semiColonIdx) {
                            writer.write(c);
                        } else {
                            String entityContent = str.substring(nextIdx, semiColonIdx);
                            int entityValue = -1;
                            int entityContentLen = entityContent.length();
                            if (entityContentLen > 0) {
                                if (entityContent.charAt(0) == '#') {
                                    if (entityContentLen > 1) {
                                        char isHexChar = entityContent.charAt(1);

                                        try {
                                            switch(isHexChar) {
                                                case 'X':
                                                case 'x':
                                                    entityValue = Integer.parseInt(entityContent.substring(2), 16);
                                                    break;
                                                default:
                                                    entityValue = Integer.parseInt(entityContent.substring(1), 10);
                                            }

                                            if (entityValue > 65535) {
                                                entityValue = -1;
                                            }
                                        } catch (NumberFormatException var15) {
                                            entityValue = -1;
                                        }
                                    }
                                } else {
                                    entityValue = this.entityValue(entityContent);
                                }
                            }

                            if (entityValue == -1) {
                                writer.write(38);
                                writer.write(entityContent);
                                writer.write(59);
                            } else {
                                writer.write(entityValue);
                            }

                            i = semiColonIdx;
                        }
                    }
                } else {
                    writer.write(c);
                }
            }

        }

        public String generateDocType(String docTypeName) {
            StringBuilder docType = new StringBuilder("<!DOCTYPE ");
            docType.append(docTypeName);
            docType.append(" [");
            Set<String> names = this.map.getNames();
            Iterator i$ = names.iterator();

            while(i$.hasNext()) {
                String entityName = (String)i$.next();
                int entityValue = this.map.value(entityName);
                docType.append("<!ENTITY ");
                docType.append(entityName);
                docType.append(" \"&#");
                docType.append(String.valueOf(entityValue));
                docType.append(";\">");
            }

            docType.append(" ]>");
            return docType.toString();
        }

        static {
            XML.addEntities(BASIC_ARRAY);
            XML.addEntities(APOS_ARRAY);
            HTML32 = new StringEscapeUtils.Entities();
            HTML32.addEntities(BASIC_ARRAY);
            HTML32.addEntities(ISO8859_1_ARRAY);
            HTML40 = new StringEscapeUtils.Entities();
            fillWithHtml40Entities(HTML40);
        }

        static class BinaryEntityMap extends StringEscapeUtils.Entities.ArrayEntityMap {
            public BinaryEntityMap() {
            }

            public BinaryEntityMap(int growBy) {
                super(growBy);
            }

            private int binarySearch(int key) {
                int low = 0;
                int high = this.size - 1;

                while(low <= high) {
                    int mid = low + high >>> 1;
                    int midVal = this.values[mid];
                    if (midVal < key) {
                        low = mid + 1;
                    } else {
                        if (midVal <= key) {
                            return mid;
                        }

                        high = mid - 1;
                    }
                }

                return -(low + 1);
            }

            @Override
            public void add(String name, int value) {
                this.ensureCapacity(this.size + 1);
                int insertAt = this.binarySearch(value);
                if (insertAt <= 0) {
                    insertAt = -(insertAt + 1);
                    System.arraycopy(this.values, insertAt, this.values, insertAt + 1, this.size - insertAt);
                    this.values[insertAt] = value;
                    System.arraycopy(this.names, insertAt, this.names, insertAt + 1, this.size - insertAt);
                    this.names[insertAt] = name;
                    ++this.size;
                }
            }

            @Override
            public String name(int value) {
                int index = this.binarySearch(value);
                return index < 0 ? null : this.names[index];
            }
        }

        static class ArrayEntityMap implements StringEscapeUtils.Entities.EntityMap {
            protected int growBy = 100;
            protected int size = 0;
            protected String[] names;
            protected int[] values;

            public ArrayEntityMap() {
                this.names = new String[this.growBy];
                this.values = new int[this.growBy];
            }

            public ArrayEntityMap(int growBy) {
                this.growBy = growBy;
                this.names = new String[growBy];
                this.values = new int[growBy];
            }

            public void add(String name, int value) {
                this.ensureCapacity(this.size + 1);
                this.names[this.size] = name;
                this.values[this.size] = value;
                ++this.size;
            }

            protected void ensureCapacity(int capacity) {
                if (capacity > this.names.length) {
                    int newSize = Math.max(capacity, this.size + this.growBy);
                    String[] newNames = new String[newSize];
                    System.arraycopy(this.names, 0, newNames, 0, this.size);
                    this.names = newNames;
                    int[] newValues = new int[newSize];
                    System.arraycopy(this.values, 0, newValues, 0, this.size);
                    this.values = newValues;
                }

            }

            public String name(int value) {
                for(int i = 0; i < this.size; ++i) {
                    if (this.values[i] == value) {
                        return this.names[i];
                    }
                }

                return null;
            }

            public int value(String name) {
                for(int i = 0; i < this.size; ++i) {
                    if (this.names[i].equals(name)) {
                        return this.values[i];
                    }
                }

                return -1;
            }

            public Set<String> getNames() {
                return new HashSet(Arrays.asList(this.names));
            }
        }

        static class LookupEntityMap extends StringEscapeUtils.Entities.PrimitiveEntityMap {
            private String[] lookupTable;
            private int LOOKUP_TABLE_SIZE = 256;

            LookupEntityMap() {
            }

            @Override
            public String name(int value) {
                return value < this.LOOKUP_TABLE_SIZE ? this.lookupTable()[value] : super.name(value);
            }

            private String[] lookupTable() {
                if (this.lookupTable == null) {
                    this.createLookupTable();
                }

                return this.lookupTable;
            }

            private void createLookupTable() {
                this.lookupTable = new String[this.LOOKUP_TABLE_SIZE];

                for(int i = 0; i < this.LOOKUP_TABLE_SIZE; ++i) {
                    this.lookupTable[i] = super.name(i);
                }

            }
        }

        static class TreeEntityMap extends StringEscapeUtils.Entities.MapIntMap {
            public TreeEntityMap() {
                this.mapNameToValue = new TreeMap();
                this.mapValueToName = new TreeMap();
            }
        }

        static class HashEntityMap extends StringEscapeUtils.Entities.MapIntMap {
            public HashEntityMap() {
                this.mapNameToValue = new HashMap();
                this.mapValueToName = new HashMap();
            }
        }

        abstract static class MapIntMap implements StringEscapeUtils.Entities.EntityMap {
            protected Map<String, Integer> mapNameToValue;
            protected Map<Integer, String> mapValueToName;

            MapIntMap() {
            }

            public void add(String name, int value) {
                this.mapNameToValue.put(name, value);
                this.mapValueToName.put(value, name);
            }

            public String name(int value) {
                return (String)this.mapValueToName.get(new Integer(value));
            }

            public int value(String name) {
                Object value = this.mapNameToValue.get(name);
                return value == null ? -1 : (Integer)value;
            }

            public Set<String> getNames() {
                return this.mapNameToValue.keySet();
            }
        }

        static class PrimitiveEntityMap implements StringEscapeUtils.Entities.EntityMap {
            protected Map<String, Integer> mapNameToValue = new HashMap();
            private StringEscapeUtils.IntHashMap mapValueToName = new StringEscapeUtils.IntHashMap();

            PrimitiveEntityMap() {
            }

            public void add(String name, int value) {
                this.mapNameToValue.put(name, new Integer(value));
                this.mapValueToName.put(value, name);
            }

            public String name(int value) {
                return (String)this.mapValueToName.get(value);
            }

            public int value(String name) {
                Object value = this.mapNameToValue.get(name);
                return value == null ? -1 : (Integer)value;
            }

            public Set<String> getNames() {
                return this.mapNameToValue.keySet();
            }
        }

        interface EntityMap {
            void add(String var1, int var2);

            String name(int var1);

            int value(String var1);

            Set<String> getNames();
        }
    }
}
