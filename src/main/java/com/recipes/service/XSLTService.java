package com.recipes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jakarta.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

@Service
public class XSLTService {

    private Templates xslTemplates;
    private Document xmlDocument;
    private Document usersDocument;
    
    @Autowired(required = false)
    private UserService userService;

    @Autowired(required = false)
    private RecipeService recipeService;

    public XSLTService() {
    }


    @PostConstruct
    public void initializeXSLT() {
        try {
            InputStream xslInputStream = getClass().getClassLoader()
                    .getResourceAsStream("recipes.xsl");
            if (xslInputStream == null) {
                System.err.println(" recipes.xsl not found in resources");
                return;
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            StreamSource xslSource = new StreamSource(xslInputStream);
            xslTemplates = transformerFactory.newTemplates(xslSource);

            System.out.println(" XSLT initialized successfully");
        } catch (Exception e) {
            System.err.println(" Error initializing XSLT: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private Document loadRecipesXMLFromFile() {
        try {
            String userDir = System.getProperty("user.dir");
            File targetDir = new File(userDir).getParentFile();
            File xmlFile = new File(targetDir, "recipes/target/recipes.xml");

            if (!xmlFile.exists()) {
                System.out.println("️  Using fallback recipes from classpath");
                // Fallback to classpath
                InputStream xmlInputStream = getClass().getClassLoader()
                        .getResourceAsStream("recipes.xml");
                if (xmlInputStream == null) {
                    System.err.println(" recipes.xml not found");
                    return null;
                }
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                dbFactory.setNamespaceAware(true);
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                return dBuilder.parse(xmlInputStream);
            }

            System.out.println(" Loading recipes from: " + xmlFile.getAbsolutePath());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse(xmlFile);
        } catch (Exception e) {
            System.err.println(" Error loading recipes XML: " + e.getMessage());
            return null;
        }
    }

    private Document loadUsersXMLFromFile() {
        try {
            String userDir = System.getProperty("user.dir");
            File targetDir = new File(userDir).getParentFile();
            File xmlFile = new File(targetDir, "recipes/target/users.xml");

            if (!xmlFile.exists()) {
                System.out.println(" Using fallback users from classpath");
                // Fallback to classpath
                InputStream xmlInputStream = getClass().getClassLoader()
                        .getResourceAsStream("users.xml");
                if (xmlInputStream == null) {
                    System.err.println(" users.xml not found");
                    return null;
                }
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                dbFactory.setNamespaceAware(true);
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                return dBuilder.parse(xmlInputStream);
            }

            System.out.println(" Loading users from: " + xmlFile.getAbsolutePath());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse(xmlFile);
        } catch (Exception e) {
            System.err.println(" Error loading users XML: " + e.getMessage());
            return null;
        }
    }


    private String getUserSkillLevel(String userId) {
        try {
            if (usersDocument == null) {
                System.err.println(" Users document not loaded");
                return "Intermediate";
            }

            if (userId != null && !userId.isEmpty() && userService != null) {
                com.recipes.model.User user = userService.getUserById(userId);
                if (user != null) {
                    String skillLevel = user.getCookingSkillLevel();
                    System.out.println(" User " + userId + " skill level: " + skillLevel);
                    return skillLevel;
                }
            }

            Element firstUser = (Element) usersDocument.getElementsByTagName("user").item(0);
            if (firstUser == null) {
                System.err.println(" No users found in users.xml");
                return "Intermediate";
            }

            String skillLevel = firstUser.getElementsByTagName("cookingSkillLevel").item(0).getTextContent();
            System.out.println(" First user skill level: " + skillLevel);
            return skillLevel;
        } catch (Exception e) {
            System.err.println(" Error getting user skill level: " + e.getMessage());
            return "Intermediate";
        }
    }


    public String transformRecipesToHTML(String userId) {
        try {
            if (xslTemplates == null) {
                return "<html><body><h1>Error: XSLT not properly initialized</h1></body></html>";
            }

            xmlDocument = loadRecipesXMLFromFile();
            usersDocument = loadUsersXMLFromFile();

            if (xmlDocument == null) {
                return "<html><body><h1>Error: Recipes XML not loaded</h1></body></html>";
            }

            Transformer transformer = xslTemplates.newTransformer();

            String userSkillLevel = getUserSkillLevel(userId);
            transformer.setParameter("userSkill", userSkillLevel);

            Source source = new DOMSource(xmlDocument);
            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult(stringWriter);

            transformer.transform(source, result);

            String html = stringWriter.toString();
            System.out.println(" XSLT transformation successful with user skill: " + userSkillLevel);
            return html;
        } catch (Exception e) {
            System.err.println(" Error during XSLT transformation: " + e.getMessage());
            e.printStackTrace();
            return "<html><body><h1>Error: XSLT transformation failed</h1><p>" + 
                   e.getMessage() + "</p></body></html>";
        }
    }
}

