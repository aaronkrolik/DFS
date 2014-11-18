package dblockcache;

import java.io.IOException;

import common.Constants;
import common.Constants.DiskOperationType;
import virtualdisk.*;
public class DBuffer extends AbstractDBuffer {

	volatile boolean isValid;
	volatile boolean isDirty;
	volatile boolean isHeld=false;
	public volatile boolean isBusy;
	volatile int blockId;
	volatile byte[] buffer;
	VirtualDisk myDisk;

	public DBuffer(int blockId) {
		this.blockId = blockId;
		buffer = new byte[Constants.BLOCK_SIZE];
		startFetch();
	}

	@Override
	/* Start an asynchronous fetch of associated block from the volume */
	public void startFetch() {
		isBusy = true;
		
	}

	@Override
	/* Start an asynchronous write of buffer contents to block on volume */
	public void startPush() {
		isBusy = true;
		// TODO Auto-generated method stub

	}

	@Override
	/* Check whether the buffer has valid data */
	public boolean checkValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	/* Wait until the buffer has valid data, i.e., wait for fetch to complete */
	public boolean waitValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	/*
	 * Check whether the buffer is dirty, i.e., has modified data written back
	 * to disk?
	 */
	public boolean checkClean() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean waitClean() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBusy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	/*
	 * reads into the buffer[] array from the contents of the DBuffer. Check
	 * first that the DBuffer has a valid copy of the data! startOffset and
	 * count are for the buffer array, not the DBuffer. Upon an error, it should
	 * return -1, otherwise return number of bytes read.
	 */
	public int read(byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	/*
	 * writes into the DBuffer from the contents of buffer[] array. startOffset
	 * and count are for the buffer array, not the DBuffer. Mark buffer dirty!
	 * Upon an error, it should return -1, otherwise return number of bytes
	 * written.
	 */
	public int write(byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void ioComplete() {
		// TODO Auto-generated method stub
		isBusy = false;

	}

	@Override
	public int getBlockID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getBuffer() {
		// TODO Auto-generated method stub
		return null;
	}

}
