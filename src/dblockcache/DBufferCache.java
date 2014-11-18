package dblockcache;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import common.Constants.DiskOperationType;

import virtualdisk.VirtualDisk;

public class DBufferCache extends AbstractDBufferCache {
	VirtualDisk vd;
	LinkedHashMap<Integer, DBuffer> cache= new LinkedHashMap<Integer, DBuffer>();
	public DBufferCache(int cacheSize, VirtualDisk vd) {
		super(cacheSize);
		this.vd = vd;
		// TODO Auto-generated constructor stub
	}

	@Override
	public DBuffer getBlock(int blockID) throws IllegalArgumentException, IOException {
		// TODO Auto-generated method stub
		DBuffer newBuff;
		if(!cache.containsKey(blockID)){
			newBuff=new DBuffer(blockID);
			vd.startRequest(newBuff, DiskOperationType.READ);
			//need to deal with Linked part -- make it MRU
			cache.put(blockID,newBuff);
			
		}
		newBuff = cache.get(blockID);
		newBuff.isHeld = true;
		return newBuff;
	}

	@Override
	//WTF IS THIS
	public void releaseBlock(DBuffer buf) {
		buf.isHeld = false;
	}

	@Override
	public void sync() throws IllegalArgumentException, IOException {
		for(Integer i : cache.keySet()){
			if(!cache.get(i).checkClean())
			vd.startRequest(cache.get(i),DiskOperationType.WRITE);
		}
		// TODO Auto-generated method stub
		
	}

	
}
