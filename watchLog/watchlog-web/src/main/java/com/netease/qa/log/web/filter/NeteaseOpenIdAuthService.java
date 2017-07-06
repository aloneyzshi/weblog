package com.netease.qa.log.web.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.netease.qa.log.util.HttpUtils;

public class NeteaseOpenIdAuthService {
    private static Logger logger = LoggerFactory.getLogger(NeteaseOpenIdAuthService.class);
    private String openIdHost = "https://login.netease.com/openid/";

    public final Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(5000)
            .expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    return cache.getIfPresent(key);
                }
            });

    /**
     * default:openIdHost = "https://login.netease.com/openid/"
     */
    public NeteaseOpenIdAuthService() {
    }

    /**
     * 创建NetEase OpenID的验证服务 .
     * @param openIdHost
     *
     */
    public NeteaseOpenIdAuthService(String openIdHost) {
        this.openIdHost = openIdHost;
    }

    /**
     * 获取返回Url，并使用应用内部缓存（不可用于分布式环境） .
     * @param returnTo
     * @param realm
     * @return
     */
    public String getRedirectUrl(String returnTo, String realm) {
        return getRedirectUrl(returnTo, realm, null);
    }
    /**
     * 获取返回Url.
     * @param returnTo
     * @param realm
     * @return
     */
    public String getRedirectUrl(String returnTo, String realm, HttpSession session) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("openid.mode", "associate");
        params.put("openid.assoc_type", "HMAC-SHA256");
        params.put("openid.session_type", "no-encryption");

        HttpUtils.Response response = HttpUtils.httpPost(openIdHost, params);
        if (response.getCode() == 200) {
            Map<String, String> result = getResultMap(response.getContent());

            String assocHandle = result.get("assoc_handle");
            Map<String, String> queryMap = getAuthQuery(assocHandle, returnTo, realm);

            if (session != null) {
                session.setAttribute(assocHandle, result.get("mac_key"));
            } else {
                cache.put(assocHandle, result.get("mac_key"));
            }
            logger.debug("set:" + assocHandle + " " + result.get("mac_key"));

            String query = urlEncode(queryMap, "utf-8");
            String redirectUrl = openIdHost + "?" + query;
            logger.debug("redirectUrl:" + redirectUrl);
            return redirectUrl;
        }

        return null;
    }

    private static Map<String, String> getAuthQuery(String assocHandle,
            String returnTo, String realm) {
        Map<String, String> query = new HashMap<String, String>();
        query.put("openid.ns", "http://specs.openid.net/auth/2.0");
        query.put("openid.mode", "checkid_setup");
        query.put("openid.assoc_handle", assocHandle);
        query.put("openid.return_to", returnTo);
        query.put("openid.claimed_id", "http://specs.openid.net/auth/2.0/identifier_select");
        query.put("openid.identity", "http://specs.openid.net/auth/2.0/identifier_select");
        query.put("openid.realm", realm);
        query.put("openid.ns.sreg", "http://openid.net/extensions/sreg/1.1");
        query.put("openid.sreg.required", "nickname,email,fullname");
        query.put("openid.ns.ax", "http://openid.net/srv/ax/1.0");
        query.put("openid.ax.mode", "fetch_request");
        query.put("openid.ax.type.empno", "https://login.netease.com/openid/empno/");
        query.put("openid.ax.type.dep", "https://login.netease.com/openid/dep/");
        query.put("openid.ax.required", "empno,dep");
        return query;
    }

    private Map<String, String> getResultMap(String content) {
        Map<String, String> result = new HashMap<String, String>();
        for (String line : content.split("\n")) {
            String[] array = line.split(":");
            if (array.length == 2) {
                result.put(array[0], array[1]);
            }
        }
        return result;
    }

    /**
     * 编码 Url.
     * @param params
     * @param charsetName
     * @return
     */
    public static String urlEncode(Map<String, String> params, String charsetName) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Map.Entry<String, String> e : params.entrySet()) {
            try {
                if (firstTime) {
                    firstTime = false;
                } else {
                    sb.append("&");
                }
                sb.append(e.getKey())
                    .append("=")
                    .append(URLEncoder.encode(e.getValue(), charsetName));
            } catch (UnsupportedEncodingException e1) {
                logger.error(e1.getMessage(), e1);
            }
        }
        return sb.toString();
    }

    /**
     * 验证 OpenID 的返回值.
     * @param url
     * @param key
     * @return
     * @deprecated Use inner cache could not be used in distributed env.
     *      Use {@link #checkSignature(String, String, String)} instead.
     */
    public boolean authSuccess(String url, String key) {
        String macKey = cache.getIfPresent(key);
        if (macKey == null) {
            return false;
        }

        try {
            if (checkSignature(url, key, macKey)) {
                logger.debug("remove key from:" + key);
                cache.invalidate(key);
                return true;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 获取 OpenID 返回的mac_key.
     * @param key
     * @return
     */
    public String getMacKey(String key) {
        return cache.getIfPresent(key);
    }

    /**
     * 检查签名.
     * @param url
     * @param assocHandle
     * @param macKey
     * @return
     * @throws IOException
     */
    public boolean checkSignature(String url, String assocHandle, String macKey)
            throws IOException {
        // 将OpenID server返回的URL地址解析，获取参数，参数和值都需要UTF-8解码
        Map<String, String> authResponse = new HashMap<String, String>();
        // 注：这里最好不要先把整个URL进行UTF-8解码，然后再取出每个openid参数和值。
        // 因为openid.return_to或者openid.realm指定的URL可能需要带参数，这样
        // 会引入 ?, =, &等特殊符号，从而让openid的参数截取变得困难
        String [] arrays = url.split("\\?|&");
        int size = arrays.length;
        for (int i = 0; i < size; i++) {
            int index = arrays[i].indexOf("=");
            if (index == -1) {
                continue;
            }
            String arg = URLDecoder.decode(arrays[i].substring(0, index), "UTF-8");
            String val = URLDecoder.decode(arrays[i].substring(index + 1, arrays[i].length()),
                    "UTF-8");
            authResponse.put(arg, val);
        }

        if (authResponse.get("openid.mode").equals("id_res") == false) {
            logger.error("openid.mode 返回值不是 id_res, 认证失败!");
            return false;
        }

        if (authResponse.get("openid.assoc_handle").equals(assocHandle) == false) {
            logger.error("assoc_handle 不一致，使用check authentication！");
            // check_authentication();
            return false;
        }
        String [] signedItems = authResponse.get("openid.signed").split(",");
        String signedContent = "";
        for (int i = 0; i < signedItems.length; i++) {
            signedContent = signedContent + signedItems[i];
            signedContent = signedContent + ":";
            signedContent = signedContent + authResponse.get("openid." + signedItems[i]);
            signedContent = signedContent + "\n";
        }

        logger.debug("需要签名的参数和值:\n{}", signedContent);
        // 注意，mac_key也许要先进行base64解码
        byte [] decoded64 = Base64.decodeBase64(macKey);
        SecretKey signingKey = new SecretKeySpec(decoded64, "HMACSHA256");
        Mac mac = null;
        try {
            mac = Mac.getInstance("HMACSHA256");
            mac.init(signingKey);
            // 签名的字符串需要转换成 UTF-8 编码
            byte [] digest = mac.doFinal(signedContent.getBytes("UTF-8"));
            // 计算得到消息摘要之后，需要进行base64编码
            String signature = Base64.encodeBase64String(digest);
            logger.debug("openid server返回的签名是:" + authResponse.get("openid.sig"));
            logger.debug("consumer 计算出来的签名是:" + signature);
            if (signature.equals(authResponse.get("openid.sig"))) {
                return true;
            } else {
                logger.error("签名不一致，验证失败！");
                return false;
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }
}
