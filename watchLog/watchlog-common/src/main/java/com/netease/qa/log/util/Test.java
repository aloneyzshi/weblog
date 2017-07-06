package com.netease.qa.log.util;

import com.sun.deploy.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hzquguoqing on 2016/9/27.
 */
public class Test {
    public static void main(String[] args) {
        //被替换关键字的的数据源
        Map<String,String> tokens = new HashMap<String,String>();
        tokens.put("signature", "#quguoqing#");
        tokens.put("sign", "#meiling#");
        List<String> tokens1 = new ArrayList<String>();
        tokens1.add("signature");
        tokens1.add("sign");

        //匹配类似velocity规则的字符串
        String template = "http://paygate.163.com/servlet/OrderServlet?payMethod=AlipayAll.AlipayWap&" +
                "storeName=kaola_domestic_hangzhou_youmai_new&" +
                "goodsType=0&" +
                "goodsTypeSecond=%D0%E9%C4%E2%C9%CC%C6%B7&" +
                "goodsTypeThird=%B5%E3%BF%A8&" +
                "goodsName=%CD%F8%D2%D7%BF%BC%C0%AD%BA%A3%B9%BA&" +
                "platformId=2088221256665933&" +
                "platformTradeId=201610291543GORDER25465007&" +
                "platformTradeTime=20161029154300&" +
                "tradeAmount=88.2&" +
                "notifyUrl=http%3A%2F%2Fpayment.kaola.com%2Fcallback%2Fpayment%2FalipayNotify&" +
                "payAccountId=m18282715527%40163.com&" +
                "timeout=29m&" +
                "signature=4c308b5a53da0ee5dfad566099b1d6816d4767dd8c74a6a8f17044a610a8aa90b165fb00f30984fe4ebeeeffd5d9a603214ad69e476654839150dabaa68c3b824a7b161a789353232ca288895b083938d650b0067953baae25c6ace49c3da544624ade17011f87d567f3300380c64036e07dc01a1933c79e9fbfefeff3ecb9dc";
        String template1 = "_input_charset=\"UTF-8\"&" +
                "body=\"[网易考拉海购]订单编号201610291535GORDER22371249\"&" +
                "it_b_pay=\"29m\"&" +
                "notify_url=\"http://paygate.163.com/servlet/AlipayAll?action=back\"&" +
                "out_trade_no=\"201610291535GORDER22371249\"&" +
                "partner=\"2088221256665933\"&" +
                "payment_type=\"1\"&" +
                "paymethod=\"directPay\"&" +
                "seller_id=\"2088221256665933\"&" +
                "service=\"mobile.securitypay.pay\"&" +
                "sign=\"Mui7HOb53YWHsPGJjKBnjg8jBg5LsNhAhbQh0miuArPePQd7E62DBLeLNjC7ZUivSgxeiSCPz4HfXMu0lmccs7yH8hS%2BIMDej99SBcIdDkLA7%2BppUMJQGpeChz8JTUqIcC4n3ETs0DtqqEGn%2FdDqHf15RpcfeIbskAosw%2Btc%2B0g%3D\"&" +
                "sign_type=\"RSA\"&" +
                "subject=\"[网易考拉海购]订单编号201610291535GORDER22371249\"&" +
                "total_fee=\"88.2\"";

        //生成匹配模式的正则表达式
        String patternString = "(" + StringUtils.join(tokens1, "|") + ")=";
        Pattern pattern = Pattern.compile(patternString);


//        StringBuffer sb = new StringBuffer();
//        while(matcher.find()){
//            System.out.println("matcher.group(1) = " + matcher.group(1));
//            matcher.appendReplacement(sb, tokens.get(matcher.group(1)));
//            System.out.println("sb = " + sb.toString());
//        }
//        matcher.appendTail(sb);

        String urlPattern = "&";
        Pattern pattern1 = Pattern.compile(urlPattern);
        String[] groups = pattern1.split(template);
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for(String str : groups){
            Matcher matcher = pattern.matcher(str);
            if(matcher.find()){
                continue;
            }
            i++;
            sb.append(str);
            if(i < groups.length - 1){
                sb.append("&");
            }
        }
        String result = sb.toString();

    }

}
