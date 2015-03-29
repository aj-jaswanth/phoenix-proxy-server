package in.rgukt.phoenix.core.authentication.decoders;

import javax.xml.bind.DatatypeConverter;

public class Base64 {

	public static String decode(String str) {
		return new String(DatatypeConverter.parseBase64Binary(str));
	}

}