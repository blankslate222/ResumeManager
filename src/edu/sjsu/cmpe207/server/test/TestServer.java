package edu.sjsu.cmpe207.server.test;

import edu.sjsu.cmpe207.server.protocol.ProtocolService;

public class TestServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		char c = '1';
		System.out.println( c - '0');
		ProtocolService ps = new ProtocolService();
		System.out.println(ps.processClientRequest("100093100281"));
	}

}
