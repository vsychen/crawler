
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
import java.io.PrintWriter;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/***
 * Here we will implement the Crawler's BFS algorithm.
 * 
 * @author Victor Chen
 *
 */
public class BfsSpider extends Spider {

	// Constructor
	public BfsSpider(String root, String filePath, PrintWriter pw) {
		super(root, filePath, pw);
	}

	/***
	 * This method will connect to the URL, get the page body and get the next links
	 * to be visited, obeying the BFS algorithm.
	 * 
	 * @param url
	 *            the URL from which the page will be acquired
	 * @param timeout
	 *            the maximum time to wait for the response
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	void crawl(String url, int timeout) throws IOException, InterruptedException {
		Connection.Response html = connect(url, timeout);
		Thread.sleep(1000);

		// Get the page body
		saveHtml(html.body());
		this.cookies.putAll(html.cookies());

		// Get the next links
		Document doc = html.parse();
		Elements links = doc.select("a[href]");
		String tempLink;
		String toCompare = "://" + this.host;

		for (Element link : links) {
			tempLink = link.absUrl("href").toLowerCase();

			if (tempLink.contains(toCompare) && this.srr.isAllowed(tempLink))
				this.linksToVisit.add(tempLink);
		}
	}
}