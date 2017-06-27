package PortfolioManager;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


/**
 * Gets all the data via web scraping from the internet or from files (different fonts depending the data)
 * @author Nicolas Benenzon
 *
 */
public class Syst {
	
	private static List<Stock> stocks;
	private static List<Commodity> commodities;
	private static List<Futuro> futuros;
	private static List<Bono> bonos;
	private static List<Opcion> opciones;
	private static double dolarBlueStatic;
	private static double dolarOficialStatic;
	private final static int CANT_BONOS = 44;

	public Syst(List<Stock> stocks, List<Commodity> commodities, List<Futuro> futuros, List<Bono> bonos, List<Opcion> opciones) {
		super();
		this.stocks = stocks;
		this.commodities = commodities;
		this.futuros = futuros;
		this.bonos = bonos;
		this.opciones = opciones;
	}

	public static void updateValuesFromInternet(){
		stocks = getStocksFromMerval();
		bonos = getBonosFromMerval();
		opciones = getOpcionesFromMerval();
		commodities = getCommoditiesFromFile();
		futuros = getFuturosFromFile();
	}
	
	/*
	 * Métodos getFuturosFromFile, getCommoditiesFromFile, etc. nos hubiese gustado hacerlos genéricos pero tuvimos inconvenientes intentando crear la instancia de forma genérica, nos gustaría saber la forma de poder hacerlo 
	 */
	
	private static List<Stock> getStocksFromFile() {
		List<Stock> stocks = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader("stocks.txt"))) {
		    String line = br.readLine();
		    String[] parsedData;
		    while (line != null) {
		    	parsedData = line.split(" ");
		    	stocks.add(new Stock(Double.parseDouble(parsedData[1]), parsedData[0]));
		        line = br.readLine();
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stocks;
	}
	
	private static List<Bono> getBonosFromFile() {
		List<Bono> bonos = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader("bonos.txt"))) {
		    String line = br.readLine();
		    String[] parsedData;
		    while (line != null) {
		    	parsedData = line.split(" ");
		    	bonos.add(new Bono(Double.parseDouble(parsedData[1]), parsedData[0]));
		        line = br.readLine();
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bonos;
	}
	
	private static List<Opcion> getOpcionesFromFile() {
		List<Opcion> opciones = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader("opciones.txt"))) {
		    String line = br.readLine();
		    String[] parsedData;
		    while (line != null) {
		    	parsedData = line.split(" ");
		    	opciones.add(new Opcion(Double.parseDouble(parsedData[1]), parsedData[0]));
		        line = br.readLine();
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return opciones;
	}
	
	/**
	 * Get futuros from a file
	 * @return
	 */
	
	private static List<Futuro> getFuturosFromFile() {
		List<Futuro> futuros = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader("futuros.txt"))) {
		    String line = br.readLine();
		    String[] parsedData;
		    while (line != null) {
		    	parsedData = line.split(" ");
		    	futuros.add(new Futuro(Double.parseDouble(parsedData[1]), parsedData[0]));
		        line = br.readLine();
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return futuros;
	}
	
	private static List<Commodity> getCommoditiesFromFile() {
		List<Commodity> commodities = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader("commodities.txt"))) {
		    String line = br.readLine();
		    String[] parsedData;
		    while (line != null) {
		    	parsedData = line.split("_");
		        commodities.add(new Commodity(Double.parseDouble(parsedData[1]), parsedData[0]));
		        line = br.readLine();
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return commodities;
	}
	
	private static List<Bono> getBonosFromMerval(){
		Document doc = null;
		boolean loaded = false;
		List<Bono> mervalBonos = new ArrayList<Bono>();
		try {
			doc = Jsoup.connect("http://www.merval.sba.com.ar/Vistas/Cotizaciones/TitulosPublicos.aspx").get();
			loaded = true;
		} catch (IOException e) {
			return getBonosFromFile();
		}
		if(loaded){
			String[] tickers = doc.select("td.txtGrisTabla_ConBorde a.link").text().split(" ");
			String[] parsedData = doc.select("td.txtGrisTabla_ConBorde ~ td.txtGrisTabla_ConBorde").text().replaceAll("0,00 %", "skip").split(" ");
			final List<String> list = new ArrayList<String>();
			Collections.addAll(list, parsedData); 
			list.removeAll(Collections.singleton("skip"));
			parsedData = list.toArray(new String[list.size()]);
			String[] prices = new String[tickers.length];
			int added = 0;
			for(int i = 5; i < parsedData.length; i+=10){
				if(added < CANT_BONOS){
					prices[added] = parsedData[i].replace(",", ".");
					added++;
				}
			}
			for(int i = 0; i < CANT_BONOS; i++){
				mervalBonos.add(new Bono(Double.parseDouble(prices[i]), tickers[i]));
			}
			return mervalBonos;
		}
		return null;
	}

	/**
	 * Get stocks from MERVAL via web scraping
	 * @return
	 */
	
	private static List<Stock> getStocksFromMerval(){
		Document doc = null;
		boolean loaded = false;
		List<Stock> mervalStocks = new ArrayList<Stock>();
		try {
			doc = Jsoup.connect("http://www.merval.sba.com.ar/Vistas/Cotizaciones/Acciones.aspx").get();
			loaded = true;
		} catch (IOException e) {
			return getStocksFromFile();
		}
		if(loaded){
			String[] tickers = doc.select("td.txtGrisTabla_ConBorde a.link").text().split(" ");
			String[] parsedData = doc.select("td.txtGrisTabla_ConBorde ~ td.txtGrisTabla_ConBorde").text().replaceAll("0,00 %", "skip").split(" ");
			final List<String> list = new ArrayList<String>();
			Collections.addAll(list, parsedData); 
			list.removeAll(Collections.singleton("skip"));
			parsedData = list.toArray(new String[list.size()]);
			String[] prices = new String[tickers.length];
			int added = 0;
			for(int i = 5; i < parsedData.length; i+=9){
				prices[added] = parsedData[i].replace(",", ".");
				added++;
			}
			for(int i = 0; i < tickers.length; i++){
				double auxDouble = Double.parseDouble(prices[i]);
				mervalStocks.add(new Stock(auxDouble, tickers[i]));
			}
			return mervalStocks;
		}
		return null;
	}
	
	private static List<Opcion> getOpcionesFromMerval(){
		Document doc = null;
		boolean loaded = false;
		List<Opcion> mervalOpciones = new ArrayList<Opcion>();
		try {
			doc = Jsoup.connect("http://www.merval.sba.com.ar/Vistas/Cotizaciones/OpcionesSuscripcion.aspx").get();
			loaded = true;
		} catch (IOException e) {
			return getOpcionesFromFile();
		}
		if(loaded){
			String[] parsedData = doc.select("td.txtGrisTabla_ConBorde").text().replaceAll("0,00 %", "skip").split(" ");
			final List<String> list = new ArrayList<String>();
			Collections.addAll(list, parsedData); 
			list.removeAll(Collections.singleton("skip"));
			parsedData = list.toArray(new String[list.size()]);
			String[] tickers = new String[parsedData.length];
			String[] prices = new String[parsedData.length];
			int tickerAdded = 0, added = 0;
			for(int i = 0; i < parsedData.length; i++){
				if (parsedData[i].matches(".*[A-Z].*")) { 
					tickers[tickerAdded] = parsedData[i];
					tickerAdded++;
				}
				else if(!parsedData[i].contains(":") && !parsedData[i].contains(".") && !parsedData[i].contains("-")){
					prices[added] = parsedData[i].replace(",", ".");
					added++;
				}
			}
			for(int i = 0; i < tickerAdded; i++){
				mervalOpciones.add(new Opcion(Double.parseDouble(prices[i]), tickers[i]));
			}
			return mervalOpciones;
		}
		return null;
	}
	
	/**
	 * Gets News from Cronista via web scraping
	 */
	
	public static void loadNewsFromInternet(){
		
		Document doc = null;
		boolean loaded = false;
		String news1, news2, news3, news4;
		news1 = news2 = news3 = news4 = "No hay conexion a internet";
		try {
			doc = Jsoup.connect("https://www.cronista.com/").get();
			loaded = true;
		} catch (IOException e) {
			
		}
		if(loaded)
		{
	        news1 = doc.select(".content-bloque-4 .entry-box .entry-data h2 a").first().text();
	        news2 = doc.select(".pull-left .entry-box .entry-data h3 a").first().text();
	        news3 = doc.select(".content-bloque-3 article div h3 a").last().text();
	        news4 = doc.select(".entry-box .entry-data h3 a").first().text();
		}
		MainScreen.setNews(news1, news2, news3, news4);
	}
	
	/**
	 * Get dollar prices via web scraping from Ámbito
	 */
	
	public static void getDollarsFromAmbito(){
		
		Document doc = null;
		boolean loaded = false;
		String dolarOficial, dolarBlue;
		dolarOficial = dolarBlue = "No hay conexion a internet";
		try {
			doc = Jsoup.connect("http://www.ambito.com/economia/mercados/monedas/dolar/").get();
			loaded = true;
		} catch (IOException e) {
			dolarOficial = "15.65";
			dolarBlue = "16.20";
		}
		if(loaded)
		{
	        dolarOficial = doc.select("div.row div.col-xs-12 div.dolarPrincipal div.floatleft div.ultimo big").first().text();
	        dolarBlue = doc.select("div.row div.col-xs-12 div.dolarPrincipal div.floatleft div.ultimo big").text();
	        dolarOficial = dolarOficial.split(" ")[0].replace(",", ".");
	        dolarBlue = dolarBlue.split(" ")[1].replace(",", ".");
	        dolarOficialStatic = Double.parseDouble(dolarOficial);
	        dolarBlueStatic = Double.parseDouble(dolarBlue);
		}
		MainScreen.setDolar(dolarOficial, dolarBlue, CCL.getMinCCL());
		MainScreen.setDolarConverter(dolarOficialStatic, dolarBlueStatic);
	}
	
	public static double getDolarOficial(){
		return dolarOficialStatic;
	}
	
	public static double getDolarBlue(){
		return dolarBlueStatic;
	}

	public static List<Stock> getStocks() {
		return stocks;
	}

	public static List<Commodity> getCommodities() {
		return commodities;
	}

	public static List<Futuro> getFuturos() {
		return futuros;
	}

	public static List<Bono> getBonos() {
		return bonos;
	}

	public static List<Opcion> getOpciones() {
		return opciones;
	}

	public void setStocks(List<Stock> stocks) {
		this.stocks = stocks;
	}

	public void setCommodities(List<Commodity> commodities) {
		this.commodities = commodities;
	}

	public void setFuturos(List<Futuro> futuros) {
		this.futuros = futuros;
	}

	public void setBonos(List<Bono> bonos) {
		this.bonos = bonos;
	}

	public void setOpciones(List<Opcion> opciones) {
		this.opciones = opciones;
	}
}
