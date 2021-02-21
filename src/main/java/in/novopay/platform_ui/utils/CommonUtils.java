package in.novopay.platform_ui.utils;

import java.io.IOException;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

public class CommonUtils extends BasePage {
	public Map<String, String> usrData;

	@FindBy(xpath = "//h4[contains(text(),'Welcome')]")
	WebElement welcomeMessage;

	@FindBy(xpath = "//h4[contains(text(),'Welcome')]/parent::div/following-sibling::div[2]/button")
	WebElement welcomeOKButton;

	@FindBy(xpath = "//button[contains(text(),'I Will Check Later')]")
	WebElement banner;

	@FindBy(xpath = "//*[@class='fa fa-bars fa-lg text-white']")
	WebElement menu;

	@FindBy(xpath = "//*[@class='slimScrollBar']")
	WebElement scrollBar;

	@FindBy(xpath = "//i[contains(@class,'np np-refresh')]")
	WebElement refreshButton;

	@FindBy(xpath = "//i[contains(@class,'np np-sync')]")
	WebElement syncButton;

	@FindBy(xpath = "//span[contains(text(),'wallet balance')]")
	WebElement retailerWallet;

	@FindBy(xpath = "//span[contains(text(),'wallet balance')]/parent::p/following-sibling::p/span")
	WebElement retailerWalletBalance;

	@FindBy(xpath = "//span[contains(text(),'cashout balance')]")
	WebElement cashoutWallet;

	@FindBy(xpath = "//span[contains(text(),'cashout balance')]/parent::p/following-sibling::p/span")
	WebElement cashoutWalletBalance;

	@FindBy(xpath = "//span[contains(text(),'merchant balance')]")
	WebElement merchantWallet;

	@FindBy(xpath = "//span[contains(text(),'merchant balance')]/parent::p/following-sibling::p/span")
	WebElement merchantWalletBalance;

	@FindBy(xpath = "//div[contains(@class,'spinner')]/parent::div")
	WebElement spinner;

	@FindBy(xpath = "//button[contains(text(),'OK. Got it!')]")
	WebElement welcomeButton;

	@FindBy(xpath = "//*[contains(text(),'Choose a Wallet')]")
	WebElement chooseWalletScreen;

	@FindBy(xpath = "//*[@for='agent-wallet']")
	WebElement mainWalletRadioButton;

	@FindBy(xpath = "//*[@for='cashout-wallet']")
	WebElement cashoutWalletRadioButton;

	@FindBy(xpath = "//h5[contains(text(),'Main Wallet')]/following-sibling::p[contains(text(),' ₹')]")
	WebElement mainWalletScreenBalance;

	@FindBy(xpath = "//h5[contains(text(),'Cashout Wallet')]/following-sibling::p[contains(text(),' ₹')]")
	WebElement cashoutWalletScreenBalance;

	@FindBy(xpath = "//*[contains(text(),'Choose a Wallet')]/parent::div/following-sibling::div/button[contains(text(),'Proceed')]")
	WebElement chooseWalletProceedButton;

	@FindBy(xpath = "//*[contains(text(),'Choose a Wallet')]/parent::div/following-sibling::div/button[contains(text(),'Cancel')]")
	WebElement chooseWalletCancelButton;

	@FindBy(xpath = "//*[@for='agent-wallet']//small")
	WebElement chooseWalletMainErrorMsg;

	@FindBy(xpath = "//*[@for='cashout-wallet']//small")
	WebElement chooseWalletCashoutErrorMsg;

	@FindBy(xpath = "//table//tr[contains(@class,'table-row')][1]")
	WebElement firstTxnInList;

	@FindBy(xpath = "//h4[contains(text(),'Processing')]")
	WebElement processingScreen;

	@FindBy(xpath = "//h4[contains(text(),'Pending')]")
	WebElement pendingScreen;

	@FindBy(xpath = "//button[@class='toast-close-button']")
	WebElement toastCloseButton;

	public CommonUtils(WebDriver wdriver) {
		super(wdriver);
		PageFactory.initElements(wdriver, this);
	}

	// Click OK on Welcome pop-up (whenever displayed)
	public void welcomePopup() {
		if (usrData.get("WELCOMEPOPUP").equalsIgnoreCase("YES")) {
			try {
				waitUntilElementIsVisible(welcomeMessage);
				System.out.println("Welcome pop-up displayed");
				waitUntilElementIsClickableAndClickTheElement(welcomeOKButton);
				System.out.println("OK button clicked");
				waitUntilElementIsInvisible("//button[contains(text(),'OK. Got it!')]");
				System.out.println("Pop-up disappeared");
			} catch (Exception e) {
				System.out.println("No pop-up displayed");
			}
		}
	}

	public void selectFeatureFromMenu1(WebElement feature, WebElement pageTitle) throws InterruptedException {
		clickElement(menu);
		scrollElementDown(scrollBar, feature);
		System.out.println(feature.getText() + " option clicked");
		waitForSpinner();
		waitUntilElementIsVisible(pageTitle);
		System.out.println(pageTitle.getText() + " page displayed");
		clickElement(menu);
	}

	public void selectFeatureFromMenu2(WebElement feature, WebElement pageTitle) throws InterruptedException {
		clickElement(menu);
		refreshBalance();
		scrollElementDown(scrollBar, feature);
		System.out.println(feature.getText() + " option clicked");
		waitUntilElementIsVisible(pageTitle);
		System.out.println(pageTitle.getText() + " page displayed");
		clickElement(menu);
	}

	public void selectFeatureFromMenu3(WebElement feature1, WebElement feature2, WebElement pageTitle)
			throws InterruptedException {
		clickElement(menu);
		scrollElementDown(scrollBar, feature1);
		System.out.println(feature1.getText() + " option clicked");
		scrollElementDown(scrollBar, feature2);
		System.out.println(feature2.getText() + " option clicked");
		waitUntilElementIsVisible(pageTitle);
		System.out.println(pageTitle.getText() + " page displayed");
		clickElement(menu);
	}

	// Wait for screen to complete loading
	public void waitForSpinner() {
		waitUntilElementIsInvisible("//div[contains(@class,'spinner')]/parent::div");
		System.out.println("Please wait...");
	}

	// Wait for screen to complete loading
	public void waitForLoader() {
		waitUntilElementIsInvisible("//i[contains(@class,'processing-loader')]");
		System.out.println("Please wait...");
	}

	// To refresh the wallet balance
	public void refreshBalance() throws InterruptedException {
		waitUntilElementIsClickableAndClickTheElement(refreshButton);
		waitUntilElementIsVisible(syncButton);
		waitUntilElementIsVisible(refreshButton);
		System.out.println("Balance refreshed successfully");
	}

	// Get wallet(s) balance
	@SuppressWarnings("null")
	public double getInitialBalance(String wallet) throws ClassNotFoundException {
		String initialWalletBal = replaceSymbols(retailerWalletBalance.getText());
		String initialCashoutBal = replaceSymbols(cashoutWalletBalance.getText());

		// Converting balance from String to Double and returning the same
		if (wallet.equalsIgnoreCase("retailer")) {
			return Double.parseDouble(initialWalletBal);
		} else if (wallet.equalsIgnoreCase("cashout")) {
			return Double.parseDouble(initialCashoutBal);
		}
		return (Double) null;
	}

}
