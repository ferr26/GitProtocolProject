package it.p2p.git.impl;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.p2p.git.GitProtocolExtends;
import it.p2p.git.entity.Commit;
import it.p2p.git.entity.FileIndex;
import it.p2p.git.entity.Repository;
import it.p2p.git.entity.FileIndex.Operation;
import it.p2p.git.utils.ManageFile;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;


public class GitProtocolImpl implements GitProtocolExtends {
	final private Peer peer;
	final private PeerDHT _dht;
	final private int DEFAULT_MASTER_PORT=4000;
	final private Map<String,Repository> localReposity;
	private List<FileIndex> fileCommit;
	private String author;

	public GitProtocolImpl( int _id, String _master_peer) throws Exception
	{
		peer= new PeerBuilder(Number160.createHash(_id)).ports(DEFAULT_MASTER_PORT+_id).start();
		_dht = new PeerBuilderDHT(peer).start();	
		localReposity = new HashMap<String, Repository>();
		author = _id+"@"+_master_peer;
		fileCommit = new ArrayList<>();
		FutureBootstrap fb = peer.bootstrap().inetAddress(InetAddress.getByName(_master_peer)).ports(DEFAULT_MASTER_PORT).start();
		fb.awaitUninterruptibly();
		if(fb.isSuccess()) {
			peer.discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
		}else {
			throw new Exception("Error in master peer bootstrap.");
		}
	}
	/**
	 * Config Author of Repository
	 * @param author a String, the name of the Author.
	 * @return true if it is correctly configured, false otherwise.
	 */
	public boolean configAuthor(String author) {
		if (author==null || author.isEmpty()) {
			return false;
		}
		this.author=author;
		return true;
	}
	/**
	 * Creates new repository in a directory
	 * @param _repo_name a String, the name of the repository.
	 * @param _directory a File, the directory where create the repository.
	 * @return true if it is correctly created, false otherwise.
	 */
	public boolean createRepository(String _repo_name, File _directory) {
		try {
			String repoNameUpperCase = _repo_name.toUpperCase();
			if (localReposity.containsKey(repoNameUpperCase)) {
				return false;
			}
			if(existRepository(repoNameUpperCase)) {
				return false;
			}
			FutureGet futureGet = _dht.get(Number160.createHash(repoNameUpperCase)).start();
			futureGet.awaitUninterruptibly();
			if(futureGet.isSuccess() && futureGet.isEmpty()) {
				Repository newRepository = new Repository(repoNameUpperCase, _directory, author); 
				localReposity.put(repoNameUpperCase, newRepository);
				_dht.put(Number160.createHash(repoNameUpperCase)).data(new Data(newRepository)).start().awaitUninterruptibly();
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Clone a repository in a directory on Network if exist
	 * @param _repo_name a String, the name of the repository.
	 * @param _directory a File, the directory where create the repository.
	 * @return true if it is correctly cloned, false otherwise.
	 */
	public boolean cloneRepository(String _repo_name, File _directory) {
		try {
			String repoNameUpperCase = _repo_name.toUpperCase();
			if (localReposity.containsKey(repoNameUpperCase)) {
				return false;
			}

			FutureGet futureGet = _dht.get(Number160.createHash(repoNameUpperCase)).start();
			futureGet.awaitUninterruptibly();
			if(futureGet.isSuccess() && !futureGet.isEmpty()) {
				Repository remoteRepository =  (Repository) futureGet.dataMap().values().iterator().next().object();
				remoteRepository.setDirectory(_directory);
				localReposity.put(repoNameUpperCase, remoteRepository);

				Map<String, File> files = remoteRepository.getFiles();
				files.forEach((nomefile,file) -> {
					File clone = new File(_directory+"/"+nomefile);
					try {
						ManageFile.cloneFiles(file, clone);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Delete a repository on Network and repository local
	 * @param _repo_name a String, the name of the repository.
	 * @return true if it is correctly deleted, false otherwise.
	 */
	public boolean deleteRepository(String _repo_name) {
		try {
			String repoNameUpperCase = _repo_name.toUpperCase();
		
			FutureGet futureGet = _dht.get(Number160.createHash(repoNameUpperCase)).start();
			futureGet.awaitUninterruptibly();
			if (futureGet.isSuccess() && !futureGet.isEmpty()) {
				if (localReposity.containsKey(repoNameUpperCase)) {
					localReposity.remove(repoNameUpperCase);
				}
				_dht.remove(Number160.createHash(repoNameUpperCase)).start().awaitUninterruptibly();
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;

		}
	}
	/**
	 * Adds a list of File to the given local repository. 
	 * @param _repo_name a String, the name of the repository.
	 * @param files a list of Files to be added to the repository.
	 * @return true if it is correctly added, false otherwise.
	 */
	public boolean addFilesToRepository(String _repo_name, java.util.List<File> files) {
		boolean addFile = false;
		try {
			String repoNameUpperCase = _repo_name.toUpperCase();
			if(localReposity.containsKey(repoNameUpperCase)) {
				Repository rep = localReposity.get(repoNameUpperCase);
				Map<String,File> fileRepository = rep.getFiles();
				Set<String> setFiles = new HashSet<>();
				for (File file : files) {
					if(!fileRepository.containsKey(file.getName())) {
						rep.addFiles(file);
						FileIndex fileIndex = new FileIndex(file, Operation.ADD);
						fileCommit.add(fileIndex);
						addFile = true;
					}
					setFiles.add(file.getName());
				}
				Set<String> fileRemoved = new HashSet<>(fileRepository.keySet());
				fileRemoved.removeAll(setFiles);
				for (String filenameRemoved : fileRemoved) {
					if(fileRepository.containsKey(filenameRemoved)) {
						File fileDelete = fileRepository.get(filenameRemoved);
						rep.removeFiles(filenameRemoved);
						FileIndex fileIndex = new FileIndex(fileDelete, Operation.REMOVE);
						fileCommit.add(fileIndex);
						addFile = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addFile;
	}
	/**
	 * Get a file in a Repository Local.
	 * @param _repo_name _repo_name a String, the name of the repository.
	 * @return a File if Repository exist, null otherwise.
	 */
	public File getRepositoryDirectory(String _repo_name) {
		String repoNameUpperCase = _repo_name.toUpperCase();
		if(localReposity.containsKey(repoNameUpperCase)) {
			Repository rep = localReposity.get(repoNameUpperCase);
			File folder = rep.getDirectory();
			return folder;
		}
		return null;
	}
	/**
	 * Apply the changing to the files in  the local repository.
	 * @param _repo_name a String, the name of the repository.
	 * @param _message a String, the message for this commit.
	 * @return true if it is correctly committed, false otherwise.
	 */
	public boolean commit(String _repo_name, String _message) {
		Commit commit = new Commit(author, _message, fileCommit);
		fileCommit = new ArrayList<>();
		
		String repoNameUpperCase = _repo_name.toUpperCase();
		if(!existRepository(repoNameUpperCase)) {
			return false;
		}
		if(localReposity.containsKey(repoNameUpperCase)) {
			Repository repo = localReposity.get(repoNameUpperCase);
			repo.getListCommit().add(commit);
			return true;
		}
		return false;	
	}
	
	/**
	 * Push all commits on the Network. If the status of the remote repository is changed, 
	 * the push fails, asking for a pull.
	 * @param _repo_name _repo_name a String, the name of the repository.
	 * @return a String, operation message.
	 */
	public String push(String _repo_name) {
		try {
			String repoNameUpperCase = _repo_name.toUpperCase();
			if(localReposity.containsKey(repoNameUpperCase)) {
				Repository localRepo = localReposity.get(repoNameUpperCase);
				FutureGet futureGet = _dht.get(Number160.createHash(repoNameUpperCase)).start();
				futureGet.awaitUninterruptibly();
				if (futureGet.isSuccess() && !futureGet.isEmpty()) {
					Repository remoteRepository =  (Repository) futureGet.dataMap().values().iterator().next().object();	
					
					List<Commit> listUp= new ArrayList<>();
					for (int i = remoteRepository.getListCommit().size() -1;i>=0;i--) {
						Commit commit = remoteRepository.getListCommit().get(i);
						if (localRepo.getListCommit().contains(commit)) {
							break;
						}
						listUp.add(commit);
					}
					if (!listUp.isEmpty()) {
						return "Not possibile Push, Make Pull";
					}
					
					_dht.put(Number160.createHash(repoNameUpperCase)).data(new Data(localRepo)).start().awaitUninterruptibly();
					return "Push Successfully";
				} else {
					return "Remote repository Not Exists";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Error Push";
	}
	/**
	 * Pull the files from the Network. If there is a conflict, the system duplicates 
	 * the files and the user should manually fix the conflict.
	 * @param _repo_name _repo_name a String, the name of the repository.
	 * @return a String, operation message.
	 */
	public String pull(String _repo_name) {
		try {
			String repoNameUpperCase = _repo_name.toUpperCase();
			if(localReposity.containsKey(repoNameUpperCase)) {
				Repository localRepo = localReposity.get(repoNameUpperCase);
				FutureGet futureGet = _dht.get(Number160.createHash(repoNameUpperCase)).start();
				futureGet.awaitUninterruptibly();
				if (futureGet.isSuccess() && !futureGet.isEmpty()) {
					Repository remoteRepository =  (Repository) futureGet.dataMap().values().iterator().next().object();	
					
					//prelevo lista di commit che sono in remoto ma non in locale
					List<Commit> listUp= new ArrayList<>();
					for (int i = remoteRepository.getListCommit().size() -1;i>=0;i--) {
						Commit commit = remoteRepository.getListCommit().get(i);
						if (localRepo.getListCommit().contains(commit)) {
							break;
						}
						listUp.add(commit);
					}
					if (listUp.isEmpty()) {
						return "Alredy Updated";
					}
					
					//ricavo tutti i commit locali che non sono in remoto
					List<Commit> listUpLocal= new ArrayList<>();
					for (int i = localRepo.getListCommit().size() -1;i>=0;i--) {
						Commit commit = localRepo.getListCommit().get(i);
						if (remoteRepository.getListCommit().contains(commit)) {
							break;
						}
						listUpLocal.add(commit);
					}
					if (!listUpLocal.isEmpty()) {
						return "Merge";
					}
					
					for (int i = listUp.size() - 1;i>=0;i--) {
						Commit commit = listUp.get(i);
						for (FileIndex file: commit.getListaFile()) {
							if (Operation.ADD.equals(file.getOperation())) {
								localRepo.addFiles(file.getFile());
								File clone = new File(localRepo.getDirectory()+"/"+file.getFile().getName());
								try {
									ManageFile.cloneFiles(file.getFile(), clone);
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else if (Operation.REMOVE.equals(file.getOperation())){
								localRepo.removeFiles(file.getFile().getName());
								File fileDeleted = new File(localRepo.getDirectory()+"/"+file.getFile().getName());
								try {
									ManageFile.deleteFiles(fileDeleted);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						localRepo.getListCommit().add(commit);
					}
					
					return "Pull Successfully";
				} else {
					return "Remote repository Not Exists";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Error Pull";
	}
	/**
	 * Clone a repository in a directory on Network if exist
	 * @param _repo_name a String, the name of the repository.
	 * @param _directory a File, the directory where create the repository.
	 * @return true if it is correctly cloned, false otherwise.
	 */
	public List<Repository> showLocalRepository() {
		List<Repository> repository = new ArrayList<>();
		for (String repositoryLocal: localReposity.keySet()){
			if (existRepository(repositoryLocal)) {
				repository.add(localReposity.get(repositoryLocal));
			}
		}
		return repository;
	}
	
	/**
	 * Show a file present in Remote Repository
	 * @param _repo_name a String, the name of the repository.
	 * @return true if file are present, null otherwise.
	 */
	public Map<String,File> showFileRepository(String _repo_name) {
		Map<String,File> file = new HashMap<String, File>();;
		String repoNameUpperCase = _repo_name.toUpperCase();
		try {
			
			FutureGet futureGet = _dht.get(Number160.createHash(repoNameUpperCase)).start();
			futureGet.awaitUninterruptibly();	
			if(futureGet.isSuccess()) {
				if(futureGet.isEmpty())
					return null;
				Repository rep = (Repository) futureGet.dataMap().values().iterator().next().object();
				file = rep.getFiles();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	
	/**
	 * Show if Repository on Network exists
	 * @param _repo_name a String, the name of the repository.
	 * @return true if repository exists, false otherwise.
	 */
	public boolean existRepository(String _repo_name) {
		String repoNameUpperCase = _repo_name.toUpperCase();
		FutureGet futureGet = _dht.get(Number160.createHash(repoNameUpperCase)).start();
		futureGet.awaitUninterruptibly();
		if(futureGet.isSuccess() && !futureGet.isEmpty()) {
			return true;
		}
		return false;
	}
	/**
	 * Show a local Commit without Push on the Network
	 * @param _repo_name a String, the name of the repository.
	 * @return List<Commit> if made a Commit, null otherwise.
	 */
	public List<Commit> showLocalHistory(String _repo_name){
		String repoNameUpperCase = _repo_name.toUpperCase();
		Repository repo = localReposity.get(repoNameUpperCase);
		if(repo != null ) {
			return repo.getListCommit();
		}	
		return null;
	}
	/**
	 * Show a Commit Pushed on the Network
	 * @param _repo_name a String, the name of the repository.
	 * @return List<Commit> if made a Commit on Network, null otherwise.
	 */
	public List<Commit> showRemoteHistory(String _repo_name){
		try {		
			String repoNameUpperCase = _repo_name.toUpperCase();
			if(!existRepository(repoNameUpperCase)) {
				return null;
			}
			List<Commit> commit = new ArrayList<Commit>();
			FutureGet futureGet = _dht.get(Number160.createHash(repoNameUpperCase)).start().awaitUninterruptibly();
			if(futureGet.isSuccess() && !futureGet.isEmpty()) {
				Repository rep = (Repository) futureGet.dataMap().values().iterator().next().object();
				commit = rep.getListCommit();
				return commit;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
