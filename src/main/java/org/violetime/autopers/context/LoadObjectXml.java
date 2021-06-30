package org.violetime.autopers.context;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.violetime.autopers.mapping.*;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.violetime.autopers.generator.Generator;
import org.violetime.autopers.reflect.AutopersReflectClass;
import org.violetime.autopers.reflect.AutopersReflectXml;

public class LoadObjectXml {

	private final static Logger logger=Logger.getLogger("Autopers");

	public void autoLoad( List<InputStream> mappings){

		logger.info("load Object of XML  ");
		if(mappings!=null)
			for(InputStream file:mappings){
					load(file);
			}
		logger.info("Entity class configuration file loading completed");
	}
	public void load(InputStream xmlFile){
		
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    try
	    {
	      DocumentBuilder builder = factory.newDocumentBuilder();
	      Document doc = builder.parse(xmlFile);
	      Node root = doc.getLastChild();
	      for(int index=0;index<root.getChildNodes().getLength();index++){
	    	  Node node =root.getChildNodes().item(index);
	    	  if(node.getNodeName().equals("class")){
	    		  loadMappingClass(node);
	    	  }
	      }
	    }catch(Exception e){
	    	e.printStackTrace();
	    	 return ;
	    }
	    return ;
		
	}
	public void loadMappingClass(Node classNode){
		AutopersMappingClass mappingClass= (AutopersMappingClass) AutopersReflectXml.initObjectByXmlNodes(classNode, AutopersMappingClass.class);
	//	StringBuffer logs=new StringBuffer();
		for(int index=0;index<classNode.getChildNodes().getLength();index++){
			Node fieldNode=classNode.getChildNodes().item(index);
			if(fieldNode.getNodeName().startsWith("#"))
				continue;
			AutopersMappingField mappingField= (AutopersMappingField)AutopersReflectXml.initObjectByXmlNodes(fieldNode, AutopersMappingField.class);
			if(fieldNode.getChildNodes()!=null&&fieldNode.getChildNodes().getLength()>0){
				for(int indexSub=0;indexSub<fieldNode.getChildNodes().getLength();indexSub++){
					Node subNode=fieldNode.getChildNodes().item(indexSub);
					if(subNode.getNodeName().startsWith("#"))
						continue;
					if(subNode.getNodeName().equals("generator")){
						String className=subNode.getAttributes().getNamedItem("class").getTextContent();
						Generator generator= (Generator) AutopersReflectClass.getObject(className);
						mappingField.setGenerator(generator);
					}else if(subNode.getNodeName().equals("foreign")){
						AutopersMappingForeign mappingForeign=(AutopersMappingForeign)AutopersReflectXml.initObjectByXmlNodes(subNode, AutopersMappingForeign.class);
						mappingField.setMappingForeign(mappingForeign);
					}
				}
			}
			List<IAutopersMappingField> mappingFields=mappingClass.getFields();
			if(mappingFields==null)
				mappingFields=new ArrayList<IAutopersMappingField>();
			//logs.append(mappingField.getName()).append(",");
			mappingFields.add(mappingField);
			mappingClass.setFields(mappingFields);
		}
		//logger.info("fields : "+logs.toString());
		logger.info(mappingClass.getClassPath().substring( mappingClass.getClassPath().lastIndexOf(".")+1)+" mapping loaded");
		AutopersMapping.addMappingClass(mappingClass);
	}
	
}
