package it.p2p.git.entity;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Repository implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private File directory;
	private String lastAuthor;
	private Map<String,File> files;
	private LocalDateTime lastModification;
	private List<Commit> listCommit;
	
	public Repository(String name, File directory, String author) {
		this.name = name;
		this.directory = directory;
		this.lastAuthor = author;
		this.lastModification = LocalDateTime.now();
		this.files = new HashMap<String, File>();
		this.listCommit = new ArrayList<>();
	}
	
	public Map<String, File> getFiles() {
		return files;
	}

	public void addFiles(File file) {
		files.put(file.getName(), file);
	}
	
	public void removeFiles(String filename) {
		files.remove(filename);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File getDirectory() {
		return directory;
	}
	
	public void setDirectory(File directory) {
		this.directory = directory;
	}
	
	public String getAuthor() {
		return lastAuthor;
	}
	
	public void setAuthor(String author) {
		this.lastAuthor = author;
	}
	
	public List<Commit> getListCommit() {
		return listCommit;
	}
		
	@Override
	public String toString() {
		return "Repository [name=" + name + ", directory=" + directory + ", lastAuthor=" + lastAuthor + ", files="
				+ files + ", lastModification=" + lastModification + ", listCommit=" + listCommit + "]";
	}
}
