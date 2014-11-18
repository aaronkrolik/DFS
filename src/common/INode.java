package common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;

import virtualdisk.VirtualDisk;

public class INode {
	
	private final int totalSizeBytes;
	private final int maxNumberElements;
	private int[] pointers;
	private byte[] bytes;
	
	
	public int[] getPointersCopy(){
		int[] ret = new int[pointers.length];
		for(int i=0; i<pointers.length; i++){
			ret[i]=pointers[i];
		}
		return ret;
	}
	
	public byte[] getByteArray(){
		return bytes;
//		byte[] ret = new byte[pointers.length*4];
//		int count = 0;
//		for(int i=0; i<pointers.length; i++){
//			byte[] b = VirtualDisk.intToByteArray(pointers[i]);
//			for(int j=0; j<4; j++){
//				ret[count]=b[j];
//				count++;
//			}
//		}
//		return ret;
	}
	
	public void printPointers(){
		for(int i=0; i<pointers.length; i++){
			System.out.print(pointers[i]+" ");
		}
		System.out.print(" \n");
	}
	
	public INode(int totalSizeBytes){
		this.totalSizeBytes = totalSizeBytes;
		this.maxNumberElements = totalSizeBytes/4;//4 bytes per int
		pointers = new int[maxNumberElements];
		for(int i=0; i<pointers.length; i++){
			pointers[i]=0;
		}
	}
	

	public boolean isDataSectionFull(){
		for(int i=0; i<(pointers.length-1); i++){
			if(pointers[i]==0){
				return false;
			}
		}
		return true;
	}
	
	public boolean isFull(){
		for(int i=0; i<(pointers.length-1); i++){
			if(pointers[i]==0){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param index index of inode to write data to
	 * @param data pointer to data block
	 */
	public void overWrite(int index, int data){
		pointers[index] = data; 
	}
	
	/**
	 * length of inode. includes indirect pointers
	 */
	public int getNumberElements(){
		return pointers.length;
	}
	
	public void initFromByteArray(byte[] initData) {
		bytes = initData;
		IntBuffer intBuf = ByteBuffer.wrap(initData)
				.order(ByteOrder.BIG_ENDIAN).asIntBuffer();
		int[] array = new int[intBuf.remaining()];
		intBuf.get(pointers);
		//intBuf.get(array);
	}

}
