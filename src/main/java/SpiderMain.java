
/*
 * Universidade Federal de Pernambuco
 * Centro de Informática (CIn)
 * Recuperação de Informação
 * 
 * Ana Caroline Ferreira de França (acff)
 * Thiago Aquino Santos (tas4)
 * Victor Sin Yu Chen (vsyc)
 */

import java.io.IOException;

public class SpiderMain {

	public static void main(String[] args) throws InterruptedException {
		long timeInMilis = System.currentTimeMillis();

		try {
			SpiderFactory sf = new SpiderFactory();
			sf.startCrawlers("bfs");
			sf.startCrawlers("heuristic");
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(System.currentTimeMillis() - timeInMilis);
	}
}