package com.company;


import com.company.pojo.ItemData;
import com.jayway.jsonpath.JsonPath;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.regex.Pattern;


public class Parser {


    private Elements searcher(String query){
        //searching the items
        String request = "https://www.aboutyou.de/suche?20201&term="+query;

        System.out.println("Sending request " + request );

        try {
            Document connect = Jsoup
                    .connect(request)
                    .timeout(5000).get();

            Elements results = connect.getElementsByAttributeValue("class","product-image loaded  ");



            return results;

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


    public void getDataAndSave(String query) throws IOException, InterruptedException {



        //Getting links
        try {
        ItemData gd = new ItemData();

        Elements results = searcher(query);


        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder  = null;

            docBuilder = factory.newDocumentBuilder();

        org.w3c.dom.Document doc = docBuilder.newDocument();
            org.w3c.dom.Element root = doc.createElement("offers");
            doc.appendChild(root);
            int connectionAmmounts = 1;
            int itemsGetted = 0;
        for(Element result:results){
            Thread.sleep(15000);                    //15 seconds pause to avoid possible bot detection
            connectionAmmounts=connectionAmmounts+1;
            itemsGetted = itemsGetted+1;
            Document resultElements = Jsoup.parse(result.toString());

            Elements parseLinks = resultElements.select("a");
            String href = parseLinks.attr("href");

            String parsingLink = "https://www.aboutyou.de"+href;

               Document parsingGood = Jsoup
                       .connect(parsingLink)
                       .timeout(15000).get();

               //Getting color from json file and saving it
                Element jsonScript = parsingGood.getElementsByAttributeValue("data-reactid","40").select("script").first();
                 String[] jsonScriptSplit = jsonScript.data().toString().split("=",2);

                 String json = jsonScriptSplit[1];
                List<String> colors = JsonPath.parse(json).read("$.adpPage.product.styles..color");
                if(jsonScript.data().contains("")){
                    jsonScript = parsingGood.getElementsByAttributeValue("data-reactid","38").select("script").first();  //some pages keep color and price in another tag
                    jsonScriptSplit = jsonScript.data().toString().split("=",2);
                     json = jsonScriptSplit[1];

                     colors = JsonPath.parse(json).read("$.adpPage.product.styles..color");
                }

            for(String color:colors){
                //Getting and saving product name and brand
                Element productNameAndBrand = parsingGood.getElementsByAttributeValue("class","productName_192josg").first();
                String[] splitedNameAndBrand = productNameAndBrand.text().split(Pattern.quote("|"));
                String productBrand = splitedNameAndBrand[0];
                gd.setBrand(productBrand);
                String productName = splitedNameAndBrand[1];
                gd.setName(productName);


                //Gettint and saving description
                Element productDescription = parsingGood.getElementsByAttributeValue("class","outerWrapper_gdz8cm").first();
                gd.setDescription(productDescription.text());

                //Getting and saving articleID
                Element articleElement = parsingGood.getElementsByAttributeValue("class","extrasWrapper_78iygn").parents().first();
                String[] unsplitedArticle =articleElement.text().split("Artikel-Nr:");
                String articleID = unsplitedArticle[1];
                gd.setArticleID(articleID);

                //Getting and saving price
                List<String> price = JsonPath.parse(json).read("$.adpPage.product.trackingData..price");
                gd.setPrice(price.get(0));

                org.w3c.dom.Element offer = doc.createElement("offer");
                root.appendChild(offer);

                org.w3c.dom.Element nameEl = doc.createElement("name");
                nameEl.appendChild(doc.createTextNode(gd.getName()));
                offer.appendChild(nameEl);

                org.w3c.dom.Element brandEl = doc.createElement("brand");
                brandEl.appendChild(doc.createTextNode(gd.getBrand()));
                offer.appendChild(brandEl);

                org.w3c.dom.Element priceEL = doc.createElement("price");
                priceEL.appendChild(doc.createTextNode(gd.getPrice()));
                offer.appendChild(priceEL);

                org.w3c.dom.Element artickleEl = doc.createElement("articleID");
                artickleEl.appendChild(doc.createTextNode(gd.getArticleID()));
                offer.appendChild(artickleEl);

                org.w3c.dom.Element descrEl = doc.createElement("description");
                descrEl.appendChild(doc.createTextNode(gd.getDescription()));
                offer.appendChild(descrEl);

                org.w3c.dom.Element colorEl = doc.createElement("color");
                colorEl.appendChild(doc.createTextNode(color));
                offer.appendChild(colorEl);
            }



            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();


            Source src = new DOMSource(doc);
            Result dest = new StreamResult(new File("output.xml"));
            aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            aTransformer.transform(src, dest);

            }
            System.out.println("connections ammount "+connectionAmmounts);
            System.out.println("iittems extracted "+ itemsGetted);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    }


