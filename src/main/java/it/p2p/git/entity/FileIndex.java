package it.p2p.git.entity;
import java.io.File;
import java.io.Serializable;

public class FileIndex implements Serializable {

	private File file;
	private Operation operation;
	
	public FileIndex(File file, Operation operation) {
		this.file = file;
		this.operation = operation;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public enum Operation {
		ADD,
		REMOVE,
		EDIT
	}
	
	@Override
	public String toString() {
		return "\n -> " + operation + " | " + file.getAbsolutePath() + "\n";
	}
}
