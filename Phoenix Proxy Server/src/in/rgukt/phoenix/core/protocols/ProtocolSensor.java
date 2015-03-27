package in.rgukt.phoenix.core.protocols;

import in.rgukt.phoenix.core.ByteBuffer;
import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.protocols.http.HttpProtocolRequest;

import java.io.IOException;
import java.io.InputStream;

public class ProtocolSensor {

	public static ApplicationLayerProtocolMessage sense(InputStream inputStream)
			throws IOException {
		ByteBuffer message = new ByteBuffer(
				Constants.HttpProtocol.requesetBufferSize);
		BufferedStreamReader bufferedReader = new BufferedStreamReader(
				inputStream, Constants.HttpProtocol.requesetBufferSize);
		int state = 0;
		for (int x = 0; x < message.getCapacity(); x++) {
			byte b = bufferedReader.read();
			if (b == -1)
				break;
			message.put(b);
			switch (state) {
			case 0:
				if (b == 'G' || b == 'P' || b == 'H' || b == 'O' || b == 'D'
						|| b == 'T')
					state = 1;
				else
					// Almost confirm it is CONNECT. Tunneling proxy.
					return null; // TODO:return HttpsProtocolRequest
				break;
			case 1:
				if (b == 'E' || b == 'O' || b == 'P' || b == 'E' || b == 'R')
					return new HttpProtocolRequest(message, bufferedReader);
				break;

			}
		}
		return null;
	}
}