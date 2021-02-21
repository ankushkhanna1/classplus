package in.novopay.platform_ui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;

public class BasePage extends JavaUtils {

	public static WebDriver wdriver;
	public String destFile;

	public BasePage(WebDriver wdriver) {
		BasePage.wdriver = wdriver;
	}

	/**
	 * @return The web driver instance.
	 */
	public WebDriver launchBrowser() {

		// local machine
		String browser = configProperties.get("browser");

		System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_setting_values.notifications", 1);
		prefs.put("profile.default_content_settings.geolocation", 1);
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", prefs);
		wdriver = new ChromeDriver(options);
		String url = "http://automationpractice.com/";
		wdriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wdriver.get(url);
		wdriver.manage().window().maximize();
		return wdriver;
	}

	/**
	 * Open new tab
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public void openNewTab(String urlApp, String urlName) throws InterruptedException {

		String url = "";

		if (urlApp.equalsIgnoreCase("Queuing")) {
			url = urlName;
			System.out.println("Hitting Queuing url");
		} else if (urlApp.equalsIgnoreCase("FinOps Portal")) {
			url = configProperties.get("finOpsUrl");
			System.out.println("Hitting FinOps Portal url");
		}

		((JavascriptExecutor) wdriver).executeScript("window.open()"); // open new tab
		ArrayList<String> tabs = new ArrayList<String>(wdriver.getWindowHandles());
		Thread.sleep(1000);
		wdriver.switchTo().window(tabs.get(1)); // switch to new tab
		wdriver.get(url); // hit url
	}

	/**
	 * Close the tab
	 */
	public void closeTab() {

		wdriver.close();
		System.out.println("Tab is closed");
	}

	/**
	 * Close the tab
	 * 
	 * @throws InterruptedException
	 */
	public void closeTabAndSwitchBack() throws InterruptedException {

		wdriver.close();
		System.out.println("Tab is closed");

		ArrayList<String> tabs = new ArrayList<String>(wdriver.getWindowHandles());
		Thread.sleep(1000);
		wdriver.switchTo().window(tabs.get(0)); // switch to previous tab
	}

	/**
	 * Close the web browser
	 */
	public void closeBrowser() {

		wdriver.quit();
		System.out.println("Web Application is closed");
	}

	/**
	 * Switch the tab to previous one
	 */
	public void switchTab(int index) {
		ArrayList<String> tabs = new ArrayList<String>(wdriver.getWindowHandles());
		wdriver.switchTo().window(tabs.get(index)); // switch to previous window
		System.out.println("Tab is switched to previous one");
	}

	/**
	 * @return The test execution status
	 */
	public String getExecutionResultStatus(int statusCode) {

		String testStatus = null;
		if (statusCode == 1) {
			testStatus = "PASS";
		} else if (statusCode == 2) {
			testStatus = "FAIL";
		} else if (statusCode == 3) {
			testStatus = "SKIPPED";
		}
		return testStatus;
	}

	/**
	 * @return The Object[][] values
	 */
	public Object[][] returnRowsUniqueValueFromSheet(String sheetName) {

		ArrayList<String> allValues = new ArrayList<String>();
		try {
			FileInputStream file = new FileInputStream("./TestData/MobileData.xlsx");
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetName);
			Iterator<Row> it = sheet.rowIterator();

			while (it.hasNext()) {

				Row record = it.next();
				allValues.add(record.getCell(0).toString());
			}

			Object[][] values = new Object[allValues.size()][1];
			for (int i = 0; i < allValues.size(); i++) {
				values[i][0] = allValues.get(i);
			}

			return values;
		} catch (NullPointerException e) {
			System.out.println("Caught NullPointerException");
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			System.out.println("Caught EncryptedDocumentException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Caught IOException");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Select drop down value
	 * 
	 * @param element, text
	 */
	public void selectDropdown(WebElement element, String text) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(wdriver, 20);
		wait.until(ExpectedConditions.visibilityOf(element));
		Select dropdown = new Select(element);
		Thread.sleep(200);
		dropdown.selectByVisibleText(text);
	}

	/**
	 * Select drop down value, here checking option values
	 * 
	 * @param element, value
	 */
	public void selectDropdown2(WebElement element, String value) {

		Select dropdown = new Select(element);

		List<WebElement> oSize = dropdown.getOptions();
		int iListSize = oSize.size();
		System.out.println(oSize.size());
		// Setting up the loop to print all the options
		for (int i = 0; i < iListSize; i++) {
			// Storing the value of the option
			String sValue = dropdown.getOptions().get(i).getText();
			System.out.println(sValue);

			if (sValue.equals(value)) {
				dropdown.selectByVisibleText(value);
				break;
			}
		}

	}

	/**
	 * @return current date in dd mm yyyy format
	 */
	public String currentdate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
		String cd = sdf.format(date);
		return cd;
	}

	/**
	 * @return current date in dd/MMMM/yy hh:mm:ss format
	 */
	public String currentdatemins() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yy hh:mm:ss");
		String cd = sdf.format(date);
		return cd;
	}

	/**
	 * @return string from float value
	 * @param String value
	 */
	public String convertfloat_to_string(String ben) {
		float d = Float.parseFloat(ben);
		int c = (int) d;
		String benAcc = Integer.toString(c);
		return benAcc;

	}

	/**
	 * This method for converting double to string value
	 */
	public String doubletoFormat(String balance) {
		double balvalue = Double.parseDouble(balance);
		DecimalFormat df2 = new DecimalFormat("#.00");
		return df2.format(balvalue);
	}

	/**
	 * @return float from string value
	 */
	public float covertStringtoFloat(String s) {
		float f = Float.parseFloat(s);
		return f;

	}

	/**
	 * @return int from string value
	 */
	public int convertString_Float_int(String s) {
		float f = covertStringtoFloat(s);
		int i = (int) f;
		return i;

	}

	/**
	 * @return int from string value
	 */
	public int covertStringtoint(String s) {
		int f = Integer.parseInt(s);
		return f;

	}

	/**
	 * @return class name
	 * @param class
	 */
	public String getClassName(Class<?> className) {

		String[] clsParts = className.getName().split("\\.");
		String clsName = clsParts[(clsParts.length) - 1];
		System.out.println("Class Name is : " + clsName);
		return clsName;
	}

	/**
	 * This method will wait page to load
	 * 
	 * @param sec
	 */
	public void waitForthePageToLoad(int sec) {
		wdriver.manage().timeouts().implicitlyWait(sec, TimeUnit.SECONDS);

	}

	/**
	 * Takes screenshot both web and mobile
	 */
	public void takeScreenShot(Map<String, String> workFlowDataMap) {
		String destDir = "./ScreenShots";
		File scrFile;
		scrFile = ((TakesScreenshot) wdriver).getScreenshotAs(OutputType.FILE);
		new File(destDir).mkdirs();
		destFile = workFlowDataMap.get("TCID") + ".png";

		try {

			FileUtils.copyFile(scrFile, new File(destDir + "/" + destFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Wait until web element is visible
	 */
	public void waitUntilElementIsVisible(WebElement element) {
		WebDriverWait wait = new WebDriverWait(wdriver, 90);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	/**
	 * Wait until rbl account validation element is visible
	 */
	public void waitUntilRblAccountValElementIsVisible(WebElement element) {
		WebDriverWait wait = new WebDriverWait(wdriver, 150);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	/**
	 * Wait until web element is visible for Fino
	 */
	public void waitUntilElementIsVisibleForFino(WebElement element) {
		WebDriverWait wait = new WebDriverWait(wdriver, 90);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	/**
	 * Wait until web element is invisible
	 */
	public void waitUntilElementIsInvisible(String xpath) {
		WebDriverWait wait = new WebDriverWait(wdriver, 60);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
	}

	/**
	 * Wait until web element is clickable and click the element
	 */
	public void waitUntilElementIsClickableAndClickTheElement(WebElement element) {
		WebDriverWait wait = new WebDriverWait(wdriver, 60);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		clickElement(element);
	}

	/**
	 * Wait until web element is clickable and click the element for Fino
	 */
	public void waitUntilElementIsClickableForFinoAndClickTheElement(WebElement element) {
		WebDriverWait wait = new WebDriverWait(wdriver, 90);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		clickElement(element);
	}

	/**
	 * Wait until Submit element is clickable and click the element
	 */
	public void waitUntilSubmitIsClickableAndClickTheElement(WebElement element) {
		WebDriverWait wait = new WebDriverWait(wdriver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		clickElement(element);
	}

	/**
	 * Wait until Submit element is clickable and click the element for Fino
	 */
	public void waitUntilSubmitIsClickableForFinoAndClickTheElement(WebElement element) {
		WebDriverWait wait = new WebDriverWait(wdriver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		clickElement(element);
	}

	/**
	 * Wait until web element is clickable
	 */
	public void waitUntilElementIsClickable(WebElement element) {
		WebDriverWait wait = new WebDriverWait(wdriver, 60);
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	/**
	 * Wait until web element is clickable for Fino
	 */
	public void waitUntilElementIsClickableForFino(WebElement element) {
		WebDriverWait wait = new WebDriverWait(wdriver, 90);
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	/**
	 * Click on invisible web element for web
	 */
	public void clickInvisibleElement(WebElement webElement) {
		JavascriptExecutor executor = (JavascriptExecutor) wdriver;
		executor.executeScript("arguments[0].click();", webElement);
	}

	// click on invisible WebElement (or forcefully)
	public void clickElement(WebElement element) {
		try {
			element.click();
		} catch (Exception e) {
			clickInvisibleElement(element);
		}
	}

	/**
	 * Scroll to web element visible
	 */
	public void scrollToElement(WebElement webElement) {
		JavascriptExecutor executor = (JavascriptExecutor) wdriver;
		executor.executeScript("arguments[0].scrollIntoView(true);", webElement);
	}

	public void scrollElementDown(WebElement scrollbar, WebElement elementToClick) throws InterruptedException {
//		Actions dragger = new Actions(wdriver);
		int numberOfPixelsToDragTheScrollbarDown = 50;
//		while (true) {
		try {
			// this causes a gradual drag of the scroll bar downwards, 10 units at a time
//				dragger.moveToElement(scrollbar).clickAndHold().moveByOffset(0, numberOfPixelsToDragTheScrollbarDown)
//						.release().perform();
			waitUntilElementIsClickableAndClickTheElement(elementToClick);
//				break;
		} catch (Exception e1) {
			numberOfPixelsToDragTheScrollbarDown = numberOfPixelsToDragTheScrollbarDown + 10;
		}
	}
//	}

	// Scroll down the page
	public void pageScrollDown() {
		JavascriptExecutor jse = (JavascriptExecutor) wdriver;
		jse.executeScript("scroll(0, 250);");
	}

	/**
	 * This method will Capture screenshot on failed test script, save in
	 * Screenshots folder
	 * 
	 * @param result, TCID
	 */
	public void captureScreenshotOnFailedTest(ITestResult result, String Tcid) {
		if (ITestResult.FAILURE == result.getStatus()) {
			try {
				System.out.println("Taking screenshot on failed test");
				File source = ((TakesScreenshot) wdriver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(source, new File("./Screenshots/" + Tcid + ".png"));
				System.out.println("Screenshot taken");
			} catch (Exception e) {
				System.out.println("Exception while taking screenshot " + e.getMessage());
			}
		}
	}
}
