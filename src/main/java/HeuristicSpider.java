
/*
 * Universidade Federal de Pernambuco
 * Centro de Informática (CIn)
 * Recuperação de Informação
 * 
 * Ana Caroline Ferreira de França (acff)
 * Thiago Aquino Santos (tas4)
 * Victor Sin Yu Chen (vsyc)
 */

import java.util.List;
import java.util.LinkedList;
import java.io.PrintWriter;
import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/***
 * Here we will implement the Crawler's Heuristic algorithm.
 * 
 * @author Victor Chen
 *
 */
public class HeuristicSpider extends Spider {

	private Dictionary goodTerms;
	private Dictionary badTerms;

	// Constructor
	public HeuristicSpider(String domain, String filePath, PrintWriter pw) {
		super(domain, filePath, pw);
		createDictionaries();
	}

	private void createDictionaries() {
		goodTerms = new Dictionary();
		badTerms = new Dictionary();

		String[] words = "3ds_game_games_jogo_jogos_pc_ps1_ps2_ps3_ps4_psp_ps-1_ps-2_ps-3_ps-4_ps-vita_xbox_wii"
				.split("_");
		for (String w : words) {
			goodTerms.addWord(w);
		}

		words = "acessorio_acessorios_amiibo_capa_card_carregador_cartao_case_console_consoles_controle_guitarra_headset_mouse_teclado"
				.split("_");
		for (String w : words)
			badTerms.addWord(w);

	}

	/***
	 * This method will connect to the URL, get the page body and get the next links
	 * to be visited, obeying some heuristic algorithm defined by the Search
	 * Engine's developer group.
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

			if (tempLink.length() > 140)
				continue;
			else if (tempLink.contains("#"))
				tempLink = tempLink.substring(0, tempLink.indexOf("#"));

			if (tempLink.contains(toCompare) && this.srr.isAllowed(tempLink))
				selectHeuristic(tempLink);
		}
	}

	/***
	 * This method will select the heuristic to be used. Both the heuristics tends
	 * to not discard the supposed irrelevant pages, because these pages may contain
	 * relevant links in the body. However, both of them will check all the supposed
	 * relevant pages in the toVisit list first.
	 * 
	 * @param url
	 *            the URL to be visited
	 */
	private void selectHeuristic(String url) {
		if (this.domain == "steampowered") {
			steamHeuristic(url);
		} else {
			String[] words = { "" };
			boolean gD = (this.domain == "americanas" || this.domain == "fnac" || this.domain == "livrariacultura"
					|| this.domain == "magazineluiza" || this.domain == "nagem" || this.domain == "submarino");

			if (url.contains("br/"))
				words = url.substring(url.indexOf("br/")).split("[/-]");

			standardHeuristic(url, words, gD);
		}
	}

	/***
	 * This method is the heuristic for the domain "steampowered". The heuristic for
	 * steam is different for others because the URL for steam products only have
	 * the product code and, in most cases, it only needs a specific part of the
	 * link to know if the page is relevant or not for the Crawler. If the language
	 * isn't "pt-br" the Crawler will discard the page.
	 * 
	 * @param url
	 *            the URL to be visited
	 */
	private void steamHeuristic(String url) {
		if (url.contains("?l="))
			return;

		if (url.contains(".com/app") && !url.contains("agecheck"))
			this.linksToVisit.add(0, url);
		else
			this.linksToVisit.add(url);
	}

	/***
	 * This method is the standard heuristic for the Crawler. It will implement the
	 * dictionary method. The URL will be checked to see if it contains any of the
	 * bad terms saved in the dictionary. As some domains have the 'product' tag or
	 * variations, the heuristic will use that tag to differentiate categories pages
	 * from product pages.
	 * 
	 * @param url
	 *            the URL to be visited
	 * @param words
	 *            a list with the terms of the suffix of the URL
	 * @param gD
	 *            boolean to check if this domain is a good domain
	 */
	private void standardHeuristic(String url, String[] words, boolean gD) {
		boolean bT = badTerms.contains(words), gT = goodTerms.contains(words);

		if (bT && gT)
			this.linksToVisit.add(url);
		else if (!bT && gT && !gD)
			this.linksToVisit.add(0, url);
		else if (!bT && gT && gD) {
			if (url.contains("/produto/") || url.contains("/p/") || url.endsWith("/p") || url.contains("/eletronicos/"))
				this.linksToVisit.add(0, url);
			else
				this.linksToVisit.add(url);
		} else
			this.trashToVisit.add(url);
	}
}

class Dictionary {
	// Variables
	List<String> words;

	// Constructor
	public Dictionary() {
		this.words = new LinkedList<String>();
	}

	/***
	 * This method will add a String s to the dictionary.
	 * 
	 * @param s
	 *            the String to be added to the dictionary
	 */
	public void addWord(String s) {
		words.add(s);
	}

	/***
	 * This method will check if the dictionary contains any of the words received.
	 * 
	 * @param toBeChecked
	 *            list with the words to be checked
	 * @return TRUE if the dictionary contains any words received; FALSE otherwise
	 */
	public boolean contains(String[] toBeChecked) {
		for (String word : words) {
			for (String check : toBeChecked) {
				if (word.equals(check))
					return true;
			}
		}

		return false;
	}
}