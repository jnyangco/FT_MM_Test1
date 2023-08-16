package testcase;

import org.openqa.selenium.By;
import org.testng.annotations.Test;
import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FT_MM_Test1 {
	
	//WebDriver
	protected WebDriver driver;
	protected WebDriverWait wait;
	
	//Objects and variables
	private String drpStartYearElement = "//select[@id='dv-start-year']";
	private String drpEndYearElement = "//select[@id='dv-end-year']";
	private By chkbox1MonthNetChange = By.xpath("//label[contains(.,'1-Month Net Change')]");
	private By chkbox12MonthNetChange = By.xpath("//label[contains(.,'12-Month Net Change')]");
	private By chkbox1MonthPercentChange = By.xpath("//label[contains(.,'1-Month % Change')]");
	private By chkbox3MonthPercentChange = By.xpath("//label[contains(.,'3-Month % Change')]");
	private By chkbox6MonthPercentChange = By.xpath("//label[contains(.,'6-Month % Change')]");
	private By btnUpdate = By.xpath("//input[@id='dv-submit']");
	private String btnUpdateXpath = "//input[@id='dv-submit']";
	private By btnDownload = By.xpath("//*[text()='Export Chart']/..");
	private By btnDownloadJPEGImage = By.xpath("//li[text()='Download JPEG image']");
	private By lblLatestObservation = By.xpath("//span[contains(.,'Latest Observation')]/following::span[2]");
	private By lblMinimumValue = By.xpath("//span[contains(.,'Minimum Value')]/following::span[1]");
	private By lblMaximumValue = By.xpath("//span[contains(.,'Maximum Value')]/following::span[1]");
	private By lblDataAvailability = By.xpath("//span[contains(.,'Data Availability')]/following::span[1]");
	private By btnDownloadCSVfile = By.xpath("//input[@id='csvclickCX']");
	private String mmDownloadPath = System.getProperty("user.dir") +"\\MM_Download";
	private String filenameJPEGImage = "total-average-annual-exp.jpeg";
	private String filenameCSV = "file.csv";

	
	@Test
	public void testBLSBetaLabs() throws InterruptedException {
		
		//Step 1: Launch browser
		startTest();
		
		//Step 2: Go to website
		driver.get("https://beta.bls.gov/dataViewer/view/timeseries/CXUTOTALEXPLB0401M;jsessionid=27BFBA409CD7A11361FFCEB601A2943A");
		
		//Step 3: Select search parameters
		selectDropdown(drpStartYearElement, "2000");
		selectDropdown(drpEndYearElement, "2021");
		click(chkbox1MonthNetChange);
		click(chkbox12MonthNetChange);
		click(chkbox1MonthPercentChange);
		click(chkbox3MonthPercentChange);
		click(chkbox6MonthPercentChange);
		waitTime(1);
		
		//Step 4: Click update button
		click(btnUpdate);
		scrollToElement(btnUpdateXpath);
		waitTime(4);
		
		//Step 5: Download JPEG image
		click(btnDownload);
		waitTime(1);
		click(btnDownloadJPEGImage);
		waitTime(4);
		
		//Step 6: Check JPEG image is downloaded successfully
		verifyFileDownloadedSuccessfully(filenameJPEGImage);
		
		//Step 7: Download CSV file
		click(btnDownloadCSVfile);
		waitTime(4);
		
		//Step 8: Check CSV file is downloaded successfully
		verifyFileDownloadedSuccessfully(filenameCSV);
		
		//Step 9: Sort column E and save as new file
		sortCSVColumnInDescendingOrder(filenameCSV, 5);
		
		//Step 10: Extract and print annual values
		printAnnualValues();
		
		driver.quit();
		
	}
	
	

	
	
	public void startTest() {
		System.out.println("Start Test");
		cleanupDownloadedFiles(mmDownloadPath);
		
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("download.default_directory", mmDownloadPath);
		
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", chromePrefs);
		
		System.setProperty("webdriver.chrome.driver", "C:\\Selenium Configuration\\browser_jars\\chromedriver.exe");
		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}
	
	public void click(By locator) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
			System.out.println("Clicked " +locator);
		} catch (Exception e) {
			Assert.fail("Unable to click the element -> " +locator);
		}
	}
	
	public void selectDropdown(String elementID, String dropdownText) {
		try {
			waitTime(2);
			WebElement element = driver.findElement(By.xpath(elementID));
			wait.until(ExpectedConditions.elementToBeClickable(element));
			Select dropdown = new Select(element);
			dropdown.selectByVisibleText(dropdownText);
			System.out.println("Dropdown selected " +dropdownText);
		} catch (Exception e) {
			Assert.fail("Unable to select dropdown " +dropdownText);
		}
	}
	
	public void waitTime(int time) throws InterruptedException {
		Thread.sleep(time * 1000);
	}
	
	public String getText(By locator) {
		String text = null;
		try {
			text = wait.until(ExpectedConditions.presenceOfElementLocated(locator)).getText();
			//System.out.println("Get text -> " +text);
		} catch (Exception e) {
			Assert.fail("Unable to get text of element -> " +locator);
		}
		return text;
	}
	
	public void scrollToElement(String locator) {
		try {
			WebElement element = driver.findElement(By.xpath(locator));
			((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
		} catch (Exception e) {
			Assert.fail("Unable to scroll to element -> " +locator);
		}
	}
	
	public void verifyFileDownloadedSuccessfully(String filename) {
		try {
			File file = new File(mmDownloadPath +"\\" +filename);
			if(file.exists()) {
				System.out.println("File is downloaded successfully -> " +filename);
			} else {
				Assert.fail("File is not downloaded");
			}
		} catch (Exception e) {
			Assert.fail("Unable to verify downloaded files");
		}
	}
	
	public void cleanupDownloadedFiles(String downloadFolderPath) {
		try {
			File path = new File(downloadFolderPath);
			FileUtils.cleanDirectory(path);
			System.out.println("Download folder is cleaned-up");
		} catch (Exception e) {
			Assert.fail("Unable to clean-up download folder");
		}
	}
	
	public void printAnnualValues() {
		System.out.println("Annual Values:");
		System.out.println("Latest Observation: " +getText(lblLatestObservation));
		System.out.println("Minimum Value: " +getText(lblMinimumValue));
		System.out.println("Maximum Value: " +getText(lblMaximumValue));
		System.out.println("Data Availability: " +getText(lblDataAvailability));
		System.out.println("");
	}

	public void sortCSVColumnInDescendingOrder(String filename, int columnNumber) {
		try {
			String sortedFilename = filename.replace(".csv", "-sorted.csv");
			String header = null;
			
	        BufferedReader reader = new BufferedReader(new FileReader(mmDownloadPath +"\\" +filename));
	        Map<String, List<String>> map = new TreeMap<String, List<String>>(Collections.reverseOrder());
	        String line = reader.readLine();//start read header
	        header = line;
	        while ((line = reader.readLine()) != null) {
	            String key = getField(line, columnNumber);
	            List<String> l = map.get(key);
	            if (l == null) {
	                l = new LinkedList<String>();
	                map.put(key, l);
	            }
	            l.add(line);
	        }
	        reader.close();
	        
	        FileWriter writer = new FileWriter(mmDownloadPath +"\\" +sortedFilename);
	        //writer.write("Series ID,Year,Period,Label,Value,1-Month Net Change");
	        writer.write(header);
	        writer.write("\n");
	        for (List<String> list : map.values()) {
	            for (String val : list) {
	                writer.write(val);
	                writer.write("\n");
	            }
	        }
	        writer.close();
	        waitTime(2);
	        System.out.println("Successfully sorted the CSV file with column " +columnNumber);
		} catch (Exception e) {
			Assert.fail("Error in sorting CSV file");
		}
	}
	
	
    public String getField(String line, int columnNumber) {
        return line.split(",")[columnNumber-1];// extract value to sort on
    }
	
}
