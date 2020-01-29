package it.p2p.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;


import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import it.p2p.git.impl.GitProtocolImpl;
import it.p2p.git.utils.ManageFile;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GitProtocolUnitTest {

	private GitProtocolImpl peer0, peer1, peer2, peer3,peer4;
	private static String PREFIX = "./test/";


	@Test	
	public void testA() throws Exception {
		File dir = new File(PREFIX);
		deleteDirectory(dir);
		createDir();

		peer0 = new GitProtocolImpl(0, "127.0.0.1");	
		peer1 = new GitProtocolImpl(1, "127.0.0.1");	

		String nameDir = PREFIX + "/" + "TestA";
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

		directoryFile.delete();

	}

	@Test
	public void testB() throws Exception {
		peer0 = new GitProtocolImpl(0, "127.0.0.1");		
		peer1 = new GitProtocolImpl(1, "127.0.0.1");

		String nameDir = PREFIX + "/" + "TestB";
		File directoryFile = new File(nameDir);
		if(!directoryFile.exists()) 
			directoryFile.mkdir();

		List<String> listFile = ManageFile.generateFile(directoryFile, 2);
		List<File> listFileSystem = ManageFile.listFiles(directoryFile);

		String nameRep = "Test";
		
		assertTrue(peer0.createRepository(nameRep, directoryFile));
		
		//Rreturn true and add file to the repository 
		assertTrue(peer0.addFilesToRepository(nameRep, listFileSystem));

		//return false because the repository notExist
		assertFalse(peer0.addFilesToRepository("TestFalse", listFileSystem));
	}

	@Test
	public void testC() throws Exception {
		String nameAuth = "Rosa Ferraioli";
		peer0 = new GitProtocolImpl(0, "127.0.0.1");		

		//Configure Author
		assertTrue(peer0.configAuthor(nameAuth));

		//return false because the name of author is null or empty
		assertFalse(peer0.configAuthor(null));
		assertFalse(peer0.configAuthor(""));
	}

	@Test
	public void testD() throws Exception {
		String nameRep = "Test";

		testB();
		//commit in the repository 
		assertTrue(peer0.commit(nameRep, "primo commit"));

		//return false because the repository not exist
		assertFalse(peer0.commit("TestFalse", "primo commit"));
	}

	@Test
	public void testE() throws Exception {
		peer0 = new GitProtocolImpl(0, "127.0.0.1");
		peer1= new GitProtocolImpl(1, "127.0.0.1");
		peer2 = new GitProtocolImpl(2, "127.0.0.1");		
		peer3 = new GitProtocolImpl(3, "127.0.0.1");
		String nameDir = PREFIX + "/" + "TestE";
		File directoryFile = new File(nameDir);
		String nameRep = "TesPush";

		if(!directoryFile.exists()) 
			directoryFile.mkdir();

		List<String> listFile = ManageFile.generateFile(directoryFile, 2);
		List<File> listFileSystem = ManageFile.listFiles(directoryFile);
	
		assertTrue(peer1.createRepository(nameRep, directoryFile));
		assertTrue(peer1.addFilesToRepository(nameRep, listFileSystem));
		assertTrue(peer1.commit(nameRep, "Primo Commit"));
		
		//Return Successfully because push the changes on the network
		assertEquals("Push Successfully", peer1.push(nameRep)); 
		
		//return Error Push because the repository not exists
		assertEquals("Error Push", peer1.push("TestPushFalso")); 
		String nameDirP1 = PREFIX + "/" + "TestE1";

		File directoryFileP1 = new File(nameDirP1);
		if(!directoryFileP1.exists()) 
			directoryFileP1.mkdir();
		
		assertTrue(peer2.cloneRepository(nameRep, directoryFileP1));
		//because it generates files with the same name
		Thread.sleep(2000);

		List<String> listFileP1 = ManageFile.generateFile(directoryFileP1, 2);
		List<File> listFileSystemP1 = ManageFile.listFiles(directoryFileP1);

		assertTrue(peer2.addFilesToRepository(nameRep, listFileSystemP1));
		assertTrue(peer2.commit(nameRep, "secondo commit"));
		assertEquals("Push Successfully", peer2.push(nameRep)); 

		List<String> listFileP0 = ManageFile.generateFile(directoryFile, 2);
		List<File> listFileSystemP0 = ManageFile.listFiles(directoryFile);

		assertTrue(peer1.addFilesToRepository(nameRep, listFileSystemP0));
		assertTrue(peer1.commit(nameRep, "secondo Commit"));
		
		//Return Not possibile Push, Make Pull because peer1 not updated
		assertEquals("Not possibile Push, Make Pull", peer1.push(nameRep)); 

	

	}

	@Test
	public void testF() throws Exception {
		peer2= new GitProtocolImpl(2, "127.0.0.1");
		peer3 = new GitProtocolImpl(3, "127.0.0.1");

		String nameRep = "UnitTest";
		String nameDir = PREFIX + "/" + "TestClone";
		String nameDirClone = PREFIX + "/" + "Clone";
		File directoryFile = new File(nameDir);
		File dirclone = new File(nameDirClone);

		if(!dirclone.exists()) 
			dirclone.mkdir();

		if(!directoryFile.exists()) 
			directoryFile.mkdir();	

		List<String> listFile = ManageFile.generateFile(directoryFile, 2);
		List<File> listFileSystem = ManageFile.listFiles(directoryFile);

		assertTrue(peer2.createRepository(nameRep, directoryFile));
		assertTrue(peer2.addFilesToRepository(nameRep, listFileSystem));
		assertTrue(peer2.commit(nameRep, "primo commit"));
		assertEquals("Push Successfully", peer2.push(nameRep)); 

		// return false because the repository not exists
		assertFalse(peer3.cloneRepository("Testing", dirclone));

		//clone the repository from the network
		assertTrue(peer3.cloneRepository(nameRep, dirclone));

		//return false because the repository its already cloned        
		assertFalse(peer3.cloneRepository(nameRep, dirclone));

	}
	@Test
	public void testG() throws Exception {
		peer0 = new GitProtocolImpl(0, "127.0.0.1");		
		String nameDir = PREFIX + "/" + "TestG";
		File directoryFile = new File(nameDir);


		if(!directoryFile.exists()) 
			directoryFile.mkdir();	

		String nameRep = "TestCase";

		peer0.createRepository(nameRep, directoryFile);
		//delete the repository from the network
		assertTrue(peer0.deleteRepository(nameRep));

		// return false because the repository not exists
		assertFalse(peer0.deleteRepository(nameRep));

		directoryFile.delete();

	}
	@Test
	public void testH() throws Exception {
		peer0 = new GitProtocolImpl(0, "127.0.0.1");	

		String nameDir = PREFIX + "/" + "TestH";
		File directoryFile = new File(nameDir);

		if(!directoryFile.exists()) 
			directoryFile.mkdir();	

		String nameRep = "TestCase";

		peer0.createRepository(nameRep, directoryFile);

		//return true because the Repository exist
		assertTrue(peer0.existRepository(nameRep));

		// return false because the repository not exists
		assertFalse(peer0.existRepository("Testing"));

		directoryFile.delete();

	}
	@Test
	public void testI() throws Exception {
		peer2 = new GitProtocolImpl(2, "127.0.0.1");		
		String nameDir = PREFIX + "/" + "TestI";
		File directoryFile = new File(nameDir);
		if(!directoryFile.exists()) 
			directoryFile.mkdir();	
		List<String> listFile = ManageFile.generateFile(directoryFile, 2);
		List<File> listFileSystem = ManageFile.listFiles(directoryFile);
		String nameRep = "TestCase";

		peer2.createRepository(nameRep, directoryFile);
		peer2.addFilesToRepository(nameRep, listFileSystem);
		peer2.commit(nameRep, "primo commit");

		//return 1 because it's present one commit
		assertEquals(1, peer2.showLocalHistory(nameRep).size());

		//return null because the repository not exists
		assertEquals(null, peer2.showLocalHistory("testing"));

	}
	@Test
	public void testL() throws Exception {
		peer3 = new GitProtocolImpl(3, "127.0.0.1");		
		String nameDir = PREFIX + "/" + "TestL";
		File directoryFile = new File(nameDir);
		if(!directoryFile.exists()) 
			directoryFile.mkdir();	
		List<String> listFile = ManageFile.generateFile(directoryFile, 2);
		List<File> listFileSystem = ManageFile.listFiles(directoryFile);
		String nameRep = "Testing";

		peer3.createRepository(nameRep, directoryFile);
		peer3.addFilesToRepository(nameRep, listFileSystem);
		peer3.commit(nameRep, "primo commit");

		//return null because not present a commit (make push)
		assertEquals(null, peer3.showRemoteHistory("nameRep"));

		peer3.push(nameRep);

		//return 1 because it's present one commit on Repository Remote
		assertEquals(1, peer3.showRemoteHistory(nameRep).size());

		//return null because the repository not exists
		assertEquals(null, peer3.showRemoteHistory("TestFalso"));

	}
	@Test
	public void testM() throws Exception {
		peer4 = new GitProtocolImpl(4, "127.0.0.1");	
		testL();
		String nameRep = "Testing";

		//return 2 because two are present in Repository
		assertEquals(2, peer4.showFileRepository(nameRep).size());

		//return null because the repository not exists
		assertEquals(null, peer4.showFileRepository("TestFalso"));

	}
	@Test
	public void testN() throws Exception {
		peer0 = new GitProtocolImpl(0, "127.0.0.1");
		peer1= new GitProtocolImpl(1, "127.0.0.1");
		peer2 = new GitProtocolImpl(2, "127.0.0.1");		
		peer3 = new GitProtocolImpl(3, "127.0.0.1");
		peer4 = new GitProtocolImpl(3, "127.0.0.1");

		String nameDir = PREFIX + "/" + "TestF";
		File directoryFile = new File(nameDir);
		String nameRep = "TesTPull";

		if(!directoryFile.exists()) 
			directoryFile.mkdir();

		List<String> listFile = ManageFile.generateFile(directoryFile, 2);
		List<File> listFileSystem = ManageFile.listFiles(directoryFile);

		assertTrue(peer3.createRepository(nameRep, directoryFile));
		assertTrue(peer3.addFilesToRepository(nameRep, listFileSystem));
		assertTrue(peer3.commit(nameRep, "Primo Commit"));
		assertEquals("Push Successfully", peer3.push(nameRep)); 

		String nameDirP1 = PREFIX + "/" + "TestF1";

		File directoryFileP1 = new File(nameDirP1);
		if(!directoryFileP1.exists()) 
			directoryFileP1.mkdir();

		Thread.sleep(1000);
		assertTrue(peer4.cloneRepository(nameRep, directoryFileP1));
		List<String> listFileP1 = ManageFile.generateFile(directoryFileP1, 2);
		List<File> listFileSystemP1 = ManageFile.listFiles(directoryFileP1);


		assertTrue(peer4.addFilesToRepository(nameRep, listFileSystemP1));
		assertTrue(peer4.commit(nameRep, "secondo commit"));
		assertEquals("Push Successfully", peer4.push(nameRep)); 
		
		//Return Pull Successfully and i take the changes from the network
		assertEquals("Pull Successfully", peer3.pull(nameRep)); 
		//Return Alredy Updated because i already take the changes from the network
		assertEquals("Alredy Updated", peer3.pull(nameRep)); 
		//Return Error Pull because repository not exists
		assertEquals("Error Pull", peer3.pull("testpullfalse")); 
		Thread.sleep(1000);
		List<String> listFileP2 = ManageFile.generateFile(directoryFileP1, 2);
		List<File> listFileSystemP2 = ManageFile.listFiles(directoryFileP1);
		assertTrue(peer4.addFilesToRepository(nameRep, listFileSystemP2));
		assertTrue(peer4.commit(nameRep, "Secondo Commit"));
		assertEquals("Push Successfully", peer4.push(nameRep)); 

		Thread.sleep(1000);
		List<String> listFileP3 = ManageFile.generateFile(directoryFile, 2);
		List<File>  listFileSystemP3 = ManageFile.listFiles(directoryFile);
		assertTrue(peer3.addFilesToRepository(nameRep, listFileSystemP3));
		assertTrue(peer3.commit(nameRep, "Secondo Commit"));
		//Return Not possibile Push, Make Pull because peer3 not updated
		assertEquals("Not possibile Push, Make Pull", peer3.push(nameRep)); 
		
		//Return Merge because there is a conflicts 
		assertEquals("Merge", peer3.pull(nameRep)); 

		
	}


	public static boolean deleteDirectory(File path) {
		if(path.exists()) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		return(path.delete());
	}
	public void createDir() {

		File dir = new File(PREFIX);

		if(!dir.getParentFile().exists())
			dir.getParentFile().mkdir();

		if(!dir.exists())
			dir.mkdir();

	}

}

