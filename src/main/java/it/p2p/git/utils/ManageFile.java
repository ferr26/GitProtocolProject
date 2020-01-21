package it.p2p.git.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ManageFile {
	
	private static String PREFIX = "./files/";
	
	public static File createDirectory(int id, String nameDirectory) {
		String nameDir = PREFIX + id + "/" + nameDirectory;
		File dir = new File(nameDir);

		if(!dir.getParentFile().exists())
			dir.getParentFile().mkdir();

		if(!dir.exists())
			dir.mkdir();
		return dir;
	}
	
	public static List<String> generateFile(File fileDirectory, int nFile) throws IOException {
		List <String> listFile = new ArrayList<>();
		String nameDir = fileDirectory.getAbsolutePath();
		System.out.println("namedir" + nameDir);
		for(int i=0; i<nFile; i++) {
			ManageData manageData = new ManageData();
			String date = manageData.getData();
			String time = manageData.getOrario();
			String nameFile = date+ "_" +time+ "_" +"File"+ "_" + i +".txt";
			String nameFileDir = nameDir + "/" +  nameFile;

			File newFile = new File(nameFileDir);
			newFile.createNewFile();
			FileWriter fw = new FileWriter(newFile);

			listFile.add(nameFile);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("This file was created by numeber "+(i+1)+" on " + date + time );
			bw.flush();
			bw.close();
		}
		return listFile;
	}

	public static List<File> listFiles(final File directory) {
		List<File> files = new ArrayList<>();
		for (final File fileEntry : directory.listFiles()) {
			if (fileEntry.isDirectory()) {
				List<File> listFile = listFiles(fileEntry);
				files.addAll(listFile);
			} else {
				files.add(fileEntry);
			}
		}
		return files;
	}

	public static void cloneFiles(File original, File clone) throws IOException {

		InputStream initialStream = new FileInputStream(original);

		Files.copy(initialStream, clone.toPath(), StandardCopyOption.REPLACE_EXISTING);

		initialStream.close();
	}
	
	public static void deleteFiles(File file) throws IOException {

		if (file.exists()) {
			Files.delete(file.toPath());
		}
	}
}
