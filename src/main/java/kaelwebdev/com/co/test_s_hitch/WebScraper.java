package kaelwebdev.com.co.test_s_hitch;

import kaelwebdev.com.co.test_s_hitch.HtmlBurnt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebScraper {
	private WebDriver driver;
	private String url = "https://www.ktronix.com/computadores-tablet/computadoresportatiles/c/BI_104_KTRON";
	
    public static void main(String[] args) {
        
    }
    
    public void start() {
    	//Selecciona el metodo deseado
    	scrapingWithSelenium();
    	//scrapingWithJsoupOffline();
    }
    
    private boolean scrapingWithSelenium() {
    	try {
    		//vincular driver
    		System.setProperty("webdriver.chrome.driver", "C:\\Users\\kael\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
    		
    		// Configura opciones para ejecutar Chrome en modo headless (sin interfaz gráfica).
            ChromeOptions options = new ChromeOptions();
            options.setHeadless(true);
            
            // crear driver
    		driver = new ChromeDriver(options);
    		
            driver.get(url);
            System.out.println( driver );
            WebDriverWait wait = new WebDriverWait(driver, 10);
            //Thread.sleep(5000);
            
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span.price")));
            java.util.List<WebElement> productElements = driver.findElements(By.cssSelector("ol.ais-InfiniteHits-list.product__list > li.ais-InfiniteHits-item"));
            
            if(productElements == null) {
            	System.out.println( "is null" );
            	return false;
            }
            
            // Configura la conexión a la base de datos (MySQL o PostgreSQL).
            /*Connection connection = dbConectionConfig(
            		"jdbc:mysql://localhost:3306/tu_basedatos",
            		"user", 
            		"pwd"
            );*/
            for (WebElement productElement : productElements) {
                String nombre = productElement.findElement(By.cssSelector("h3.product__item__top__title a")).getText();
                java.util.List<WebElement> precioE = productElement.findElements(By.cssSelector("p.product__price--discounts__price span.price"));
                String precio = precioE.get(1).getText();
                String codigo = productElement.findElement(By.cssSelector("div.product__item__information__view-details a")).getAttribute("data-id");
                
                /*
                // este fragmento de logica podria ser util en un caso especial donde el precio fuera ocultado
                WebElement elementoOculto = productElement.findElement(By.cssSelector("p.product__price--discounts__price span.price"));
                String precioOculto = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].textContent;", elementoOculto);
                System.out.println("Texto del precio oculto: " + precioOculto);
                */
                
                
                System.out.println("Nombre: " + nombre);
                System.out.println("Precio: " + precio);
                System.out.println("Código: " + codigo);
                
                //Inserta la información en la base de datos.
                //sqlInsertion(nombre, precio, codigo, connection)
            }
            
            driver.quit();
            
            // Cierra la conexión a la base de datos.
            //connection.close();
            return true;
    	} catch (Exception e) {
    		return false;
    	}
    	
        
    }
    
    private boolean scrapingWithJsoupOffline() {
    	

        try {
        	//Conecta con la página web.Pero en este caso creo que no es posible porque los datos a buscar parecen no cargar.
        	//Document document = Jsoup.connect(url).get();

        	// Por tanto a Emulando la data del html.
        	String html = HtmlBurnt.htmlfake;
        	Document document = Jsoup.parse(html);

          
            // Obtén los elementos que contienen información de los productos.
            Elements productElements = document.select("ol.ais-InfiniteHits-list.product__list > li.ais-InfiniteHits-item");
            if (productElements.isEmpty()) {
                System.out.println("No se encontraron elementos que coincidan con el selector.");
                return false;
            } 
        
            // Configura la conexión a la base de datos (MySQL o PostgreSQL).
            /*Connection connection = dbConectionConfig(
            		"jdbc:mysql://localhost:3306/tu_basedatos",
            		"user",
            		"pwd"
            );*/
            
            int counter = 0;
            for (Element productElement : productElements) {
            	
            	System.out.println( "----------" );
            	 
            	counter = counter + 1;
            	
            	
            	//System.out.println( productElement );
                String nombre = productElement.select("h3.product__item__top__title a").text();
                String precio = productElement.select("p.product__price--discounts__price span.price").first().text();
                String codigo = productElement.select("div.product__item__information__view-details a").attr("data-id");
                
                System.out.println("Index: " + counter);
                System.out.println("Nombre: " + nombre);
                System.out.println("Precio: " + precio);
                System.out.println("Código: " + codigo);
                
                // Inserta la información en la base de datos.
                //sqlInsertion(nombre, precio, codigo, connection)
               
            }
            
            // Cierra la conexión a la base de datos.
            //connection.close();
            
            return true;
            
        } catch ( Exception e) {
            e.printStackTrace();
            System.out.println( e );
            return false;
        }
    }
    
    private  Connection dbConectionConfig(String jdbcUrl, String user, String password) throws SQLException {
    	/**
    	 * @jdbcUrl URL de base de datos
    	 * @user user bd
    	 * @password pwd bd
    	 * return connection
    	 */
	    // Inserta la información en la base de datos.
        Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
        return connection;
 
    }
    
    private void sqlInsertion(String nombre, String precio, String codigo, Connection connection) throws SQLException {
	    // Inserta la información en la base utilizando una manera que evita inyecciones SQL enemigas.
	    String insertQuery = "INSERT INTO productos (nombre, precio, codigo) VALUES (?, ?, ?)";
	    PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
	    preparedStatement.setString(1, nombre);
	    preparedStatement.setString(2, precio);
	    preparedStatement.setString(3, codigo);
	    preparedStatement.executeUpdate();
 
    }
    
}
