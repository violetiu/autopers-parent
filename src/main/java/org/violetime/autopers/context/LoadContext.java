package org.violetime.autopers.context;

import java.io.InputStream;
import java.lang.annotation.Annotation;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.violetime.autopers.annotation.AutopersAnnotation;
import org.violetime.autopers.objects.part.AutopersPart;
import org.violetime.autopers.objects.part.AutopersPartFactory;
import org.violetime.autopers.units.AutopersCodeName;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.violetime.autopers.creator.AutoWriteFactory;
import org.violetime.autopers.creator.AutoWriteObjectXml;
import org.violetime.autopers.database.DataBaseFactory;
import org.violetime.autopers.database.DataBaseSource;
import org.violetime.autopers.reflect.AutopersReflectXml;

/**
 * 载入配置文件
 * @author taoyo
 *
 */
public class LoadContext {

	private final static Logger logger=Logger.getLogger("Autopers");
	/**
	 * 载入配置文件
	 * @param
	 * @return
	 */
	public  boolean load(InputStream contexIS){
		logger.log(Level.INFO,"Start loading autoPers configuration file:");
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    try
		    {
		      DocumentBuilder builder = factory.newDocumentBuilder();
		      Document doc = builder.parse(contexIS);
		      Node root = doc.getLastChild();
		      for(int index=0;index<root.getChildNodes().getLength();index++){
		    	  Node node =root.getChildNodes().item(index);
		    	  if(node.getNodeName().equals("databases")){
		    		  logger.info("Loading database connections");
		    		  loadDatabases(node);
		    	  }
		    	  if(node.getNodeName().equals("autowrite")){
		    		  logger.info("Loading Entity Class Autowrite Program");
		    		  loadAutoWriteObject(node);
		    	  }
		    	  if(node.getNodeName().equals("parts")){
					  logger.info("Loading parts");
					  loadParts(node);
				  }

			  }
		      logger.info("Autopers Profile Loading Completed");
		    }catch(Exception e){
		    	e.printStackTrace();
		    	 return false;
		    }
		    return true;
	}
	/**
	 * 加载数据库配置资源
	 * @param node
	 */
	private  void loadDatabases(Node  node){
		for(int index=0;index<node.getChildNodes().getLength();index++){
			Node dataBaseNode=node.getChildNodes().item(index);
			if(dataBaseNode.getNodeName().startsWith("#"))
				continue;
			DataBaseSource dataBaseSource=(DataBaseSource)( AutopersReflectXml.initObjectByXmlNode(dataBaseNode, DataBaseSource.class));
			DataBaseFactory.addDataBaseSource(dataBaseSource);
		}
		
	}
	/**
	 * 加载写入实体对象得配置文件
	 * @param node
	 */
	private void loadAutoWriteObject(Node node){
		String className= node.getAttributes().getNamedItem("class").getTextContent();
		try {
			AutoWriteObjectXml autoWriteObjectXml=(AutoWriteObjectXml) AutopersReflectXml.initObjectByXmlNode(node, Class.forName(className));
			AutoWriteFactory.setAutoWriteObjectXml(autoWriteObjectXml);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 *
	 * @param node
	 */

	private void loadParts(Node node){
		for(int index=0;index<node.getChildNodes().getLength();index++) {
			Node dataBaseNode = node.getChildNodes().item(index);
			if (dataBaseNode.getNodeName().startsWith("#"))
				continue;
			loadPart(dataBaseNode);
		}

	}
	private void loadPart(Node node){
		String className= node.getAttributes().getNamedItem("class").getTextContent();
		try {
			Class<AutopersPart> aClass= (Class<AutopersPart>) Class.forName(className);
			Annotation[] list=  aClass.getAnnotationsByType(AutopersAnnotation.AutopersPart.class);
			if(list.length>0){
				AutopersAnnotation.AutopersPart part= (AutopersAnnotation.AutopersPart) list[0];
				String table=part.table();
				String objectName= AutopersCodeName.className(table);
				AutopersPartFactory.put(objectName,aClass);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
