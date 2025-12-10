package Main;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class MyTestCases {
	
	WebDriver driver = new EdgeDriver();
	String site="https://automationteststore.com/index.php?rt=account/create";
	
	Connection con;
	Statement stmt;
	ResultSet rs;

	// - - - -  الفنكشنز
	
	public void scrollByAmountOfX (String x)  {
		
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0, "+ x +");");
	}
	
	public void takeScreenShoot()throws IOException {

		String fileName = "screenshot" + System.currentTimeMillis() + ".jpg";
		TakesScreenshot ts = (TakesScreenshot) driver;
		File myScreenshotFile = ts.getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(myScreenshotFile, new File("src/Main/screenshot/"+fileName));
	}
	
	public String getRandomCustomer () {
		
		String [] randomCuromer = {"112" ,"151" ,"119", "121" , "124"};
		Random rand = new Random();
		int randomUserNumber = rand.nextInt(randomCuromer.length);
		return randomCuromer[randomUserNumber];
	}

	public String getDataByColoumnFromDatabase(String customerNumber , String Coloumn) throws SQLException {
		
		String data = null;
		String Query = "SELECT " + Coloumn + " FROM customers WHERE customerNumber = " + customerNumber;
		
		stmt = con.createStatement();
		rs = stmt.executeQuery(Query);
		
		while (rs.next()) {
			data = rs.getString(Coloumn);
			System.out.println(data);
		}
		
		return data;
	}
	
	// ----- الكود التيست
	
	@BeforeTest
	public void mySetup() throws SQLException{
		
		driver.get(site);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/classicmodels","root", "abc123");
	}
	
	@Test()
	public void UpdateTheData() throws SQLException, IOException, InterruptedException {
		
		String randomUserNumber = getRandomCustomer();
		
		scrollByAmountOfX("200");

		String getFirstName = getDataByColoumnFromDatabase(randomUserNumber , "contactFirstName");
		driver.findElement(By.id("AccountFrm_firstname")).sendKeys(getFirstName);

		String getLastName = getDataByColoumnFromDatabase(randomUserNumber , "contactLastName");
		driver.findElement(By.id("AccountFrm_lastname")).sendKeys(getLastName);
		
		driver.findElement(By.id("AccountFrm_email")).sendKeys("Mahdi" + System.currentTimeMillis()+ "@gmai.com");

		String getUserPhone = getDataByColoumnFromDatabase(randomUserNumber, "phone");
		driver.findElement(By.id("AccountFrm_telephone")).sendKeys(getUserPhone);
		
		takeScreenShoot();
		scrollByAmountOfX("350");
		
		String getAddress = getDataByColoumnFromDatabase(randomUserNumber, "addressLine1");
		driver.findElement(By.id("AccountFrm_address_1")).sendKeys(getAddress);

		String getCity = getDataByColoumnFromDatabase(randomUserNumber, "city");
		driver.findElement(By.id("AccountFrm_city")).sendKeys(getCity);
		
		WebElement countries = driver.findElement(By.id("AccountFrm_country_id"));
		Select select2 = new Select(countries);
		select2.selectByIndex(5);
		
		Thread.sleep(2000);
		
		WebElement states = driver.findElement(By.id("AccountFrm_zone_id"));
		Select select = new Select(states);
		select.selectByIndex(4);
		
		String getPostalCode = getDataByColoumnFromDatabase(randomUserNumber, "postalCode");
		driver.findElement(By.id("AccountFrm_postcode")).sendKeys(getPostalCode);
		
		takeScreenShoot();
		scrollByAmountOfX("350");
		
		String loginName = getDataByColoumnFromDatabase(randomUserNumber, "customerName");
		loginName = loginName.replace(" ", "-") + System.currentTimeMillis();
		driver.findElement(By.id("AccountFrm_loginname")).sendKeys(loginName);
		
		String Password = "abcd@1234";
		driver.findElement(By.id("AccountFrm_password")).sendKeys(Password);
		driver.findElement(By.id("AccountFrm_confirm")).sendKeys(Password);

		driver.findElement(By.id("AccountFrm_agree")).click();
		takeScreenShoot();
		driver.findElement(By.className("lock-on-click")).click();
		takeScreenShoot();

	}

	@AfterTest
	public void exit() throws InterruptedException {
		
		Thread.sleep(2000);
	    driver.close();

	}
		
	}