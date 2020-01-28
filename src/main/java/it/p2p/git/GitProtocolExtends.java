package it.p2p.git;

import java.io.File;
import java.util.List;
import java.util.Map;

import it.p2p.git.entity.Commit;
import it.p2p.git.entity.FileIndex;
import it.p2p.git.entity.Repository;

public interface GitProtocolExtends extends GitProtocol {

	/**
	 * Clone a repository in a directory on Network if exist
	 * @param _repo_name a String, the name of the repository.
	 * @param _directory a File, the directory where create the repository.
	 * @return true if it is correctly cloned, false otherwise.
	 */
	public boolean cloneRepository(String _repo_name, File _directory);
	
	/**
	 * Delete a repository on Network and repository local
	 * @param _repo_name a String, the name of the repository.
	 * @return true if it is correctly deleted, false otherwise.
	 */
	public boolean deleteRepository(String _repo_name);

	/**
	 * Config Author of Repository
	 * @param author a String, the name of the Author.
	 * @return true if it is correctly configured, false otherwise.
	 */
	public boolean configAuthor(String author);


	/**
	 * Get a file in a Repository Local.
	 * @param _repo_name _repo_name a String, the name of the repository.
	 * @return a File if Repository exist, null otherwise.
	 */
	public List<Repository> showLocalRepository();

	/**
	 * Get a Repository Local
	 * @param _repo_name a String, the name of the repository.
	 * @return File if repository exists, null otherwise.
	 */
	public File getRepositoryDirectory(String _repo_name);

	/**
	 * Show if Repository on Network exists
	 * @param _repo_name a String, the name of the repository.
	 * @return true if repository exists, false otherwise.
	 */
	public boolean existRepository(String _repo_name);
	
	/**
	 * Show a local Commit without Push on the Network
	 * @param _repo_name a String, the name of the repository.
	 * @return List<Commit> if made a Commit, null otherwise.
	 */
	public List<Commit> showLocalHistory(String _repo_name);
	
	/**
	 * Show a Commit Pushed on the Network
	 * @param _repo_name a String, the name of the repository.
	 * @return List<Commit> if made a Commit on Network, null otherwise.
	 */
	public List<Commit> showRemoteHistory(String _repo_name);
	
	/**
	 * Show a file present in Remote Repository
	 * @param _repo_name a String, the name of the repository.
	 * @return true if file are present, null otherwise.
	 */
	public Map<String,FileIndex> showFileRepository(String _repo_name);

}