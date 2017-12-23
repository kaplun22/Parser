package com.company.XML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class XmlDocBuilder {


    public Document xmlDoc() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element root = doc.createElement("offers");
        doc.appendChild(root);

        return doc;
    }


    public Document docElements(Document doc, String name,String brand,String price,String articleID,String description,String color){


        Element offer = doc.createElement("offer");
        doc.getFirstChild().appendChild(offer);

        Element nameEl = doc.createElement("name");
        nameEl.appendChild(doc.createTextNode(name));
        offer.appendChild(nameEl);

        Element brandEl = doc.createElement("brand");
        brandEl.appendChild(doc.createTextNode(brand));
        offer.appendChild(brandEl);

        Element priceEL = doc.createElement("price");
        priceEL.appendChild(doc.createTextNode(price));
        offer.appendChild(priceEL);

        Element artickleEl = doc.createElement("articleID");
        artickleEl.appendChild(doc.createTextNode(articleID));
        offer.appendChild(artickleEl);

        Element descrEl = doc.createElement("description");
        descrEl.appendChild(doc.createTextNode(description));
        offer.appendChild(descrEl);

        Element colorEl = doc.createElement("color");
        colorEl.appendChild(doc.createTextNode(color));
        offer.appendChild(colorEl);
        return  doc;
    }

    public void buildXMLFile(Document doc)  {
        try {
        TransformerFactory tranFactory = TransformerFactory.newInstance();
        Transformer aTransformer = tranFactory.newTransformer();
        Source src = new DOMSource(doc);
        Result dest = new StreamResult(new File("output.xml"));
        aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        aTransformer.transform(src, dest);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}



