package org.violetime.autopers.reflect;

import org.w3c.dom.Node;

public class AutopersReflectXml {

	/**
	 *
	 * @param node
	 * @param cls
	 * @return
	 */
	public static Object initObjectByXmlNode(Node node, Class cls) {
		try {
			Object object = cls.getConstructors()[0].newInstance();
			AutopersReflectObject reflectObject = new AutopersReflectObject(
					object);
			for (int index = 0; index < node.getChildNodes().getLength(); index++) {
				Node propertyNode = node.getChildNodes().item(index);
				if (propertyNode.getNodeName().startsWith("#"))
					continue;
				String name = propertyNode.getAttributes().getNamedItem("name")
						.getTextContent();
				String value = propertyNode.getAttributes()
						.getNamedItem("value").getTextContent();
				if (value != null && value.length() > 0) {
					try {
						reflectObject.setField(name, value);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
			return reflectObject.getObject();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
/**
 * @param node
 * @param cls
 * @return
 */
	public static Object initObjectByXmlNodes(Node node, Class cls) {
		try {
			Object object = cls.getConstructors()[0].newInstance(null);
			
			AutopersReflectObject reflectObject = new AutopersReflectObject(
					object);
			
			for (int index = 0; index < node.getAttributes()
					.getLength(); index++) {
				Node attrNode= node.getAttributes().item(index);
				String attrName=attrNode.getNodeName();
				String attrValue=attrNode.getNodeValue();
				if(attrValue!=null&&attrValue.length()>0){
					reflectObject.setField(attrName, attrValue);
				}
			}
			
			return reflectObject.getObject();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
