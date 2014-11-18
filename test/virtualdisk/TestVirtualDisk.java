package virtualdisk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import common.Constants;
import common.INode;
import common.Constants.DiskOperationType;
import dblockcache.DBuffer;

public class TestVirtualDisk {
	
	@Test
	public void testIntegerToByteArray(){
		Random r = new Random();
		int i=0;
		for(i=0; i<2000; i++){
			Integer x = r.nextInt();
			byte[] b = VirtualDisk.intToByteArray(x);
			Integer y = VirtualDisk.byteArrayToInt(b);
			Assert.assertEquals(x, y);
		}		
		Assert.assertEquals(2000, i);
	}

	@Test
	public void firstTest() throws Exception {	
		List<Integer> ints = new ArrayList<Integer>();
		File f = new File("TestFile.txt");
		f.delete();
		f.createNewFile();
		
		
		
		FileOutputStream fos = new FileOutputStream(f);
		Random r = new Random();
		for(int j = 0; j<15000; j++){
			Integer i = r.nextInt(Constants.MAX_DFILES);
			byte[] b = VirtualDisk.intToByteArray(i);
			if(j<10){
				System.out.println(i);
			}
				
			fos.write(b);
		}
		fos.flush();
		fos.close();
		VirtualDisk vd = new VirtualDisk("TestFile.txt", false){

			@Override
			public void startRequest(DBuffer buf, DiskOperationType operation)
					throws IllegalArgumentException, IOException {}
			
		};
		vd.initConstants();
		vd.readInMetadata();
	}
	
	@Test
	public void testWriteAndReadInode() throws FileNotFoundException, IOException{
		VirtualDisk vd = new VirtualDisk("TestFile.txt", false){

			@Override
			public void startRequest(DBuffer buf, DiskOperationType operation)
					throws IllegalArgumentException, IOException {}
			
		};
		vd.initConstants();
		vd.readInMetadata();
		Random rand = new Random();
		for(int i=0; i<1000; i++){
			byte[] bytes = new byte[Constants.INODE_SIZE];
			rand.nextBytes(bytes);
			int r = rand.nextInt(100);
			INode inode1 = new INode(Constants.INODE_SIZE);
			inode1.initFromByteArray(bytes);
			vd.writeINode(i, inode1);
			INode inode2 = vd.getINode(i);
			byte[] bytes2 = inode2.getByteArray();
			Assert.assertEquals(bytes.length,bytes2.length);
			for(int j=0; j<bytes.length; j++){
				Assert.assertEquals(bytes[j], bytes2[j]);
			}			
		}
		
		
		
	}
}
