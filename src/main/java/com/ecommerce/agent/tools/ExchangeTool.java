package com.ecommerce.agent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;


public class ExchangeTool {
    @Tool(description = "User tool for exchanging products")
    public String exchangeGoods(
            @ToolParam(description = "Order Number")  String orderId) {
        StringBuilder response = new StringBuilder();

        // 1. 退换货流程说明
        response.append("感谢您的咨询，以下是退换货的完整流程：\n\n");
        response.append("【退货流程】\n");
        response.append("1. 登录您的账户，在 我的订单 中找到需要退货的商品，点击 申请售后 \n");
        response.append("2. 选择退货原因（如质量问题/不喜欢等），并上传相关凭证（可选）\n");
        response.append("3. 我们会在24小时内审核您的申请，审核通过后将发送退货地址\n");
        response.append("4. 请在7天内按照提供的地址寄回商品，并保留物流单号\n");
        response.append("5. 我们收到退货并确认无误后，将在3-5个工作日内处理退款\n\n");

        response.append("【换货流程】\n");
        response.append("1. 提交换货申请（步骤同退货），并注明需要更换的商品规格/颜色等\n");
        response.append("2. 审核通过后，我们将先为您发货新商品（特殊情况需先寄回原商品）\n");
        response.append("3. 收到新商品后，请在7天内将原商品寄回\n\n");

        // 2. 退换货地址信息
        response.append("【退换货地址】\n");
        response.append("收件人：电商客服中心\n");
        response.append("地址：上海市浦东新区张江高科技园区科苑路88号\n");
        response.append("邮编：201203\n");
        response.append("联系电话：400-888-8888（周一至周日 9:00-21:00）\n\n");

        // 3. 注意事项
        response.append("【注意事项】\n");
        response.append("- 请确保商品保持原包装、吊牌完整，不影响二次销售\n");
        response.append("- 若因质量问题退换货，我们承担运费；非质量问题需您自理\n");
        response.append("- 退换货时效：自签收之日起7天内可申请无理由退换，15天内质量问题包退包换\n\n");

        return response.toString();
    }
}
