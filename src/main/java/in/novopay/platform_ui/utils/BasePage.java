package in.novopay.platform_ui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.openqa.selenium.support.ui.ExpectedConditions;
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

		System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");
		wdriver = new ChromeDriver();
		String url = "http://automationpractice.com/";
		wdriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wdriver.get(url);
		wdriver.manage().window().maximize();
		return wdriver;
	}

	/**
	 * Close the web browser
	 */
	public void closeBrowser() {

		wdriver.quit();
		System.out.println("Web Application is closed");
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
		WebDriverWait wait = new WebDriverWait(wdriver, 10);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	/**
	 * Wait until web element is invisible
	 */
	public void waitUntilElementIsInvisible(String xpath) {
		WebDriverWait wait = new WebDriverWait(wdriver, 10);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
	}

	/**
	 * Wait until web element is clickable and click the element
	 */
	public void waitUntilElementIsClickableAndClickTheElement(WebElement element) {
		WebDriverWait wait = new WebDriverWait(wdriver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		clickElement(element);
	}

	/**
	 * Wait until web element is clickable
	 */
	public void waitUntilElementIsClickable(WebElement element) {
		WebDriverWait wait = new WebDriverWait(wdriver, 10);
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
