package edu.sjsu.cmpe207.server.protocol;

import edu.sjsu.cmpe207.server.bean.ClientData;

public class ProtocolService {

	public EnumResponse processClientRequest(String input) {
		EnumResponse response = EnumResponse.USAGE;
		System.out.println("processing line - " + input + "\nProcessing...");
		if (input.length() != 12) {
			return getResponseType(EnumRequest.values()[3]);
		}
		for (int i = 0; i < input.length(); i++) {
			if ( (input.charAt(i) - '0') < 0 || (input.charAt(i) - '0') > 9) {
				return getResponseType(EnumRequest.values()[3]);
			}
		}
		ClientData clientRequest = new ClientData(input);
		System.out.println("operation requested = "+clientRequest.getOperationId());
		response = getResponseType(EnumRequest.values()[clientRequest
				.getOperationId()]);
		return response;
	}

	private EnumResponse getResponseType(EnumRequest requestType) {
		System.out.println("Received request of type = " + requestType);
		EnumResponse responseType = EnumResponse.USAGE;
		switch (requestType) {
		case INITIATE:
			responseType = EnumResponse.ACKNOWLEDGE;
			break;
		case HELP:
			responseType = EnumResponse.USAGE;
			break;
		case UPLOAD:
			responseType = EnumResponse.READ;
			break;
		case DOWNLOAD:
			responseType = EnumResponse.WRITE;
			break;
		default:
			responseType = EnumResponse.USAGE;
			break;
		}
		return responseType;
	}

}
