
/*
 * Universidade Federal de Pernambuco
 * Centro de Informática (CIn)
 * Recuperação de Informação
 * 
 * Ana Caroline Ferreira de França (acff)
 * Thiago Aquino Santos (tas4)
 * Victor Sin Yu Chen (vsyc)
 */

import java.util.Date;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

/***
 * Here we create all Crawlers.
 * 
 * @author Victor Chen
 *
 */
public class SpiderFactory {

	// Constants
	private static final String roots[] = { "www.americanas.com.br", "www.fastgames.com.br", "www.fnac.com.br",
			"www.livrariacultura.com.br", "www.magazineluiza.com.br", "www.nagem.com.br", "www.saraiva.com.br",
			"store.steampowered.com", "www.submarino.com.br", "www.walmart.com.br" };

	// Variables
	public static String ARTIFACT_PATH = Paths.get(".").toAbsolutePath().normalize().toString() + "/artefatos";
	private Thread threads[];
	public static boolean error = false;

	// Constructor
	public SpiderFactory() {
		this.threads = new Thread[10];
	}

	/***
	 * This method starts the Crawlers.
	 * 
	 * @param type
	 *            search method to be used by the Crawler
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void startCrawlers(String type) throws IOException, InterruptedException {
		File f = new File(ARTIFACT_PATH);

		if (!f.exists())
			f.mkdirs();

		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(ARTIFACT_PATH + "/log.txt", true)));

		if (type == "bfs")
			startBfsCrawlers(pw);
		else
			startHeuristicCrawlers(pw);

		for (Thread t : this.threads)
			t.join();

		if (SpiderFactory.error) {
			pw.print(new Date().toString());
			pw.println(": All Crawlers finished.");
		} else {
			pw.print(new Date().toString());
			pw.println(": All Crawlers successfully finished.");
		}

		pw.close();
	}

	/***
	 * This method runs the BFS Crawlers.
	 * 
	 * @throws RuntimeException
	 */
	private void startBfsCrawlers(PrintWriter pw) throws RuntimeException {
		for (int i = 0; i < 10; i++)
			(this.threads[i] = new Thread(new BfsSpider(SpiderFactory.roots[i], ARTIFACT_PATH + "/bfs", pw))).start();
	}

	/***
	 * This method runs the Heuristic Crawlers.
	 * 
	 * @throws RuntimeException
	 */
	private void startHeuristicCrawlers(PrintWriter pw) throws RuntimeException {
		for (int i = 0; i < 10; i++)
			(this.threads[i] = new Thread(
					new HeuristicSpider(SpiderFactory.roots[i], ARTIFACT_PATH + "/heuristic", pw))).start();
	}
}
