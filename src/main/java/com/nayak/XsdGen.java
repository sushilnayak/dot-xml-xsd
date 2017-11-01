package com.nayak;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.inst2xsd.Inst2Xsd;
import org.apache.xmlbeans.impl.inst2xsd.Inst2XsdOptions;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;

enum XMLSchemaDesign{
	VENETIAN_BLIND,RUSSIAN_DOLL,SALAMI_SLICE;
}

public class XsdGen {
	public static void main(String[] args) {
		try {
			XsdGen xmlBeans = new XsdGen();
			SchemaDocument schemaDocument = xmlBeans.generateSchema(new File("src\\main\\resources\\dummy.xml"), XMLSchemaDesign.VENETIAN_BLIND);

			StringWriter writer = new StringWriter();
			schemaDocument.save(writer, new XmlOptions().setSavePrettyPrint());
			writer.close();

			String xmlText = writer.toString();
			System.out.println(xmlText);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

 
	public SchemaDocument generateSchema(File inputFile, XMLSchemaDesign design) throws XmlException, IOException {
		// Only 1 instance is required for now
		XmlObject[] xmlInstances = new XmlObject[1];
		xmlInstances[0] = XmlObject.Factory.parse(inputFile);

		return inst2xsd(xmlInstances, design);
	} 


	public SchemaDocument generateSchema(String input, XMLSchemaDesign design) throws XmlException, IOException {
		// Only 1 instance is required for now
		XmlObject[] xmlInstances = new XmlObject[1];
		xmlInstances[0] = XmlObject.Factory.parse(input);

		return inst2xsd(xmlInstances, design);
	}

	private SchemaDocument inst2xsd(XmlObject[] xmlInstances, XMLSchemaDesign design) throws IOException {
		Inst2XsdOptions inst2XsdOptions = new Inst2XsdOptions();
		if (design == null || design == XMLSchemaDesign.VENETIAN_BLIND) {
			inst2XsdOptions.setDesign(Inst2XsdOptions.DESIGN_VENETIAN_BLIND);
		} else if (design == XMLSchemaDesign.RUSSIAN_DOLL) {
			inst2XsdOptions.setDesign(Inst2XsdOptions.DESIGN_RUSSIAN_DOLL);
		} else if (design == XMLSchemaDesign.SALAMI_SLICE) {
			inst2XsdOptions.setDesign(Inst2XsdOptions.DESIGN_SALAMI_SLICE);
		}

		SchemaDocument[] schemaDocuments = Inst2Xsd.inst2xsd(xmlInstances, inst2XsdOptions);
		if (schemaDocuments != null && schemaDocuments.length > 0) {
			return schemaDocuments[0];
		}

		return null;
	}

	public static String formatXSD(String xsdString) {
		try {

			Source xmlInput = new StreamSource(new StringReader(xsdString));
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", 2);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(xmlInput, xmlOutput);

			return xmlOutput.getWriter().toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}