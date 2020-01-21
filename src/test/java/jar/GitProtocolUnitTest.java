package jar;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import it.p2p.git.impl.GitProtocolImpl;
import it.p2p.git.utils.ManageFile;

public class GitProtocolUnitTest {

	private GitProtocolImpl peer0, peer1, peer2;
	private static String PREFIX = "./test/";


	@Before
	public void testCreateRepo() throws Exception {
		peer0 = new GitProtocolImpl(0, "127.0.0.1");		
		peer1 = new GitProtocolImpl(1, "127.0.0.1");
		
		String nameDir = PREFIX + "/" + "Test";
		File directoryFile = new File(nameDir);
		if(!directoryFile.exists()) 
			directoryFile.mkdir();	

		String nameRep = "Test";
		//repository create
		assertTrue(peer0.createRepository(nameRep, directoryFile));
		
		//return false because the repository its already exists
		assertFalse(peer0.createRepository(nameRep, directoryFile));

		//return false because the remote repository exists
	    assertFalse(peer1.createRepository(nameRep, directoryFile));
	
}
	
	@Test
	public void testAddFileToRep() throws Exception {
	
		String nameDir = PREFIX + "/" + "Test";
		File directoryFile = new File(nameDir);
		if(!directoryFile.exists()) 
			directoryFile.mkdir();
		
		List<String> listFile = ManageFile.generateFile(directoryFile, 2);
		List<File> listFileSystem = ManageFile.listFiles(directoryFile);

		String nameRep = "Test";

		//add file to the repository 
		assertTrue(peer0.addFilesToRepository(nameRep, listFileSystem));
		
		//return false because the repository notExist
		assertFalse(peer0.addFilesToRepository("TestFalse", listFileSystem));


}
	@Test
	public void testConfigAuthor() throws Exception {
		peer0 = new GitProtocolImpl(0, "127.0.0.1");		

		String nameAuth = "Rosa Ferraioli";
		//Configure Author
		assertTrue(peer0.configAuthor(nameAuth));
		
		//return false because the name of author is null or empty
		assertFalse(peer0.configAuthor(null));
		assertFalse(peer0.configAuthor(""));
}

	
	@Test
	public void testCloneRepository() throws Exception {
		peer2 = new GitProtocolImpl(2, "127.0.0.1");		
		String nameDir = PREFIX + "/" + "Test2";
		File directoryFile = new File(nameDir);
		if(!directoryFile.exists()) 
			directoryFile.mkdir();	
		String nameRep = "Test";
		String nameRepNotExist = "Testing";
	
		//clone the repository from the network
		assertTrue(peer2.cloneRepository(nameRep, directoryFile));
	
		// return false because the repository not exists
		assertFalse(peer2.cloneRepository(nameRepNotExist, directoryFile));
		
		//return false because the repository its already cloned		
		assertFalse(peer2.cloneRepository(nameRep, directoryFile));

}
	

	
	
}

