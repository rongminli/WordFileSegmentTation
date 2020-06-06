package com.lrmin.wordresolver;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.UUID;

/**
 * @author lirongmin
 * @date 2020/6/1 0001
 */
public class WordParse {
    private final String TARGET_DIR = "D:\\fileSplitTest";
    private final int IMG_WIDTH = 390;
    private final String DOC_TYPE = "docx";


    public void parseDocx(File file) throws IOException, InvalidFormatException, XmlException {
        String targetFilePath = TARGET_DIR + "\\" + file.getName().split("\\.")[0];
        String tempPicturePath = targetFilePath + "\\" + UUID.randomUUID().toString();
        String mainFilePath = targetFilePath + "\\" + file.getName();

        try {

            File targetFile = new File(mainFilePath);
            targetFile.getParentFile().mkdir();
            FileOutputStream fos = new FileOutputStream(targetFile);
            FileInputStream fis = new FileInputStream(file);

            XWPFDocument xdoc = new XWPFDocument(fis);

            List<XWPFPictureData> allPictures = xdoc.getAllPictures();
            // 保存临时图片
            saveTempPic(allPictures, tempPicturePath);

            XWPFDocument newXdoc = createEmptyDoc();

            // 文档坐标参数
            int pIndex = 0;
            int tableIndex = 0;
            int fileIndex = 0;

            List<IBodyElement> bodyElements = xdoc.getBodyElements();
            for (IBodyElement element: bodyElements ) {
                // 如果是一个段落
                if(element.getElementType().equals(BodyElementType.PARAGRAPH)){
                    XWPFParagraph e = (XWPFParagraph)element;

                    // 如果遇到一个标题
                    if ( isTitle(e) ){
                        // 输出当前文件
                        newXdoc = initDoc(newXdoc);
                        newXdoc.write(fos);
                        fos.close();
                        // 重置输出流
                        String newFilePath = targetFilePath + "\\" + e.getText().trim() +"_"+ fileIndex++ +"." + DOC_TYPE;
                        fos = new FileOutputStream(new File(newFilePath));
                        // 重置转储对象
                        newXdoc = new XWPFDocument();
                        // 重置文档坐标参数
                        pIndex = 0;
                        tableIndex = 0;
                        continue;
                    }

                    // 是否是一个图片
                    List<XWPFRun> runs = e.getRuns();
                    if ( runs.size() > 0){

                        XWPFRun xwpfRun = runs.get(0);
                        List<XWPFPicture> embeddedPictures = xwpfRun.getEmbeddedPictures();

                        // 是否是一个图片
                        if(embeddedPictures.size() > 0) {
                            if (embeddedPictures.size() != 1) {
                                throw new RuntimeException("图片读取异常");
                            }
                            // 插入图片
                            XWPFPictureData pdata =  embeddedPictures.get(0).getPictureData();
                            ByteArrayInputStream is = new ByteArrayInputStream(pdata.getData());

                            XWPFParagraph paragraph = newXdoc.createParagraph();
                            paragraph.setAlignment(ParagraphAlignment.CENTER);
                            XWPFRun run = paragraph.createRun();

                            ImageIcon img = new ImageIcon(tempPicturePath + "\\" + pdata.getFileName());
                            int widthImg = img.getIconWidth();
                            int heightImg = img.getIconHeight();
                            int width = IMG_WIDTH;
                            int height = (IMG_WIDTH * heightImg/widthImg);

                            run.addPicture(is, pdata.getPictureType(), pdata.getFileName(), Units.toEMU(width), Units.toEMU(height));
                            pIndex++;
                        }else {
                            newXdoc.createParagraph();
                            newXdoc.setParagraph(e, pIndex++);
                        }
                    }else {
                        newXdoc.createParagraph();
                        newXdoc.setParagraph(e, pIndex++);
                    }
                    // 处理表格
                }else if (element.getElementType().equals(BodyElementType.TABLE)) {
                    XWPFTable e = (XWPFTable)element;
                    newXdoc.createTable();
                    newXdoc.setTable(tableIndex++,e);

                    // 处理意外情况，几乎不可能用到
                }else {
                    System.out.println("遇到未知的元素---------------");
                }
            }

            initDoc(newXdoc);
            newXdoc.write(fos);
            fos.close();

        }finally {
            deleteTempPics(tempPicturePath);
        }
    }

    /**
     * 创建一个空的转储对象
     * @return 转储对象
     * @throws IOException
     * @throws XmlException
     */
    private XWPFDocument createEmptyDoc()  {
        return new XWPFDocument();
    }

    /**
     * 为文档设置基本属性
     * @param newXdoc
     * @throws IOException
     * @throws XmlException
     */
    private XWPFDocument initDoc(XWPFDocument newXdoc) throws IOException, XmlException {
        newXdoc.createParagraph();
        newXdoc.createStyles();
        CTSectPr sectPr = newXdoc.getDocument().getBody().addNewSectPr();
        XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(newXdoc, sectPr);
        headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
        headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

        return newXdoc;
    }


    // 判断段落是否是标题
    private Boolean isTitle(XWPFParagraph paragraph){
        if ( paragraph .isEmpty() ) {
            return false;
        }
        List<XWPFRun> runs = paragraph.getRuns();

        if (runs.size() == 0){
            return  false;
        }

        XWPFRun run = runs.get(0);
        double fontsize = (double)(run.getFontSize());
        String fontfamily = (String)run.getFontFamily();

        return fontsize >= 14;
    }

    // 保存图片到临时文件
    private void saveTempPic(List<XWPFPictureData> picdatas, String tempPicturePath) throws IOException {
        new File(tempPicturePath).mkdir();
        for (XWPFPictureData pic: picdatas) {
            File file = new File(tempPicturePath+"\\"+ pic.getFileName());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(pic.getData());
            out.writeTo(new FileOutputStream(file));
            out.close();
        }
    }

    // 删除临时文件
    private void deleteTempPics(String tempPicturePath) {
        deleteDir(tempPicturePath);
    }

    /**
     * 迭代删除文件夹
     * @param dirPath 文件夹路径
     */
    private void deleteDir(String dirPath)
    {
        File file = new File(dirPath);
        if(file.isFile())
        {
            file.delete();
        }else
        {
            File[] files = file.listFiles();
            if(files == null)
            {
                file.delete();
            }else
            {
                for (int i = 0; i < files.length; i++)
                {
                    deleteDir(files[i].getAbsolutePath());
                }
                file.delete();
            }
        }
    }

    public static void main(String[] args) throws IOException, InvalidFormatException, XmlException {
        WordParse wordParse = new WordParse();
        wordParse.parseDocx(new File("D:\\fileSplitTest\\GB202001-规范测试文件.docx"));
    }
}
