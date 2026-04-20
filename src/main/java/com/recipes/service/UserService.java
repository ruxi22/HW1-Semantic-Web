package com.recipes.service;

import com.recipes.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class UserService {

    private List<User> users = new ArrayList<>();
    private Document xmlDocument;
    private XPath xPath;
    private String xmlFilePath;

    @Autowired(required = false)
    private XmlValidationService validationService;

    public UserService() {
        loadUsers();
    }

    private void loadUsers() {
         try {
             String classPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
             File targetDir = new File(classPath).getParentFile();
             xmlFilePath = new File(targetDir, "users.xml").getAbsolutePath();
             
             System.out.println(" Looking for users XML at: " + xmlFilePath);
             
             File xmlFile = new File(xmlFilePath);
             Document doc = null;
             
             DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
             dbFactory.setNamespaceAware(true);
             DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
             
             if (xmlFile.exists() && xmlFile.length() > 0) {
                 System.out.println(" Loading users from file system: " + xmlFilePath + " (size: " + xmlFile.length() + " bytes)");
                 doc = dBuilder.parse(xmlFile);
             } else {
                 System.out.println(" Loading users from classpath resources");
                 InputStream inputStream = getClass().getClassLoader()
                         .getResourceAsStream("users.xml");
                 if (inputStream == null) {
                     System.err.println(" users.xml not found in resources");
                     return;
                 }
                 doc = dBuilder.parse(inputStream);
                 inputStream.close();

                 System.out.println(" Saving initial users to: " + xmlFilePath);
                 saveUsersToXML(doc);
             }

             validateAndLoadUsers();
             
             xmlDocument = doc;

             xPath = XPathFactory.newInstance().newXPath();

             NodeList userNodes = xmlDocument.getElementsByTagName("user");
             System.out.println(" Found " + userNodes.getLength() + " user elements in XML");

             for (int i = 0; i < userNodes.getLength(); i++) {
                 Element userElement = (Element) userNodes.item(i);
                 String id = userElement.getElementsByTagName("id").item(0).getTextContent();
                 String name = userElement.getElementsByTagName("name").item(0).getTextContent();
                 String surname = userElement.getElementsByTagName("surname").item(0).getTextContent();
                 String skillLevel = userElement.getElementsByTagName("cookingSkillLevel").item(0).getTextContent();
                 String preferredCuisine = userElement.getElementsByTagName("preferredCuisineType").item(0).getTextContent();

                 User user = new User(id, name, surname, skillLevel, preferredCuisine);
                 users.add(user);
             }

             System.out.println(" Loaded " + users.size() + " users into memory");
         } catch (ParserConfigurationException | SAXException | IOException e) {
             System.err.println("❌ Error loading users: " + e.getMessage());
             e.printStackTrace();
         }
     }

    private void validateAndLoadUsers() {
        try {
            if (validationService != null) {
                System.out.println(" Validating users.xml against users.xsd...");
                if (validationService.validateUsersXml(xmlDocument)) {
                    System.out.println(" XML Validation passed: users.xml is valid");
                } else {
                    System.err.println("  XML Validation warnings/errors detected, but continuing...");
                }
            } else {
                System.out.println("  ValidationService not available, skipping schema validation");
            }
        } catch (Exception e) {
            System.err.println(" Error during validation: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }


    public User getUserById(String id) {
        try {
            String xPathExpression = "//user[id='" + id + "']";
            Element userElement = (Element) xPath.evaluate(
                    xPathExpression,
                    xmlDocument,
                    XPathConstants.NODE
            );

            if (userElement != null) {
                return parseUserElement(userElement);
            }
        } catch (XPathExpressionException e) {
            System.err.println(" XPath error in getUserById: " + e.getMessage());
        }

        return null;
    }

    public User getFirstUser() {
        try {
            String xPathExpression = "//user[1]";
            Element userElement = (Element) xPath.evaluate(
                    xPathExpression,
                    xmlDocument,
                    XPathConstants.NODE
            );

            if (userElement != null) {
                return parseUserElement(userElement);
            }
        } catch (XPathExpressionException e) {
            System.err.println(" XPath error in getFirstUser: " + e.getMessage());
        }

        return users.isEmpty() ? null : users.get(0);
    }

    public User addUser(User user) {
        if (user == null || user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException(" User name is required");
        }
        
        if (user.getSurname() == null || user.getSurname().isEmpty()) {
            throw new IllegalArgumentException(" User surname is required");
        }
        
        if (user.getCookingSkillLevel() == null || user.getCookingSkillLevel().isEmpty()) {
            throw new IllegalArgumentException(" Cooking skill level is required");
        }
        
        if (user.getPreferredCuisineType() == null || user.getPreferredCuisineType().isEmpty()) {
            throw new IllegalArgumentException(" Preferred cuisine type is required");
        }

        if (user.getId() == null || user.getId().isEmpty()) {
            int maxId = users.stream()
                    .map(u -> Integer.parseInt(u.getId().substring(1)))
                    .max(Integer::compareTo)
                    .orElse(0);
            user.setId("U" + String.format("%03d", maxId + 1));
        }
        users.add(user);

        addUserToXMLDocument(user);
        saveUsersToXML(xmlDocument);
        
        System.out.println(" User saved: " + user.getId() + " - " + user.getName() + " " + user.getSurname());
        return user;
    }

    private void addUserToXMLDocument(User user) {
        try {
            Element rootElement = xmlDocument.getDocumentElement();

            Element userElement = xmlDocument.createElement("user");

            Element idElement = xmlDocument.createElement("id");
            idElement.setTextContent(user.getId());
            userElement.appendChild(idElement);

            Element nameElement = xmlDocument.createElement("name");
            nameElement.setTextContent(user.getName());
            userElement.appendChild(nameElement);

            Element surnameElement = xmlDocument.createElement("surname");
            surnameElement.setTextContent(user.getSurname());
            userElement.appendChild(surnameElement);

            Element skillElement = xmlDocument.createElement("cookingSkillLevel");
            skillElement.setTextContent(user.getCookingSkillLevel());
            userElement.appendChild(skillElement);

            Element cuisineElement = xmlDocument.createElement("preferredCuisineType");
            cuisineElement.setTextContent(user.getPreferredCuisineType());
            userElement.appendChild(cuisineElement);

            rootElement.appendChild(userElement);
        } catch (Exception e) {
            System.err.println(" Error adding user to XML: " + e.getMessage());
        }
    }
    private void saveUsersToXML(Document doc) {
        try {
            if (xmlFilePath == null) {
                String classPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
                File targetDir = new File(classPath).getParentFile();
                xmlFilePath = new File(targetDir, "users.xml").getAbsolutePath();
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("encoding", "UTF-8");
            
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(source, result);
            
            System.out.println(" Users saved to file: " + xmlFilePath);
        } catch (Exception e) {
            System.err.println(" Error saving users to XML file: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private User parseUserElement(Element userElement) {
        try {
            String id = userElement.getElementsByTagName("id").item(0).getTextContent();
            String name = userElement.getElementsByTagName("name").item(0).getTextContent();
            String surname = userElement.getElementsByTagName("surname").item(0).getTextContent();
            String skillLevel = userElement.getElementsByTagName("cookingSkillLevel").item(0).getTextContent();
            String preferredCuisine = userElement.getElementsByTagName("preferredCuisineType").item(0).getTextContent();

            return new User(id, name, surname, skillLevel, preferredCuisine);
        } catch (Exception e) {
            System.err.println(" Error parsing user element: " + e.getMessage());
            return null;
        }
    }
}

