package com.recipes.service;

import com.recipes.model.Recipe;
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
public class RecipeService {

    private List<Recipe> recipes = new ArrayList<>();
    private Document xmlDocument;
    private XPath xPath;
    private String xmlFilePath;

    @Autowired(required = false)
    private XmlValidationService validationService;

    public RecipeService() {
        loadRecipes();
    }

    private void loadRecipes() {
         try {
             String classPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
             File targetDir = new File(classPath).getParentFile();
             xmlFilePath = new File(targetDir, "recipes.xml").getAbsolutePath();
             
             System.out.println(" Looking for recipes XML at: " + xmlFilePath);

             File xmlFile = new File(xmlFilePath);
             Document doc = null;
             
             DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
             dbFactory.setNamespaceAware(true);
             DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
             
             if (xmlFile.exists() && xmlFile.length() > 0) {
                 System.out.println(" Loading recipes from file system: " + xmlFilePath + " (size: " + xmlFile.length() + " bytes)");
                 doc = dBuilder.parse(xmlFile);
             } else {
                 System.out.println(" Loading recipes from classpath resources");
                 InputStream inputStream = getClass().getClassLoader()
                         .getResourceAsStream("recipes.xml");
                 if (inputStream == null) {
                     System.err.println(" recipes.xml not found in resources");
                     return;
                 }
                 doc = dBuilder.parse(inputStream);
                 inputStream.close();

                 System.out.println("Saving initial recipes to: " + xmlFilePath);
                 saveRecipesToXML(doc);
             }

             validateAndLoadRecipes(doc);
             
             xmlDocument = doc;

             xPath = XPathFactory.newInstance().newXPath();

             NodeList recipeNodes = xmlDocument.getElementsByTagName("recipe");
             System.out.println(" Found " + recipeNodes.getLength() + " recipe elements in XML");

             for (int i = 0; i < recipeNodes.getLength(); i++) {
                 Element recipeElement = (Element) recipeNodes.item(i);
                 String id = recipeElement.getAttribute("id");
                 String title = recipeElement.getElementsByTagName("title").item(0).getTextContent();
                 String image = recipeElement.getElementsByTagName("image").item(0) != null
                         ? recipeElement.getElementsByTagName("image").item(0).getTextContent()
                         : null;

                 List<String> cuisineTypes = new ArrayList<>();
                 NodeList cuisineNodes = recipeElement.getElementsByTagName("cuisineType");
                 for (int j = 0; j < cuisineNodes.getLength(); j++) {
                     cuisineTypes.add(cuisineNodes.item(j).getTextContent());
                 }

                 List<String> difficultyLevels = new ArrayList<>();
                 NodeList difficultyNodes = recipeElement.getElementsByTagName("difficultyLevel");
                 for (int j = 0; j < difficultyNodes.getLength(); j++) {
                     difficultyLevels.add(difficultyNodes.item(j).getTextContent());
                 }

                 Recipe recipe = new Recipe(id, title, image, cuisineTypes, difficultyLevels);
                 recipes.add(recipe);
             }

             System.out.println(" Loaded " + recipes.size() + " recipes into memory");
         } catch (ParserConfigurationException | SAXException | IOException e) {
             System.err.println(" Error loading recipes: " + e.getMessage());
             e.printStackTrace();
        }
    }


    private void validateAndLoadRecipes(Document doc) {
        try {
            if (validationService != null) {
                System.out.println(" Validating recipes.xml against recipes.xsd...");
                if (validationService.validateRecipesXml(doc)) {
                    System.out.println(" XML Validation passed: recipes.xml is valid");
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

    public List<Recipe> getAllRecipes() {
        return new ArrayList<>(recipes);
    }


    public Recipe getRecipeById(String id) {
        try {
            String xPathExpression = "//recipe[@id='" + id + "']";
            Element recipeElement = (Element) xPath.evaluate(
                    xPathExpression,
                    xmlDocument,
                    XPathConstants.NODE
            );

            if (recipeElement != null) {
                return parseRecipeElement(recipeElement);
            }
        } catch (XPathExpressionException e) {
            System.err.println(" XPath error in getRecipeById: " + e.getMessage());
        }

        return null;
    }

    public Recipe addRecipe(Recipe recipe) {
        if (recipe == null || recipe.getTitle() == null || recipe.getTitle().isEmpty()) {
            throw new IllegalArgumentException(" Recipe title is required");
        }
        
        if (recipe.getCuisineTypes() == null || recipe.getCuisineTypes().isEmpty()) {
            throw new IllegalArgumentException(" At least one cuisine type is required");
        }
        
        if (recipe.getDifficultyLevels() == null || recipe.getDifficultyLevels().isEmpty()) {
            throw new IllegalArgumentException(" At least one difficulty level is required");
        }

        if (recipe.getId() == null || recipe.getId().isEmpty()) {
            int maxId = recipes.stream()
                    .map(r -> Integer.parseInt(r.getId().substring(1)))
                    .max(Integer::compareTo)
                    .orElse(0);
            recipe.setId("R" + String.format("%03d", maxId + 1));
        }
        recipes.add(recipe);

        addRecipeToXMLDocument(recipe);
        saveRecipesToXML(xmlDocument);
        
        System.out.println(" Recipe saved: " + recipe.getId() + " - " + recipe.getTitle());
        return recipe;
    }


    private void addRecipeToXMLDocument(Recipe recipe) {
        try {
            Element rootElement = xmlDocument.getDocumentElement();

            Element recipeElement = xmlDocument.createElement("recipe");
            recipeElement.setAttribute("id", recipe.getId());

            Element titleElement = xmlDocument.createElement("title");
            titleElement.setTextContent(recipe.getTitle());
            recipeElement.appendChild(titleElement);

            if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
                Element imageElement = xmlDocument.createElement("image");
                imageElement.setTextContent(recipe.getImage());
                recipeElement.appendChild(imageElement);
            }

            Element cuisineTypesElement = xmlDocument.createElement("cuisineTypes");
            for (String cuisine : recipe.getCuisineTypes()) {
                Element cuisineElement = xmlDocument.createElement("cuisineType");
                cuisineElement.setTextContent(cuisine);
                cuisineTypesElement.appendChild(cuisineElement);
            }
            recipeElement.appendChild(cuisineTypesElement);

            Element difficultyLevelsElement = xmlDocument.createElement("difficultyLevels");
            for (String level : recipe.getDifficultyLevels()) {
                Element difficultyElement = xmlDocument.createElement("difficultyLevel");
                difficultyElement.setTextContent(level);
                difficultyLevelsElement.appendChild(difficultyElement);
            }
            recipeElement.appendChild(difficultyLevelsElement);
            
            // Append to root
            rootElement.appendChild(recipeElement);
        } catch (Exception e) {
            System.err.println(" Error adding recipe to XML: " + e.getMessage());
        }
    }


    private void saveRecipesToXML(Document doc) {
        try {
            if (xmlFilePath == null) {
                String classPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
                File targetDir = new File(classPath).getParentFile();
                xmlFilePath = new File(targetDir, "recipes.xml").getAbsolutePath();
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("encoding", "UTF-8");
            
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(source, result);
            
            System.out.println(" Recipes saved to file: " + xmlFilePath);
        } catch (Exception e) {
            System.err.println(" Error saving recipes to XML file: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public List<Recipe> getRecipesBySkillLevel(String skillLevel) {
        List<Recipe> filtered = new ArrayList<>();
        try {
            String xPathExpression = "//recipe[difficultyLevels/difficultyLevel='" + skillLevel + "']";
            NodeList recipeNodes = (NodeList) xPath.evaluate(
                    xPathExpression,
                    xmlDocument,
                    XPathConstants.NODESET
            );

            for (int i = 0; i < recipeNodes.getLength(); i++) {
                Element recipeElement = (Element) recipeNodes.item(i);
                Recipe recipe = parseRecipeElement(recipeElement);
                if (recipe != null) {
                    filtered.add(recipe);
                }
            }
        } catch (XPathExpressionException e) {
            System.err.println(" XPath error in getRecipesBySkillLevel: " + e.getMessage());
        }
        return filtered;
    }


    public List<Recipe> getRecipesByCuisineType(String cuisineType) {
        List<Recipe> filtered = new ArrayList<>();
        try {
            String xPathExpression = "//recipe[cuisineTypes/cuisineType='" + cuisineType + "']";
            NodeList recipeNodes = (NodeList) xPath.evaluate(
                    xPathExpression,
                    xmlDocument,
                    XPathConstants.NODESET
            );

            for (int i = 0; i < recipeNodes.getLength(); i++) {
                Element recipeElement = (Element) recipeNodes.item(i);
                Recipe recipe = parseRecipeElement(recipeElement);
                if (recipe != null) {
                    filtered.add(recipe);
                }
            }
        } catch (XPathExpressionException e) {
            System.err.println(" XPath error in getRecipesByCuisineType: " + e.getMessage());
        }
        return filtered;
    }


    public List<Recipe> getRecipesBySkillAndCuisine(String skillLevel, String cuisineType) {
        List<Recipe> filtered = new ArrayList<>();
        try {
            String xPathExpression = "//recipe[difficultyLevels/difficultyLevel='" + skillLevel +
                    "' and cuisineTypes/cuisineType='" + cuisineType + "']";
            NodeList recipeNodes = (NodeList) xPath.evaluate(
                    xPathExpression,
                    xmlDocument,
                    XPathConstants.NODESET
            );

            for (int i = 0; i < recipeNodes.getLength(); i++) {
                Element recipeElement = (Element) recipeNodes.item(i);
                Recipe recipe = parseRecipeElement(recipeElement);
                if (recipe != null) {
                    filtered.add(recipe);
                }
            }
        } catch (XPathExpressionException e) {
            System.err.println(" XPath error in getRecipesBySkillAndCuisine: " + e.getMessage());
        }
        return filtered;
    }


    private Recipe parseRecipeElement(Element recipeElement) {
        try {
            String id = recipeElement.getAttribute("id");
            String title = recipeElement.getElementsByTagName("title").item(0).getTextContent();
            String image = recipeElement.getElementsByTagName("image").item(0) != null
                    ? recipeElement.getElementsByTagName("image").item(0).getTextContent()
                    : null;

            List<String> cuisineTypes = new ArrayList<>();
            NodeList cuisineNodes = recipeElement.getElementsByTagName("cuisineType");
            for (int j = 0; j < cuisineNodes.getLength(); j++) {
                cuisineTypes.add(cuisineNodes.item(j).getTextContent());
            }

            List<String> difficultyLevels = new ArrayList<>();
            NodeList difficultyNodes = recipeElement.getElementsByTagName("difficultyLevel");
            for (int j = 0; j < difficultyNodes.getLength(); j++) {
                difficultyLevels.add(difficultyNodes.item(j).getTextContent());
            }

            return new Recipe(id, title, image, cuisineTypes, difficultyLevels);
        } catch (Exception e) {
            System.err.println(" Error parsing recipe element: " + e.getMessage());
            return null;
        }
    }


    public void cleanDuplicateDifficulties() {
        try {
            System.out.println(" Cleaning duplicate difficulty levels from recipes...");
            NodeList recipeNodes = xmlDocument.getElementsByTagName("recipe");
            int cleaned = 0;
            
            for (int i = 0; i < recipeNodes.getLength(); i++) {
                Element recipeElement = (Element) recipeNodes.item(i);
                Element difficultyContainer = (Element) recipeElement.getElementsByTagName("difficultyLevels").item(0);
                
                if (difficultyContainer == null) continue;
                
                java.util.Set<String> seenDifficulties = new java.util.HashSet<>();
                NodeList difficultyNodes = difficultyContainer.getElementsByTagName("difficultyLevel");
                java.util.List<org.w3c.dom.Node> nodesToRemove = new java.util.ArrayList<>();
                
                for (int j = 0; j < difficultyNodes.getLength(); j++) {
                    Element diffElement = (Element) difficultyNodes.item(j);
                    String difficulty = diffElement.getTextContent();
                    
                    if (seenDifficulties.contains(difficulty)) {
                        nodesToRemove.add(diffElement);
                        cleaned++;
                    } else {
                        seenDifficulties.add(difficulty);
                    }
                }
                
                // Remove duplicates
                for (org.w3c.dom.Node node : nodesToRemove) {
                    difficultyContainer.removeChild(node);
                }
            }
            
            if (cleaned > 0) {
                System.out.println(" Cleaned " + cleaned + " duplicate difficulty entries");
                saveRecipesToXML(xmlDocument);
            } else {
                System.out.println(" No duplicates found");
            }
        } catch (Exception e) {
            System.err.println(" Error cleaning duplicates: " + e.getMessage());
        }
    }
}
