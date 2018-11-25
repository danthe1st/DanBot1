package core;

import java.util.Scanner;
/**
 * used to close a {@link Scanner} when Application stopps
 * @author Daniel Schmid
 */
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
