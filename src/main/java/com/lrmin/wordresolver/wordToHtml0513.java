package com.lrmin.wordresolver;


import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

/**
 *
 * @author Mafx
 *
 */
public class wordToHtml0513 {

    private final static String  tempPath = "D:\\fileSplitTest\\html";

    public static void main(String argv[]) {
        try {

                String inName = "D:\\fileSplitTest\\test\\消防学院条文解释_1.docx";
                String outName = "D:\\fileSplitTest\\消防学院条文解释_1.html";
                //doc转Html
//                doc2Html(inName, outName);
                //docx转HTml
                docx2Html(inName, outName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * doc转换为html
     *
     * @param fileName
     * @param outPutFile
     * @throws TransformerException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static void doc2Html(String fileName, String outPutFile) throws TransformerException, IOException, ParserConfigurationException {
        long startTime = System.currentTimeMillis();
        HWPFDocument  wordDocument = new HWPFDocument(new FileInputStream(fileName));
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        wordToHtmlConverter.setPicturesManager(new PicturesManager() {
            public String savePicture(byte[] content, PictureType pictureType, String suggestedName, float widthInches, float heightInches) {
                return "test/" + suggestedName;
            }
        });
        wordToHtmlConverter.processDocument(wordDocument);
        // 保存图片
        List<Picture> pics = wordDocument.getPicturesTable().getAllPictures();
        if (pics != null) {
            for (int i = 0; i < pics.size(); i++) {
                Picture pic = (Picture) pics.get(i);
                System.out.println();
                try {
                    pic.writeImageContent(new FileOutputStream(tempPath + pic.suggestFullFileName()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        Document htmlDocument = wordToHtmlConverter.getDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(out);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        out.close();
        writeFile(new String(out.toByteArray()), outPutFile);
        System.out.println("Generate " + outPutFile + " with " + (System.currentTimeMillis() - startTime) + " ms.");
    }

    /**
     * 写文件
     *
     * @param content
     * @param path
     */
    public static void writeFile(String content, String path) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
            bw.write(content);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ie) {
            }
        }
    }

    /**
     * docx格式word转换为html
     *
     * @param fileName
     *            docx文件路径
     * @param outPutFile
     *            html输出文件路径
     * @throws TransformerException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static void docx2Html(String fileName, String outPutFile) throws TransformerException, IOException, ParserConfigurationException {
        String fileOutName = outPutFile;
        long startTime = System.currentTimeMillis();
        InputStream inputStream = new FileInputStream(fileName);
        XWPFDocument document = new XWPFDocument(inputStream);
        if(document != null){
            XHTMLOptions options = XHTMLOptions.create().indent(4);
            // 导出图片
            File imageFolder = new File(tempPath);
            options.setExtractor(new FileImageExtractor(imageFolder));
            // URI resolver
            options.URIResolver(new FileURIResolver(imageFolder));
            File outFile = new File(fileOutName);
            outFile.getParentFile().mkdirs();
            OutputStream out =  new FileOutputStream(outFile);
            document.createNumbering();
            XHTMLConverter.getInstance().convert(document, out, options);
            System.out.println("Generate " + fileOutName + " with " + (System.currentTimeMillis() - startTime) + " ms.");
        }
    }
}
