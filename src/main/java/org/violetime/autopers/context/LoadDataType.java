package org.violetime.autopers.context;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * 载入数据类型定义
 * @author taoyo
 *
 */
public class LoadDataType {
	private final static Logger logger=Logger.getLogger("Autopers");
	public HashMap<String, String> load(InputStream dataIS){
		logger.log(Level.INFO,"Start loading data definitions");

	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    HashMap<String, String> dataTypeMap=new HashMap<>();
	    try
	    {
	      DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = null;
	      if(dataIS==null){
			  doc=builder.parse("https://document.violetime.com/autopers/autopersDataType.xml");
		  }else{

			 doc=builder.parse(dataIS);

		  }

	      Node root = doc.getLastChild();
	      for(int index=0;index<root.getChildNodes().getLength();index++){
	    	  Node node =root.getChildNodes().item(index);
	    	  if(node.getNodeName().equals("java")){

	    		 String javaName=node.getAttributes().getNamedItem("name").getTextContent();
	    		 String javaClass=node.getAttributes().getNamedItem("class").getTextContent();
	    		  NodeList sqlNodeLIst= node.getChildNodes();
	    		  for(int indexNode=0;indexNode<sqlNodeLIst.getLength();indexNode++){
	    			  Node sqlNode=sqlNodeLIst.item(indexNode);
	    			  if(sqlNode.getNodeName().equals("sql")){
	    				 String sqlType= sqlNode.getAttributes().getNamedItem("name").getTextContent();
	    				 dataTypeMap.put(sqlType.toUpperCase(), javaClass);
	    			  }
	    		  }
	    	  }
	      }
	      
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    logger.info("Data definition loading is completed."+dataTypeMap.size());
	    return dataTypeMap;
	}
}
