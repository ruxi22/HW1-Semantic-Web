package com.recipes.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Service
public class XmlValidationService {

    private Schema recipesSchema;
    private Schema usersSchema;
    private List<String> validationErrors;

    public XmlValidationService() {
        initializeSchemas();
    }


    private void initializeSchemas() {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            InputStream recipesXsdStream = getClass().getClassLoader()
                    .getResourceAsStream("recipes.xsd");
            if (recipesXsdStream != null) {
                StreamSource recipesXsdSource = new StreamSource(recipesXsdStream);
                this.recipesSchema = schemaFactory.newSchema(recipesXsdSource);
                System.out.println(" Recipes XSD schema loaded successfully");
            } else {
                System.err.println(" recipes.xsd not found in resources");
            }
            
            // Load users.xsd
            InputStream usersXsdStream = getClass().getClassLoader()
                    .getResourceAsStream("users.xsd");
            if (usersXsdStream != null) {
                StreamSource usersXsdSource = new StreamSource(usersXsdStream);
                this.usersSchema = schemaFactory.newSchema(usersXsdSource);
                System.out.println(" Users XSD schema loaded successfully");
            } else {
                System.err.println(" users.xsd not found in resources");
            }
        } catch (SAXException e) {
            System.err.println(" Error loading XSD schemas: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public boolean validateRecipesXml(Document document) {
        if (recipesSchema == null) {
            System.err.println(" Recipes schema not loaded, skipping validation");
            return true;
        }

        try {
            this.validationErrors = new ArrayList<>();
            
            Validator validator = recipesSchema.newValidator();

            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) {
                    String msg = "WARNING: " + exception.getMessage() + 
                               " (line " + exception.getLineNumber() + ")";
                    validationErrors.add(msg);
                    System.out.println("fail  " + msg);
                }

                @Override
                public void error(SAXParseException exception) {
                    String msg = "ERROR: " + exception.getMessage() + 
                               " (line " + exception.getLineNumber() + ")";
                    validationErrors.add(msg);
                    System.err.println("fail " + msg);
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    String msg = "FATAL: " + exception.getMessage() + 
                               " (line " + exception.getLineNumber() + ")";
                    validationErrors.add(msg);
                    System.err.println("fail " + msg);
                    throw exception;
                }
            });

            Source source = new DOMSource(document);
            validator.validate(source);

            if (validationErrors.isEmpty()) {
                System.out.println(" recipes.xml is valid according to recipes.xsd");
                return true;
            } else {
                System.err.println(" recipes.xml validation failed with " + validationErrors.size() + " error(s)");
                return false;
            }
        } catch (SAXException | IOException e) {
            System.err.println(" Exception during recipes.xml validation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateUsersXml(Document document) {
        if (usersSchema == null) {
            System.err.println("  Users schema not loaded, skipping validation");
            return true;
        }

        try {
            this.validationErrors = new ArrayList<>();
            
            Validator validator = usersSchema.newValidator();

            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) {
                    String msg = "WARNING: " + exception.getMessage() + 
                               " (line " + exception.getLineNumber() + ")";
                    validationErrors.add(msg);
                    System.out.println("  " + msg);
                }

                @Override
                public void error(SAXParseException exception) {
                    String msg = "ERROR: " + exception.getMessage() + 
                               " (line " + exception.getLineNumber() + ")";
                    validationErrors.add(msg);
                    System.err.println(" " + msg);
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    String msg = "FATAL: " + exception.getMessage() + 
                               " (line " + exception.getLineNumber() + ")";
                    validationErrors.add(msg);
                    System.err.println(" " + msg);
                    throw exception;
                }
            });

            Source source = new DOMSource(document);
            validator.validate(source);

            if (validationErrors.isEmpty()) {
                System.out.println(" users.xml is valid according to users.xsd");
                return true;
            } else {
                System.err.println(" users.xml validation failed with " + validationErrors.size() + " error(s)");
                return false;
            }
        } catch (SAXException | IOException e) {
            System.err.println(" Exception during users.xml validation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public boolean validateRecipesXmlFile(String xmlFilePath) {
        if (recipesSchema == null) {
            System.err.println("  Recipes schema not loaded, skipping validation");
            return true;
        }

        try {
            this.validationErrors = new ArrayList<>();
            
            Validator validator = recipesSchema.newValidator();
            validator.setErrorHandler(new ValidationErrorHandler());
            
            File xmlFile = new File(xmlFilePath);
            StreamSource source = new StreamSource(xmlFile);
            validator.validate(source);

            System.out.println(" recipes.xml file is valid according to recipes.xsd");
            return true;
        } catch (SAXException | IOException e) {
            System.err.println(" Exception during recipes.xml file validation: " + e.getMessage());
            return false;
        }
    }


    public boolean validateUsersXmlFile(String xmlFilePath) {
        if (usersSchema == null) {
            System.err.println("  Users schema not loaded, skipping validation");
            return true;
        }

        try {
            this.validationErrors = new ArrayList<>();
            
            Validator validator = usersSchema.newValidator();
            validator.setErrorHandler(new ValidationErrorHandler());
            
            File xmlFile = new File(xmlFilePath);
            StreamSource source = new StreamSource(xmlFile);
            validator.validate(source);

            System.out.println(" users.xml file is valid according to users.xsd");
            return true;
        } catch (SAXException | IOException e) {
            System.err.println(" Exception during users.xml file validation: " + e.getMessage());
            return false;
        }
    }


    public List<String> getValidationErrors() {
        return validationErrors != null ? validationErrors : new ArrayList<>();
    }


    private static class ValidationErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException exception) {
            System.out.println("  Validation WARNING: " + exception.getMessage() +
                             " (line " + exception.getLineNumber() + ")");
        }

        @Override
        public void error(SAXParseException exception) {
            System.err.println(" Validation ERROR: " + exception.getMessage() +
                             " (line " + exception.getLineNumber() + ")");
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            System.err.println(" Validation FATAL: " + exception.getMessage() +
                             " (line " + exception.getLineNumber() + ")");
            throw exception;
        }
    }
}

