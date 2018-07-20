package core;

import java.util.Scanner;

public class ScanCloser implements Runnable{
	private Scanner scan;
	
	public ScanCloser(Scanner scan) {
		this.scan=scan;
	}
	
	@Override
	public void run() {
		scan.close();
	}

}
