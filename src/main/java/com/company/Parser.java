package com.company;


import com.company.XML.XmlDocBuilder;
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

            XmlDocBuilder xmlDocBuilder = new XmlDocBuilder();
            org.w3c.dom.Document doc =xmlDocBuilder.xmlDoc();



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

               //Getting color and price from json file and saving it
                Element jsonScript = parsingGood.getElementsByAttributeValue("data-reactid","40").select("script").first();
                if(jsonScript.data().contains("")){
                    jsonScript = parsingGood.getElementsByAttributeValue("data-reactid","38").select("script").first();     //some pages keep color and price in another tag

                }
                 String[] jsonScriptSplit = jsonScript.data().toString().split("=",2);
                 String json = jsonScriptSplit[1];
                List<String> colors = JsonPath.parse(json).read("$.adpPage.product.styles..color");


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

                //Data about initial price and shipping costs are not specified on that site
                xmlDocBuilder.docElements(xmlDocBuilder.xmlDoc(),gd.getName(),gd.getBrand(),gd.getPrice(),gd.getArticleID(),gd.getDescription(),color);


                //creating xml elements and sanding data to them
                xmlDocBuilder.docElements(doc,gd.getName(),gd.getBrand(),gd.getPrice(),gd.getArticleID(),gd.getDescription(),color);

            }

            xmlDocBuilder.buildXMLFile(doc);




            }
            System.out.println("connections ammount "+connectionAmmounts);
            System.out.println("ittems extracted "+ itemsGetted);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();

        }
    }

    }


