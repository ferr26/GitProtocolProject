package it.p2p.git.menu;

import java.io.File;
import java.util.List;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import it.p2p.git.entity.Commit;
import it.p2p.git.entity.Repository;
import it.p2p.git.impl.GitProtocolImpl;
import it.p2p.git.utils.ManageFile;
/**
 * docker build --no-cache -t test  .
 * docker run -i -e MASTERIP="127.0.0.1" -e ID=0 test
 * use -i for interactive mode
 * use -e to set the environment variables
 *
 */
public class GitProtocolMenu {

	@Option(name="-m", aliases="--masterip", usage="the master peer ip address", required=true)
	private static String master;

	@Option(name="-id", aliases="--identifierpeer", usage="the unique identifier for this peer", required=true)
	private static int id; 

	private final static int MENU_GENERAFILE = 1; 
	private final static int MENU_CREATEREPO = 2; 
	private final static int MENU_CLONEREPO  = 3; 
	private final static int MENU_DELETEREPO = 4; 
	private final static int MENU_CONFIG     = 0; 
	private final static int MENU_SHOWLOCAL  = 5; 
	private final static int MENU_ADD_FILE   = 6; 
	private final static int MENU_COMMIT     = 7; 
	private final static int MENU_PUSH       = 8; 
	private final static int MENU_PULL       = 9; 
	private final static int LOCAL_HISTORY   = 10; 
	private final static int REMOTE_HISTORY  = 11; 

	private final static int MENU_EXIT  	 = 12; 

	public static void main(String[] args) throws Exception {


		GitProtocolMenu example = new GitProtocolMenu();
		final CmdLineParser parser = new CmdLineParser(example);  
		String repositoryName,repositoryDirectory;

		try  
		{  
			parser.parseArgument(args);  
			TextIO textIO = TextIoFactory.getTextIO();
			TextTerminal<?> terminal = textIO.getTextTerminal();
			GitProtocolImpl peer = 
					new GitProtocolImpl(id, master);

			while(true) {
				terminal.printf("\n********************************************");

				terminal.printf("\n*Staring peer id:%d on master node: %s*\n",
						id, master);
				terminal.printf("* 			                          *");

				printMenu(terminal);

				int option = textIO.newIntInputReader()
						.withMaxVal(12)
						.withMinVal(0)
						.read("# Option");
				switch (option) {
				case MENU_GENERAFILE:
					try {
						terminal.printf("\nGenerate File \n");
						repositoryDirectory = textIO.newStringInputReader().read("Enter name directory:");
						int nFile = textIO.newIntInputReader().read("How many files do you want to generate?:");
						
						File fileDir = ManageFile.createDirectory(id, repositoryDirectory);
						List<String> listFile = ManageFile.generateFile( fileDir, nFile);
						for (String file : listFile) {
							terminal.printf("\n -> "+file+" \n");
						}
					} catch (Exception e) {
						e.getStackTrace();
						terminal.printf("\n -> Error in Generate File \n");
					}
					break;
				case MENU_CREATEREPO:
					try {
						terminal.printf("\nCreate Repository \n");
						repositoryName = textIO.newStringInputReader().read("Repository Name:");
						repositoryDirectory = textIO.newStringInputReader().read("Directory Name:");

						File directoryFile = ManageFile.createDirectory(id, repositoryDirectory);

						if(peer.createRepository(repositoryName, directoryFile))
							terminal.printf("\n -> Repository %s successfully created \n",repositoryName);
						else
							terminal.printf("\n -> Error Repository Not Created \n");
					} catch (Exception e) {
						e.getStackTrace();
						terminal.printf("\n -> Error in Create Repository \n");
					}
					break;
				case MENU_CLONEREPO:
					try {
						terminal.printf("\nDownload Repository \n");
						repositoryName = textIO.newStringInputReader().read("Repository Name:");
						repositoryDirectory = textIO.newStringInputReader().read("Directory Name:");

						File directoryFile = ManageFile.createDirectory(id, repositoryDirectory);

						if(peer.cloneRepository(repositoryName, directoryFile))
							terminal.printf("\n -> Repository %s Successfully Cloned \n",repositoryName);
						else
							terminal.printf("\n -> Error Repository not Cloned \n");
					} catch (Exception e) {
						e.getStackTrace();
						terminal.printf("\n -> Error in Cloned Repository \n");
					}
					break;
				case MENU_DELETEREPO:
					try {
						terminal.printf("\nDelete Repository \n");
						repositoryName = textIO.newStringInputReader().read("Repository Name:");
					
						if(peer.deleteRepository(repositoryName))
							terminal.printf("\n Repository %s Successfully Deleted \n",repositoryName);
						else
							terminal.printf("\n Error in Deleting the Repository\n");
					} catch (Exception e) {
						e.getStackTrace();
						terminal.printf("\n -> Error in Delete Repository \n");
					}
					break;
				case MENU_CONFIG:
					try {
						terminal.printf("\nGit Config \n");

						String author = textIO.newStringInputReader().read("Name of Author:");
						if(peer.configAuthor(author))
							terminal.printf("\n Config Author %s Successfully \n",author);
						else
							terminal.printf("\n Error in Deleting the Repository\n");
					} catch (Exception e) {
						e.getStackTrace();
						terminal.printf("\n -> Error in Git Config \n");
					}
					break;
				case MENU_SHOWLOCAL:
					try {
						terminal.printf("\nLOCAL REPOSITORY \n");
					
						List<Repository> list = peer.showLocalRepository();
						if (list.isEmpty()) {
							terminal.printf("\n -> NOT PRESENT\n");
							break;
						}
						
						for (Repository rp: list){
							terminal.printf("\n -> Name: %s\n",rp.getName());
							terminal.printf("\n    Directory: %s\n",rp.getDirectory().getAbsolutePath());
							terminal.printf("\n    Create Author: %s\n",rp.getAuthor());
						}
					} catch (Exception e) {
						e.getStackTrace();
						terminal.printf("\n -> Error in Git Config \n");
					}
					break;
				case MENU_ADD_FILE:
					try {
						terminal.printf("\nAdd File to Repository \n");
						repositoryName = textIO.newStringInputReader().read("Repository Name:");

						File folder = peer.getRepositoryDirectory(repositoryName);
						if(folder==null) {
							terminal.printf("\n -> Not Possibile Add File to Repository %s \n",repositoryName);
							break;
						}
						List<File> listFileSystem = ManageFile.listFiles(folder);

						if(peer.addFilesToRepository(repositoryName, listFileSystem))
							terminal.printf("\n -> File Successfully Added in Repository %s \n" , repositoryName);
						else
							terminal.printf("\n -> Error Added File in Repository %s \n", repositoryName);
					} catch (Exception e) {
						e.getStackTrace();
						terminal.printf("\n -> Error Added File in Repository  \n");
					}
					break;
				case MENU_COMMIT:
					try {
						terminal.printf("\nCommit File \n");
						String repo_name = textIO.newStringInputReader().read("Repository Name:");
						String message = textIO.newStringInputReader().read("Message:");

						if(peer.commit(repo_name, message))
							terminal.printf("\n -> File Successfully Committed in Repository %s \n" , repo_name);
						else
							terminal.printf("\n -> Error Commit File in Repository %s \n", repo_name);
					} catch (Exception e) {
						e.getStackTrace();
						terminal.printf("\n -> Error Commit File in Repository  \n");
					}
					break;

				case MENU_PUSH:
					try {
						terminal.printf("\n Push File to Repository \n");
						String repo_namePush = textIO.newStringInputReader().read("Repository Name:");
						
						String result = peer.push(repo_namePush);
						terminal.printf("\n -> "+result +"\n");
					} catch (Exception e) {
						e.getStackTrace();
						terminal.printf("\n -> Error Commit File in Repository  \n");
					}
					break;
				case MENU_PULL:
					try {
						terminal.printf("\n Pull File \n");
						repositoryName = textIO.newStringInputReader().read("Repository Name:");

						String result = peer.pull(repositoryName);
						terminal.printf("\n -> "+result +"\n");
						
					} catch (Exception e) {
						e.getStackTrace();
						terminal.printf("\n -> Error Commit File in Repository  \n");
					}
					break;
				case LOCAL_HISTORY:
					try {
						terminal.printf("\nLocal History\n");
						repositoryName = textIO.newStringInputReader().read("Repository Name:");
					
						List<Commit> commitLocal = peer.showLocalHistory(repositoryName);
					
						if(commitLocal == null) {
							terminal.printf("\n -> Not Possibile View Local History Repository %s not Exists \n",repositoryName);
						}
						if(commitLocal.isEmpty()) {
							terminal.printf("\n -> No Commit Present in Local Repository %s  \n",repositoryName);
						}
					
						for(int i = commitLocal.size()-1; i>=0; i--) {
							terminal.printf("\n -> "+commitLocal.get(i).toString());
						}
					} catch (Exception e) {
						e.getStackTrace();
					//	terminal.printf("\n -> Error Local History \n");
					}
					break;
				case REMOTE_HISTORY:
					try {
						terminal.printf("\nRemote History\n");
						repositoryName = textIO.newStringInputReader().read("Repository Name:");
					
						List<Commit> commitRemote = peer.showRemoteHistory(repositoryName);
					
						if(commitRemote == null) {
							terminal.printf("\n -> Not Possibile View Remote History Repository %s not Exists \n",repositoryName);
						}
						if(commitRemote.isEmpty()) {
							terminal.printf("\n -> No Commit Present in remote Repository %s  \n",repositoryName);
						}
						
						for(int i = commitRemote.size()-1; i>=0; i--) {
							terminal.printf("\n"+commitRemote.get(i).toString() + "\n");
						}
					} catch (Exception e) {
						e.getStackTrace();
						//terminal.printf("\n -> Error Remote History \n");
					}
					break;
				case MENU_EXIT:
					
					boolean exit = textIO.newBooleanInputReader().withDefaultValue(false).read("exit?");
					if(exit) {
						System.exit(0);
					}
					break;

				default:
					break;
				}
			}



		}  
		catch (CmdLineException clEx)  
		{  
			System.err.println("ERROR: Unable to parse command-line options: " + clEx);  
		}  


	}



	public static void printMenu(TextTerminal<?> terminal) {
		terminal.printf("\n********************************************");
		terminal.printf("\n* #  %s - GIT CONFIG                                       *", MENU_CONFIG);
		terminal.printf("\n* #  %s - CREATE FILE                                      *", MENU_GENERAFILE);
		terminal.printf("\n* #  %s - CREATE REPOSITORY                         *", MENU_CREATEREPO);
		terminal.printf("\n* #  %s - CLONE  REPOSITORY                          *", MENU_CLONEREPO);
		terminal.printf("\n* #  %s - DELETE REPOSITORY                          *", MENU_DELETEREPO);
		terminal.printf("\n* #  %s - SHOW LOCAL REPOSITORY                 *", MENU_SHOWLOCAL);
		terminal.printf("\n* ------------------------------------------------------------------ *");
		terminal.printf("\n* #  %s - ADD FILE               		            *", MENU_ADD_FILE);
		terminal.printf("\n* #  %s - COMMIT                         	            *", MENU_COMMIT);
		terminal.printf("\n* #  %s - PUSH                       	            *", MENU_PUSH);
		terminal.printf("\n* #  %s - PULL                                                  *", MENU_PULL);
		terminal.printf("\n* #%s - SHOW LOCAL HISTORY                       *", LOCAL_HISTORY);
		terminal.printf("\n* #%s - SHOW REMOTE HISTORY                    *", REMOTE_HISTORY);
		terminal.printf("\n* ------------------------------------------------------------------ *");
		terminal.printf("\n* #%s - EXIT                                                   *", MENU_EXIT);
		terminal.printf("\n********************************************\n");


	}


}
