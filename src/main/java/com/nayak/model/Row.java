package com.nayak.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class Row {
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