package in.novopay.platform_ui.pages.web;
//Assignment 1
/**
1. Go to https://www.amazon.in. 
2. Once page is loaded, search for iPhone 12 (128GB) - Blue. 
3. Select the matching iPhone once list appears. 
4. Get the price of the selected iPhone. 
5. Now, go to https://www.flipkart.com/. 
6. Repeat steps 2 to 4 and get the price. 
7. Compare the price on both the website and determine which website has lesser value for the iPhone 
   and print the final result on the console. 
*
* @author Ankush Khanna
*/
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import in.novopay.platform_ui.utils.BasePage;

public class Assignment1Page extends BasePage {

	public Assignment1Page(WebDriver wdriver) {
		super(wdriver);
		PageFactory.initElements(wdriver, this);
	}

	@FindBy(xpath = "//a[@aria-label='Amazon']")
	WebElement amazonPageTitle;

	@FindBy(id = "twotabsearchtextbox")
	WebElement amazonSearchField;

	@FindBy(id = "nav-search-submit-button")
	WebElement amazonSearchButton;

	@FindBy(id = "productTitle")
	WebElement amazonProductTitle;

	@FindBy(id = "priceblock_ourprice")
	WebElement amazonProductPrice;

	@FindBy(xpath = "//button[contains(text(),'✕')]")
	WebElement flipkartCloseButton;

	@FindBy(xpath = "//img[@title='Flipkart']")
	WebElement flipkartPageTitle;

	@FindBy(xpath = "//input[contains(@title,'Search')]")
	WebElement flipkartSearchField;

	@FindBy(xpath = "//button[@type='submit']")
	WebElement flipkartSearchButton;

	@FindBy(xpath = "//span[contains(text(),'Filters')]")
	WebElement flipkartFilters;

	public void assignment1(Map<String, String> usrData) throws ClassNotFoundException, InterruptedException {

		try {
			// Opening Amazon website to get the price of iPhone
			wdriver.get("https://www.amazon.in/");
			waitUntilElementIsVisible(amazonPageTitle);
			System.out.println("Amazon page displayed");

			waitUntilElementIsClickableAndClickTheElement(amazonSearchField);
			amazonSearchField.sendKeys(usrData.get("AMAZON_MODEL_NAME"));
			waitUntilElementIsClickableAndClickTheElement(amazonSearchButton);

			String amazonListItemXpath = "//a[contains(@class,'text-normal')]//span[contains(text(),'"
					+ usrData.get("AMAZON_MODEL_NAME") + "')]";
			waitUntilElementIsClickableAndClickTheElement(wdriver.findElement(By.xpath(amazonListItemXpath)));

			ArrayList<String> amazonTab = new ArrayList<String>(wdriver.getWindowHandles());
			wdriver.switchTo().window(amazonTab.get(1)); // switch to next tab
			waitUntilElementIsVisible(amazonProductTitle);

			saveOrGetPrice("Amazon", "SavePrice", replaceSymbols(amazonProductPrice.getText()));
			System.out.println("Price of " + usrData.get("AMAZON_MODEL_NAME") + " is "
					+ replaceSymbols(amazonProductPrice.getText()));

			wdriver.close(); // close the tab
			wdriver.switchTo().window(amazonTab.get(0)); // switch to previous window

			// Opening Flipkart website to get the price of iPhone
			wdriver.get("https://www.flipkart.com/");
			waitUntilElementIsClickableAndClickTheElement(flipkartCloseButton);

			waitUntilElementIsVisible(flipkartPageTitle);
			System.out.println("Flipkart page displayed");

			waitUntilElementIsClickableAndClickTheElement(flipkartSearchField);
			flipkartSearchField.sendKeys(usrData.get("FLIPKART_MODEL_NAME"));
			waitUntilElementIsClickableAndClickTheElement(flipkartSearchButton);

			waitUntilElementIsVisible(flipkartFilters);

			String listItemXpath = "//div[contains(text(),'" + usrData.get("FLIPKART_MODEL_NAME") + "')]";
			waitUntilElementIsClickableAndClickTheElement(wdriver.findElement(By.xpath(listItemXpath)));

			ArrayList<String> flipkartTab = new ArrayList<String>(wdriver.getWindowHandles());
			wdriver.switchTo().window(flipkartTab.get(1));
			String productTitleXpath = "//h1/span[contains(text(),'" + usrData.get("FLIPKART_MODEL_NAME") + "')]";
			waitUntilElementIsVisible(wdriver.findElement(By.xpath(productTitleXpath)));

			String productPriceXpath = "//h1/span[contains(text(),'" + usrData.get("FLIPKART_MODEL_NAME")
					+ "')]/parent::h1/parent::div/following-sibling::div[3]/div/div/div";
			WebElement productPrice = wdriver.findElement(By.xpath(productPriceXpath));
			saveOrGetPrice("Flipkart", "SavePrice", replaceSymbols(productPrice.getText()));
			System.out.println(
					"Price of " + usrData.get("FLIPKART_MODEL_NAME") + " is " + replaceSymbols(productPrice.getText()));

			double amazonPrice = Double.parseDouble(saveOrGetPrice("Amazon", "GetPrice", ""));
			double flipkartPrice = Double.parseDouble(saveOrGetPrice("Flipkart", "GetPrice", ""));
			DecimalFormat df = new DecimalFormat("#.00");
			String lowerValue = df.format(Math.min(amazonPrice, flipkartPrice));
			if (lowerValue.equals(saveOrGetPrice("Amazon", "GetPrice", ""))) {
				System.out.println("------------\nAmazon has lesser iPhone price: "
						+ saveOrGetPrice("Amazon", "GetPrice", "") + "\n------------");
			} else if (lowerValue.equals(saveOrGetPrice("Flipkart", "GetPrice", ""))) {
				System.out.println("------------\nFlipkart has lesser iPhone price: "
						+ saveOrGetPrice("Flipkart", "GetPrice", "") + "\n------------");
			}
			
			wdriver.close(); // close the tab
			wdriver.switchTo().window(flipkartTab.get(0)); // switch to previous window
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Test Case Failed");
			Assert.fail();
		}
	}

}
