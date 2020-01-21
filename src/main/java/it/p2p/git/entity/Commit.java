package it.p2p.git.entity;
import java.io.File;
import java.io.Serializable;
import java.rmi.server.UID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;



public class Commit implements Serializable {

	private String author;
	private String message;
	private LocalDateTime data;
	private List<FileIndex> listaFile;
	private String id;
	
	public Commit(String author, String message, List<FileIndex> listaFile) {
		super();
		this.author = author;
		this.message = message;
		this.data = LocalDateTime.now();
		this.id=UUID.randomUUID().toString();
		this.listaFile = listaFile;
	}
	
	public String getId() {
		return id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public List<FileIndex> getListaFile() {
		return listaFile;
	}

	public void setListaFile(List<FileIndex> listaFile) {
		this.listaFile = listaFile;
	}

	@Override
	public String toString() {
		return  id + " | " + data + " | "+ author+" | " + message + "\n";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((listaFile == null) ? 0 : listaFile.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Commit other = (Commit) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
