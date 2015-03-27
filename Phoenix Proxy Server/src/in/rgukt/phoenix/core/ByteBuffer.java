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
			enlargeBuffer();
		buffer[position++] = b;
	}

	public void put(byte[] array) {
		while ((capacity - position) < array.length)
			capacity *= 2;
		byte[] a = new byte[capacity];
		System.arraycopy(buffer, 0, a, 0, position);
		System.arraycopy(array, 0, a, position, array.length);
		buffer = a;
		position += array.length;
	}

	private void enlargeBuffer() {
		capacity *= 2;
		buffer = Arrays.copyOf(buffer, capacity);
	}

	public void trim() {
		buffer = Arrays.copyOf(buffer, position);
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