package com.ecommerce.agent.tools;




import cn.hutool.core.io.FileUtil;
import com.ecommerce.agent.constant.FileConstant;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
public class ReturnVoucherTool {

    // 基础字体设置
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);

    @Tool(description = "Tool for generating return and exchange vouchers")
    public String generateRefundPdf(
            @ToolParam(description = "Order Number") String orderId) throws IOException, DocumentException {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + UUID.randomUUID().toString() + ".pdf";

        // 创建并初始化一个PDF文档
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 1. 创建文档
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        // 2. 打开文档
        document.open();

        // 3. 添加内容
        addHeader(document, orderId);
        addBody(document);
        addFooter(writer, document);

        // 4. 关闭文档
        document.close();

        // 5. 将PDF保存到文件
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            baos.writeTo(fos);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }


        log.info("PDF generated successfully to: {}", filePath);
        return "PDF generated successfully to: " + filePath;
    }

    private void addHeader(Document document, String orderId) throws DocumentException {
        // 添加标题
        Paragraph title = new Paragraph("Return and Exchange Voucher", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // 添加订单信息
        Paragraph orderInfo = new Paragraph();
        orderInfo.add(new Chunk("Order Number: ", NORMAL_FONT));
        orderInfo.add(new Chunk(orderId, NORMAL_FONT));
        orderInfo.setSpacingAfter(10);
        document.add(orderInfo);

        // 添加分隔线
        Paragraph line = new Paragraph();
        line.add(new Chunk("\n"));
        LineSeparator ls = new LineSeparator();
        line.add(new Chunk(ls));
        document.add(line);
    }

    private void addBody(Document document) throws DocumentException {
        Paragraph content = new Paragraph();
        content.add(new Chunk("\nDear customer：\n\n", NORMAL_FONT));
        content.add(new Chunk("Thank you for submitting the return and exchange request. We have received your request and will process it within 3 working days.\n\n", NORMAL_FONT));
        content.add(new Chunk("Please return the product along with this voucher to the following address:\n", NORMAL_FONT));
        content.add(new Chunk("Delivery address: No. XX, XX Street, XX District, XX City, XX Province\n", NORMAL_FONT));
        content.add(new Chunk("Contact: Customer Service Department\n", NORMAL_FONT));
        content.add(new Chunk("Phone: 400-123-4567\n\n", NORMAL_FONT));
        document.add(content);
    }

    private void addFooter(PdfWriter writer, Document document) throws DocumentException {
        // 添加页脚
        Paragraph footer = new Paragraph();
        footer.add(new Chunk("\n"));
        footer.add(new Chunk("Thank you for your support | Customer service hotline: 400-123-4567",
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY)));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

    }



}
