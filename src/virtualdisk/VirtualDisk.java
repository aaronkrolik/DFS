package virtualdisk;
/*
 * VirtualDisk.java
 *
 * A virtual asynchronous disk.
 *
 */

import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import common.Constants;
import common.Constants.DiskOperationType;
import common.INode;
import dblockcache.DBuffer;

public abstract class VirtualDisk implements IVirtualDisk {

	private String _volName;
	private RandomAccessFile _file;
	private int _maxVolSize;
	private int totalMetadataSize;
	private int firstDataBlockOffset;
	private int singleMetadataElementSize;
	private int intsPerInode;
	protected Map<Integer, INode> inodeMap;

	
	public static byte[] intToByteArray (final int integer) {  
	    byte[] result = new byte[4];  
	   
	    result[0] = (byte)((integer & 0xFF000000) >> 24);  
	    result[1] = (byte)((integer & 0x00FF0000) >> 16);  
	    result[2] = (byte)((integer & 0x0000FF00) >> 8);  
	    result[3] = (byte)((integer & 0x000000FF) >> 0);  
	   
	    return result;  
	}
	
	public static int byteArrayToInt (byte[] b){
		return ByteBuffer.wrap(b).getInt();
	}
	
	/*
	 * VirtualDisk Constructors
	 */
	public VirtualDisk(String volName, boolean format) throws FileNotFoundException,
			IOException {

		_volName = volName;
		_maxVolSize = Constants.BLOCK_SIZE * Constants.NUM_OF_BLOCKS;

		/*
		 * mode: rws => Open for reading and writing, as with "rw", and also
		 * require that every update to the file's content or metadata be
		 * written synchronously to the underlying storage device.
		 */
		_file = new RandomAccessFile(_volName, "rws");
		

		/*
		 * Set the length of the file to be NUM_OF_BLOCKS with each block of
		 * size BLOCK_SIZE. setLength internally invokes ftruncate(2) syscall to
		 * set the length.
		 */
		_file.setLength(Constants.BLOCK_SIZE * Constants.NUM_OF_BLOCKS);
		if(format) {
			formatStore();
		}
		/* Other methods as required */
	}
	
	public VirtualDisk(boolean format) throws FileNotFoundException,
	IOException {
		this(Constants.vdiskName, format);
	}
	
	public VirtualDisk() throws FileNotFoundException,
	IOException {
		this(Constants.vdiskName, false);
	}
	
	
	protected void initConstants(){
		inodeMap = new HashMap<Integer, INode>();
		intsPerInode = Constants.INODE_SIZE/4;//4 bytes per int
		this.singleMetadataElementSize = Constants.INODE_SIZE+4;//4 bytes per int
		totalMetadataSize = Constants.MAX_DFILES*singleMetadataElementSize;
		int metadataNumberOfBlocks = totalMetadataSize/Constants.BLOCK_SIZE + 2; // +2 b/c block 0 is empty and round up from division
		firstDataBlockOffset = metadataNumberOfBlocks + 1;
		System.out.println("constants: intsperinode: "+intsPerInode+" singleMetataelementSize "+this.singleMetadataElementSize+" totalMetadataSize "+totalMetadataSize+" firstDataBlockOffset "+firstDataBlockOffset+" ");
	}
	
	public void writeINode(int inodeNumber, INode inode){
		try {
			_file.seek(Constants.BLOCK_SIZE+(Constants.INODE_SIZE*inodeNumber));
			int[] intarr = inode.getPointersCopy();
			for(int i=0; i<intarr.length; i++ ){
				_file.write(VirtualDisk.intToByteArray(intarr[i]));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public INode getINode(int inodeNumber){
		INode ret = new INode(Constants.INODE_SIZE);
		try {
			_file.seek(Constants.BLOCK_SIZE+(Constants.INODE_SIZE*inodeNumber));
			byte[] byteArr = new byte[Constants.INODE_SIZE];
			_file.read(byteArr, 0, byteArr.length);
			ret.initFromByteArray(byteArr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	protected void readInMetadata() throws IOException{
		_file.seek(0);
		System.out.println("first read int"+ _file.readInt());
		_file.seek(0);
		
		byte[] byteArr = new byte[Constants.INODE_SIZE];
		for(int i=0; i<Constants.MAX_DFILES; i++){
			_file.read(byteArr, 0, byteArr.length);
			INode inode = new INode(Constants.INODE_SIZE);
			inode.initFromByteArray(byteArr);
			inodeMap.put(i, inode);
		}
		for(INode x : inodeMap.values()){
			//x.printPointers();
		}
	}
	
	

	/*
	 * Start an asynchronous request to the underlying device/disk/volume. 
	 * -- buf is an DBuffer object that needs to be read/write from/to the volume.	
	 * -- operation is either READ or WRITE  
	 */
	public abstract void startRequest(DBuffer buf, DiskOperationType operation) throws IllegalArgumentException,
			IOException;
	
	/*
	 * Clear the contents of the disk by writing 0s to it
	 */
	private void formatStore() {
		byte b[] = new byte[Constants.BLOCK_SIZE];
		setBuffer((byte) 0, b, Constants.BLOCK_SIZE);
		for (int i = 0; i < Constants.NUM_OF_BLOCKS; i++) {
			try {
				int seekLen = i * Constants.BLOCK_SIZE;
				_file.seek(seekLen);
				_file.write(b, 0, Constants.BLOCK_SIZE);
			} catch (Exception e) {
				System.out.println("Error in format: WRITE operation failed at the device block " + i);
			}
		}
	}

	/*
	 * helper function: setBuffer
	 */
	private static void setBuffer(byte value, byte b[], int bufSize) {
		for (int i = 0; i < bufSize; i++) {
			b[i] = value;
		}
	}

	/*
	 * Reads the buffer associated with DBuffer to the underlying
	 * device/disk/volume
	 */
	private int readBlock(DBuffer buf) throws IOException {
		int seekLen = buf.getBlockID() * Constants.BLOCK_SIZE;
		/* Boundary check */
		if (_maxVolSize < seekLen + Constants.BLOCK_SIZE) {
			return -1;
		}
		_file.seek(seekLen);
		return _file.read(buf.getBuffer(), 0, Constants.BLOCK_SIZE);
	}

	/*
	 * Writes the buffer associated with DBuffer to the underlying
	 * device/disk/volume
	 */
	private void writeBlock(DBuffer buf) throws IOException {
		int seekLen = buf.getBlockID() * Constants.BLOCK_SIZE;
		_file.seek(seekLen);
		_file.write(buf.getBuffer(), 0, Constants.BLOCK_SIZE);
	}
}