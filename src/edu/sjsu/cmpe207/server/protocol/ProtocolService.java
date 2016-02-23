package edu.sjsu.cmpe207.server.protocol;

import edu.sjsu.cmpe207.server.bean.ClientData;

/**
 * @author Nikhil
 *
 */
public class ProtocolService {
	/*
	 * Class responsible for enforcing the handshake protocol based on the
	 * parsed request's details
	 */
	public EnumResponse processClientRequest(String input,
			ClientData clientRequest) {

		EnumResponse response = EnumResponse.USAGE;

		if (input.length() < 12) {
			return getResponseType(EnumRequest.values()[3]);
		}
		for (int i = 0; i < input.length(); i++) {
			if ((input.charAt(i) - '0') < 0 || (input.charAt(i) - '0') > 9) {
				return getResponseType(EnumRequest.values()[3]);
			}
		}
		clientRequest.setInput(input);

		response = getResponseType(EnumRequest.values()[clientRequest
				.getOperationId()]);
		return response;
	}

	private EnumResponse getResponseType(EnumRequest requestType) {

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
