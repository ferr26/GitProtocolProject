package it.p2p.git.utils;

import java.text.SimpleDateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ManageData {


	public String getData() {
		SimpleDateFormat sdf = new SimpleDateFormat(); 
		sdf.applyPattern("yyyyMMdd");  
		String dateStr = sdf.format(new Date()); 

		return dateStr;
	}


	public String getOrario() {
		SimpleDateFormat sdf = new SimpleDateFormat(); 
		sdf.applyPattern("HH.mm.ss");
		String timeStr = sdf.format(new Date()); 

		return timeStr;
	}
}
