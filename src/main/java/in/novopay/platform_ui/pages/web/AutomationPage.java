package in.novopay.platform_ui.pages.web;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import in.novopay.platform_ui.utils.BasePage;

public class AutomationPage extends BasePage {

	public AutomationPage(WebDriver wdriver) {
		super(wdriver);
		PageFactory.initElements(wdriver, this);
	}

	@FindBy(xpath = "//a[@title='Women']")
	WebElement womenButton;

	@FindBy(id = "search_query_top")
	WebElement searchField;

	@FindBy(xpath = "//a[@title='Return to Home']")
	WebElement homeButton;

	@FindBy(xpath = "//div[@class='right-block']//span[@itemprop='price'][contains(text(),'$27')]/parent::div/following-sibling::div/a[@title='Add to cart']")
	WebElement addToCart;

	@FindBy(xpath = "//i[@class='icon-ok']")
	WebElement icon;

	@FindBy(xpath = "//span[contains(text(),'Proceed to checkout')]")
	WebElement proceedCheckout;

	@FindBy(xpath = "//*[@id='our_price_display']")
	WebElement displayPrice;
	
	@FindBy(xpath = "//span[@class='navigation_page'][contains(text(),'Your shopping cart')]")
	WebElement yourShoppingCart;
	
	@FindBy(xpath = "//span[@class='navigation_page'][contains(text(),'Authentication')]")
	WebElement authentication;
	
	@FindBy(xpath = "//span[@class='navigation_page'][contains(text(),'Addresses')]")
	WebElement addresses;
	
	@FindBy(xpath = "//button/span[contains(text(),'Proceed to checkout')]")
	WebElement proceedToCheckout;
	
	@FindBy(xpath = "//span[@class='navigation_page'][contains(text(),'Shipping')]")
	WebElement shipping;

	@FindBy(xpath = "//*[@id='color_to_pick_list']/li/a[@name='White']")
	WebElement color;

	@FindBy(xpath = "//label[contains(text(),'Size')]/following-sibling::div//select")
	WebElement size;

	@FindBy(xpath = "//button/span[contains(text(),'Add to cart')]")
	WebElement addToCartButton;

	@FindBy(xpath = "//p/a[@title='Proceed to checkout']/span[contains(text(),'Proceed to checkout')]")
	WebElement checkout;

	@FindBy(xpath = "//*[@type='checkbox']")
	WebElement checkbox;

	@FindBy(xpath = "//p[@class='fancybox-error']")
	WebElement error;
	
	@FindBy(xpath = "//*[@title='Close']")
	WebElement close;

	@FindBy(xpath = "//span[@class='navigation_page'][contains(text(),'Your payment method')]")
	WebElement payment;
	
	@FindBy(id = "email")
	WebElement email;

	@FindBy(id = "passwd")
	WebElement password;

	@FindBy(id = "SubmitLogin")
	WebElement SubmitLogin;

	public void automation(Map<String, String> usrData) throws ClassNotFoundException, InterruptedException {

		waitUntilElementIsVisible(searchField);
		System.out.println("My Store page displayed");

		try {
			waitUntilElementIsClickableAndClickTheElement(womenButton);
			System.out.println("Women button clicked");
			waitUntilElementIsVisible(homeButton);
			System.out.println("Women page is displayed");

			Actions actions = new Actions(wdriver);
			String priceXpath = "//div[@class='right-block']//span[@itemprop='price'][contains(text(),'$"
					+ usrData.get("PRICE") + "')]";
			WebElement price = wdriver.findElement(By.xpath(priceXpath));
			actions.moveToElement(price).perform();
			System.out.println("Mouse hovered on Product with $27 price");

			String quickViewXpath = "//div[@class='left-block']//span[@itemprop='price'][contains(text(),'$"
					+ usrData.get("PRICE") + "')]/parent::div/preceding-sibling::a/img";
			WebElement quickView = wdriver.findElement(By.xpath(quickViewXpath));
			waitUntilElementIsClickableAndClickTheElement(quickView);
			System.out.println("Clicked on Product's QuickView part");

			waitUntilElementIsVisible(displayPrice);
			Assert.assertEquals(displayPrice.getText(), "$" + usrData.get("PRICE") + ".00");
			System.out.println("Display Price is " + displayPrice.getText());

			int rowCount = wdriver.findElements(By.xpath("//*[@id='color_to_pick_list']/li")).size();
			if (rowCount>1) {
				waitUntilElementIsClickableAndClickTheElement(color);
				System.out.println("Another color selected");
			}
			
			Select sizeOption = new Select(size);
			sizeOption.selectByVisibleText("M");
			System.out.println("Size selected");
			
			waitUntilElementIsClickableAndClickTheElement(addToCartButton);
			System.out.println("Add to cart button clicked");
			
			waitUntilElementIsVisible(icon);
			System.out.println("Pop-up displayed");

			waitUntilElementIsClickableAndClickTheElement(proceedCheckout);
			System.out.println("Proceed to Checkout button clicked");
			
			waitUntilElementIsVisible(yourShoppingCart);
			System.out.println("Cart page displayed");
			
			waitUntilElementIsClickableAndClickTheElement(checkout);
			System.out.println("Proceed to Checkout button clicked");
			
			waitUntilElementIsVisible(authentication);
			System.out.println("Authentication page displayed");
			
			email.sendKeys(usrData.get("EMAIL"));
			password.sendKeys(usrData.get("PASSWORD"));
			
			waitUntilElementIsClickableAndClickTheElement(SubmitLogin);
			
			waitUntilElementIsVisible(addresses);
			System.out.println("Address page displayed");
			
			waitUntilElementIsClickableAndClickTheElement(proceedToCheckout);
			System.out.println("Proceed to Checkout button clicked");
			
			waitUntilElementIsVisible(shipping);
			System.out.println("Shipping page displayed");
			
			waitUntilElementIsClickableAndClickTheElement(proceedToCheckout);
			System.out.println("Proceed to Checkout button clicked");
			
			waitUntilElementIsClickableAndClickTheElement(error);
			System.out.println(error.getText());
			
			waitUntilElementIsClickableAndClickTheElement(close);
			System.out.println("Pop-up closed");
			
			waitUntilElementIsClickableAndClickTheElement(checkbox);
			System.out.println("Checkbox selected");
			
			waitUntilElementIsClickableAndClickTheElement(proceedToCheckout);
			System.out.println("Proceed to Checkout button clicked");
			
			waitUntilElementIsVisible(payment);
			System.out.println("Payments page displayed");
			System.out.println("End of the test case");
		} catch (Exception e) {
			wdriver.navigate().refresh();
			e.printStackTrace();
			System.out.println("Test Case Failed");
			Assert.fail();
		}
	}

}
