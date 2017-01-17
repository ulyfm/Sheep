package us.noop.sheep;

import us.noop.server.Main;

public class Start {
	public static void main(String... args){
		Main m = new Main(new SheepServerSetup());
		m.start();
	}
}
