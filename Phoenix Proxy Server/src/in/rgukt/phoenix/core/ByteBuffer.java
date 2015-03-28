package in.rgukt.phoenix.core;

import java.util.Arrays;

public class ByteBuffer {

	private byte[] buffer;
	private int position;
	private int capacity;

	public ByteBuffer(int initialBufferSize) {
		buffer = new byte[initialBufferSize];
		position = 0;
		capacity = initialBufferSize;
	}

	public void put(byte b) {
		if (position == capacity)
			expandBuffer();
		buffer[position++] = b;
	}

	public void put(byte[] array) {
		if (capacity - position < array.length) {
			while ((capacity - position) < array.length)
				capacity *= 2;
			buffer = Arrays.copyOf(buffer, capacity);
		}
		System.arraycopy(array, 0, buffer, position, array.length);
		position += array.length;
	}

	private void expandBuffer() {
		capacity *= 2;
		buffer = Arrays.copyOf(buffer, capacity);
	}

	public void trim() {
		if (position < capacity) {
			buffer = Arrays.copyOf(buffer, position);
			capacity = position;
		}
	}

	public byte get(int index) {
		return buffer[index];
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public int getPosition() {
		return position;
	}

	public int getCapacity() {
		return capacity;
	}
}