package com.nayak;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
							sb.append("</" + openTag.pop().split("\\|")[0].split("\\[")[0] + ">");
						}
						// if (Integer.parseInt(openTag.peek().split("\\|")[1]) >= j) {
						// log.debug("</" + openTag.pop().split("\\|")[0] + ">");
						// }
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

		System.out.println(unformattedXml);

	}
}

@Data
@ToString
@NoArgsConstructor
class Row {
	String dataType;
	String dotData;
	List<String> listDotData;
	Integer count;

	public Row(String dataType, String dotData) {
		super();
		this.dataType = dataType;
		this.dotData = dotData;

		this.listDotData = new ArrayList<>();
		this.listDotData.addAll(Arrays.asList(this.dotData.split("\\.")));
		this.count = this.listDotData.size();

	}

}
