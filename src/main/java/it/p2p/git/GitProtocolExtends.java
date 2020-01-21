package it.p2p.git;

import java.io.File;
import java.util.List;

import it.p2p.git.entity.Commit;
import it.p2p.git.entity.Repository;

public interface GitProtocolExtends extends GitProtocol {
	
	public boolean cloneRepository(String _repo_name, File _directory);
	
	public boolean deleteRepository(String _repo_name);
	
	public boolean configAuthor(String author);
	
	public List<Repository> showLocalRepository();
	
	public File getRepositoryDirectory(String _repo_name);
	
	public boolean existRepository(String _repo_name);
	
	public List<Commit> showLocalHistory(String _repo_name);
	
	public List<Commit> showRemoteHistory(String _repo_name);

}