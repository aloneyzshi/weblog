package com.me.storm.datatopology.xml;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.me.storm.datatopology.util.GlobalDef;

public class FilterXml {
	// xml路径
	private String fd = null;
	// MetaBolt参数
	// 匹配条件间的逻辑关系
	public static String MatchLogic;
	// !--匹配类型列表
	public static String MatchType;
	// !--匹配字段列表-
	public static String MatchField;
	// !--字段值列表-
	public static String FieldValue;

	public FilterXml(String str) {
		this.fd = str;
	}

	public void read() {
		try {
			File file = new File(this.fd);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);

			NodeList nl = doc.getElementsByTagName(GlobalDef.Parameter);

			Element e = (Element) nl.item(0);

			MatchLogic = e.getElementsByTagName(GlobalDef.MatchLogic).item(0).getFirstChild().getNodeValue();
			MatchType = e.getElementsByTagName(GlobalDef.MatchType).item(0).getFirstChild().getNodeValue();
			MatchField = e.getElementsByTagName(GlobalDef.MatchField).item(0).getFirstChild().getNodeValue();
			FieldValue = e.getElementsByTagName(GlobalDef.FieldValue).item(0).getFirstChild().getNodeValue();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
