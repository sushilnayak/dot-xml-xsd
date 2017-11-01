package com.nayak;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import com.nayak.model.Row;
import com.nayak.utilities.XmlUtility;

public class XmlApp {
	
	public static final Logger log=LoggerFactory.getLogger(XmlApp.class);

	public static List<Row> loadDotFile(String dotFile) throws IOException {
		List<Row> dotData = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader(new File(dotFile)));

		String line;

		while ((line = br.readLine()) != null) {
			line=cleanUp(line);
			dotData.add(new Row(line.split("\\|")[0], line.split("\\|")[1]));
		}

		br.close();
		
		return dotData;
	}
	
	public static String cleanUp(String inString) {
		
		return inString.replaceAll("[\\-\\(]", "_")
				       .replaceAll("\\)", "")
				       .replaceAll("[\\x00-\\x1F]", "");
	}

	public static String fromDotToXML(List<Row> dotModelList) {
		
		Stack<String> openTag = new Stack<>();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < dotModelList.size(); i++) {
			Row row = dotModelList.get(i);

			List<String> dotList = row.getListDotData();

			for (int j = 0; j < dotList.size(); j++) {

				String s = dotList.get(j);

				if (openTag.search(s.concat("|").concat(String.valueOf(j))) < 0) {

					try {
						while (Integer.parseInt(openTag.peek().split("\\|")[1]) >= j) {
							log.debug("</" + openTag.peek().split("\\|")[0] + ">");
							// TODO: Need to work on logic to prefix . dot notation which have number .. it's not possible for XML to have number starting tag
							sb.append("</" + openTag.pop().split("\\|")[0].split("\\[")[0] + ">");
						}

					} catch (Exception e) {
						// TODO: first row would have exception otherwise this is cool!
					}

					if (dotList.size() - 1 == j) {
//						System.out.print("<" + s + ">");
						log.debug("<" + s + ">");
						sb.append("<" + s.split("\\[")[0] + ">");
					} else {
						log.debug("<" + s + ">");
						sb.append("<" + s.split("\\[")[0] + ">");
					}
					
					openTag.add(s.concat("|").concat(String.valueOf(j)));
				}

				if (dotList.size() - 1 == j) {
//					System.out.print("**" + row.getDataType() + "**");
					log.debug("**" + row.getDataType() + "**");
					log.debug("</" + openTag.peek().split("\\|")[0] + ">");

					sb.append("**" + row.getDataType() + "**");
					sb.append("</" + openTag.pop().split("\\|")[0].split("\\[")[0] + ">");
				}

			}

		}

		while (!openTag.isEmpty()) {
			log.debug("</" + openTag.peek().split("\\|")[0] + ">");
			sb.append("</" + openTag.pop().split("\\|")[0].split("\\[")[0] + ">");

		}

		return sb.toString();

	}

	public static void main(String[] args) throws IOException {

		List<Row> dotModelList = loadDotFile("src/main/resources/dotfile.txt");

		String unformattedXml = fromDotToXML(dotModelList);

		System.out.println(XmlUtility.formatXML(unformattedXml));

	}
	
}


