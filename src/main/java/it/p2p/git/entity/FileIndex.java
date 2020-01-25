package it.p2p.git.entity;
import java.io.File;
import java.io.Serializable;

public class FileIndex implements Serializable {

	private File file;
	private String encodeBase64;
	private Operation operation;
	
	public FileIndex(File file, String encodeBase64, Operation operation) {
		this.file = file;
		this.operation = operation;
		this.encodeBase64 = encodeBase64;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public String getEncodeBase64() {
		return encodeBase64;
	}
	
	public void setEncodeBase64(String encodeBase64) {
		this.encodeBase64 = encodeBase64;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public enum Operation {
		ADD,
		REMOVE
	}
	
	@Override
	public String toString() {
		return "\n -> " + operation + " | " + file.getAbsolutePath() + "\n";
	}
}
