package org.violetime.autopers.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.platform.AutopersPlatformFactory;
import org.violetime.autopers.platform.AutopersPlatformObject;
import org.violetime.autopers.platform.AutopersPlatformPackage;

public class LoadPlatform{
	private final static Logger logger=Logger.getLogger("Autopers");
    public boolean load(InputStream platformIS){
        logger.info("Start loading the platform configuration file");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try
        {
          DocumentBuilder builder = factory.newDocumentBuilder();
          Document doc =null;
          if(platformIS==null){
              doc= builder.parse("https://document.violetime.com/autopers/autopersPlatform.xml");
          }else{


             doc= builder.parse(platformIS);

          }
          HashMap<String,AutopersPlatform> platformMap=new HashMap<>();
          Node root =doc.getLastChild();
          for(int index=0;index<root.getChildNodes().getLength();index++){
              Node node =root.getChildNodes().item(index);
              if(!node.getNodeName().equals("platform")){
                  continue;
              }
              
              AutopersPlatform autoPersPlatform=new AutopersPlatform();
              autoPersPlatform.setName(node.getAttributes().getNamedItem("name").getTextContent());
              autoPersPlatform.setJdbc(node.getAttributes().getNamedItem("jdbc").getTextContent());
              HashMap<String,AutopersPlatformPackage>  packageMap= loadPackage(node.getChildNodes());
              autoPersPlatform.setPackageMap(packageMap);
              platformMap.put(autoPersPlatform.getJdbc(), autoPersPlatform);
              logger.info("load platform name is "+autoPersPlatform.getName()+",jdbc is "+autoPersPlatform.getJdbc());
              
          }
          AutopersPlatformFactory.setPlatformMap(platformMap);
          logger.info("Platform Configuration File Loading Completed.");
        }catch(Exception e){
            e.printStackTrace();
             return false;
        }
        return true;
    }
    public HashMap<String,AutopersPlatformPackage> loadPackage(NodeList nodeList){
        HashMap<String,AutopersPlatformPackage> packageMap=new HashMap<>();
        for(int index=0;index<nodeList.getLength();index++){
            Node node=nodeList.item(index);
            if(!node.getNodeName().equals("package"))
                continue;
            AutopersPlatformPackage platformPackage=new AutopersPlatformPackage();
            platformPackage.setPath(node.getAttributes().getNamedItem("path").getTextContent());
            HashMap<String,AutopersPlatformObject> objectMap=loadObject(node.getChildNodes());
            platformPackage.setObjectMap(objectMap);
            packageMap.put(platformPackage.getPath(), platformPackage);

            //logger.debug("laod package Path--> "+platformPackage.getPath());
        }

        return packageMap;
    }

    private HashMap<String, AutopersPlatformObject> loadObject(NodeList childNodes) {
        HashMap<String, AutopersPlatformObject> objectMap=new HashMap<>();
        for(int index=0;index<childNodes.getLength();index++){
            Node node=childNodes.item(index);
            if(!node.getNodeName().equals("object"))
                continue;
            AutopersPlatformObject object=new AutopersPlatformObject();
            object.setName(node.getAttributes().getNamedItem("name").getTextContent());
            object.setClassName(node.getAttributes().getNamedItem("class").getTextContent());
            HashMap<String,String> propertys =loadProperty(node.getChildNodes());
            object.setPropertys(propertys);
            objectMap.put(object.getName(), object);
        }


        return objectMap;
    }

    private HashMap<String,String> loadProperty(NodeList childNodes){
        HashMap<String, String> propertys=new HashMap<>();
        for(int index=0;index<childNodes.getLength();index++){
            Node node=childNodes.item(index);
            if(!node.getNodeName().equals("property"))
                continue;
            if(node.getAttributes().getNamedItem("name")==null)
            	continue;
            if(node.getAttributes().getNamedItem("value")==null)
            	continue;
            propertys.put(node.getAttributes().getNamedItem("name").getTextContent(), 
                        node.getAttributes().getNamedItem("value").getTextContent());
            //logger.debug("loadProperty --> "+node.getAttributes().getNamedItem("name").getTextContent());
        }
        return propertys;

    }

}
