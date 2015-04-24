package in.rgukt.phoenix.core;

import java.util.Arrays;

/**
 * ByteBuffer is an expandable data structure. This is backed by a byte array.
 * The array is expanded to suit the needs.
 * 
 * @author Venkata Jaswanth
 */
public final class ByteBuffer {

	private byte[] buffer;
	private int position;
	private int capacity;

	/**
	 * Create a ByteBuffer with the initial capacity
	 * 
	 * @param initialSize
	 *            Initial capacity of the ByteBuffer
	 */
	public ByteBuffer(int initialSize) {
		buffer = new byte[initialSize];
		position = 0;
		capacity = initialSize;
	}

	/**
	 * Add a byte to the ByteBuffer. It gets added at the end.
	 * 
	 * @param b
	 *            byte to be added
	 */
	public void put(byte b) {
		if (position == capacity)
			expandBuffer();
		buffer[position++] = b;
	}

	/**
	 * Add an array of bytes to the ByteBuffer.
	 * 
	 * @param array
	 *            Array of bytes to be added.
	 */
	public void put(byte[] array) {
		if (capacity - position < array.length) {
			while ((capacity - position) < array.length)
				capacity *= 2;
			buffer = Arrays.copyOf(buffer, capacity);
		}
		System.arraycopy(array, 0, buffer, position, array.length);
		position += array.length;
	}

	/**
	 * Expands the ByteBuffer according to internal logic. Doubling the capacity
	 * is preferred.
	 */
	private void expandBuffer() {
		capacity *= 2;
		buffer = Arrays.copyOf(buffer, capacity);
	}

	/**
	 * Usually after inserting all the data, some space will be wasted at the
	 * end. Calling this compacts the ByteBuffer to fit to the contents.
	 */
	public void trim() {
		if (position < capacity) {
			buffer = Arrays.copyOf(buffer, position);
			capacity = position;
		}
	}

	/**
	 * Return byte at the given index
	 * 
	 * @param index
	 *            index of the byte
	 * @return byte at the index
	 */
	public byte get(int index) {
		return buffer[index];
	}

	/**
	 * Returns the backing array of the ByteBuffer. Should be used with care.
	 * All direct modifications to the array are undesirable.
	 * 
	 * @return byte array
	 */
	public byte[] getBuffer() {
		return buffer;
	}

	/**
	 * Trims the ByteBuffer and returns the backing array.
	 * 
	 * @return byte array
	 */
	public byte[] getTrimmedBuffer() {
		if (position < capacity)
			trim();
		return buffer;
	}

	/**
	 * Returns the index into which the next byte will be inserted. This is
	 * nothing but the number of bytes inserted so far.
	 * 
	 * @return number of bytes inserted so far.
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Returns the capacity of the ByteBuffer
	 * 
	 * @return capacity of the ByteBuffer
	 */
	public int getCapacity() {
		return capacity;
	}
}