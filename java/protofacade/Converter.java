package protofacade;

import java.util.Map;

import clojure.lang.RT;
import clojure.lang.Symbol;

import com.google.protobuf.MessageOrBuilder;

/**
 * 
 * A Java wrapper to convert-to-map.
 * 
 */
public class Converter {

	static {
		RT.var("clojure.core", "require").invoke(Symbol.intern("proto-facade.core"));
	}
	
	/**
	 * Converts the proto message into a Map, more accurately the message is
	 * wrapped in an object that will make it look like a map.
	 * 
	 * @param msg
	 *            MessageOrBuilder
	 * @return Map of key=String value=Object
	 */
	@SuppressWarnings("unchecked")
	public static final Map<String, Object> convertToMap(final MessageOrBuilder msg) {
		
		return (Map<String, Object>) clojure.lang.RT.var("proto-facade.core",
				"convert-to-map").invoke(msg);
	}

}
