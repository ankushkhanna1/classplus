package tests.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import in.novopay.platform_ui.pages.web.AutomationPage;
import in.novopay.platform_ui.utils.BasePage;
import in.novopay.platform_ui.utils.JavaUtils;

public class AutomationTest {
	String featureName = "Automation";
	public static WebDriver wdriver;
	private BasePage mBasePage = new BasePage(wdriver);
	private AutomationPage wAutomationPage;
	private Map<String, String> usrData;
	public String sheetname = "AutomationPage", workbook = "TestData";
	private JavaUtils javaUtils = new JavaUtils();

	// Start adding all the page objects below this line
	@BeforeSuite
	public void generateIniFile() throws EncryptedDocumentException, InvalidFormatException, IOException {
		javaUtils.readConfigProperties();
	}

	@Test(dataProvider = "getData")
	public void automationTest(HashMap<String, String> usrData) throws ClassNotFoundException, InterruptedException {
		this.usrData = usrData;
		if (wdriver == null) {
			System.out.println("LAUNCHING THE WEB APP FOR FLOW : " + usrData.get("TCID"));
			wdriver = mBasePage.launchBrowser();
		} else if (wdriver != null) {
			System.out.println("LAUNCHING THE WEB APP FOR FLOW : " + usrData.get("TCID"));
		}

		wAutomationPage = new AutomationPage(wdriver);
		wAutomationPage.automation(usrData);
	}

	@AfterClass
	public void killDriver() {

		if (wdriver != null) {
			mBasePage.closeBrowser();
		}
	}

	@DataProvider
	public Object[][] getData() throws EncryptedDocumentException, InvalidFormatException, IOException {

		return mBasePage.returnAllUniqueValuesInMap(workbook, sheetname, "no-check");
	}

	// STORING EXECUTION RESULTS IN EXCEL
	@AfterMethod
	public void result(ITestResult result) throws InvalidFormatException, IOException {

		String failureReason = "";

		if (!result.isSuccess()) {
			failureReason = result.getThrowable() + "";
		}
		String[] execeutionDtls = { JavaUtils.configProperties.get("buildNumber"), featureName, usrData.get("TCID"),
				usrData.get("DESCRIPTION"), javaUtils.getExecutionResultStatus(result.getStatus()), failureReason };
		javaUtils.writeExecutionStatusToExcel(execeutionDtls);
	}

}
