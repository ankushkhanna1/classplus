package in.novopay.platform_ui.pages.web;
//Assignment 2
/**
1. Go to https://www.tripadvisor.in/ 
2. Once page is loaded, search for “Club Mahindra”. 
3. Click on the first result that appears in the list. 
4. Click on Write a Review. 
5. On “Your overall rating” section, hover over the stars and click on the fifth star.
   Your code should actually do the hover and make sure the stars inside get lit up when you hover over them,
   then click on the fifth star. 
6. Write some random text in “Title of your review” and “Your review” section.
7. Check if “Hotel Ratings” section is available, if available repeat step 5.
8. Select checkbox in ‘Submit your review’ section.
*
* @author Ankush Khanna
*/
import java.util.ArrayList;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import in.novopay.platform_ui.utils.BasePage;

public class Assignment2Page extends BasePage {

	public Assignment2Page(WebDriver wdriver) {
		super(wdriver);
		PageFactory.initElements(wdriver, this);
	}

	@FindBy(xpath = "//img[@alt='Tripadvisor']")
	WebElement pageTitle;

	@FindBy(xpath = "//input[@type='search'][@placeholder='Where to?']")
	WebElement searchField;

	@FindBy(xpath = "//div[@data-test-attribute='typeahead-SINGLE_SEARCH_HERO']//button[@title='Search']")
	WebElement searchButton;

	@FindBy(xpath = "//div[@class='search-results-list']//div[@data-widget-type='TOP_RESULT'][1]//div[@class='result-title']")
	WebElement firstItemInList;

	@FindBy(xpath = "//h1[@id='HEADING']")
	WebElement hotelTitle;

	@FindBy(xpath = "//a[text()='Write a review'][contains(@class,'button')]")
	WebElement writeAReviewButton;

	@FindBy(xpath = "//div[@class='labelHeader'][contains(text(),'Your overall rating')]")
	WebElement overallRatingLabel;

	@FindBy(xpath = "//span[@id='bubble_rating'][contains(@class,'bubble_50')]")
	WebElement fiveStarRating;

	@FindBy(id = "ReviewTitle")
	WebElement reviewTitle;

	@FindBy(id = "ReviewText")
	WebElement reviewText;

	@FindBy(xpath = "//div[@class='labelHeader'][contains(text(),'Hotel Ratings')]")
	WebElement hotelRatingsLabel;
	
	@FindBy(xpath = "//input[@type='checkbox']")
	WebElement checkbox;


	public void assignment2(Map<String, String> usrData) throws ClassNotFoundException, InterruptedException {

		try {
			// Opening Amazon website to get the price of iPhone
			wdriver.get("https://www.tripadvisor.in/");
			waitUntilElementIsVisible(pageTitle);
			System.out.println("Tripadvisor page displayed");

			waitUntilElementIsClickableAndClickTheElement(searchField);
			searchField.sendKeys(usrData.get("HOTEL_NAME"));
			waitUntilElementIsClickableAndClickTheElement(searchButton);

			waitUntilElementIsClickableAndClickTheElement(firstItemInList);

			ArrayList<String> tabs1 = new ArrayList<String>(wdriver.getWindowHandles());
			wdriver.switchTo().window(tabs1.get(1)); // switch to next tab
			waitUntilElementIsVisible(hotelTitle);

			waitUntilElementIsClickableAndClickTheElement(writeAReviewButton);
			System.out.println("Write a review button clicked");
			wdriver.switchTo().window(tabs1.get(1)); // switch to same tab
			wdriver.close(); // close the tab
			wdriver.switchTo().window(tabs1.get(0)); // switch to previous window
			ArrayList<String> tabs2 = new ArrayList<String>(wdriver.getWindowHandles());
			wdriver.switchTo().window(tabs2.get(1)); // switch to next tab
			waitUntilElementIsVisible(overallRatingLabel);
			rating("//span[@id='bubble_rating']");
			System.out.println("Fifth star selected");
			waitUntilElementIsVisible(fiveStarRating);
			System.out.println("All 5 stars got lit up");
			
			waitUntilElementIsClickableAndClickTheElement(reviewTitle);
			reviewTitle.sendKeys(usrData.get("REVIEW_TITLE"));
			System.out.println("Review title written");

			waitUntilElementIsClickableAndClickTheElement(reviewText);
			reviewText.sendKeys(usrData.get("REVIEW_TEXT"));
			System.out.println("Review text written");

			if (hotelRatingsLabel.isDisplayed()) {
				for (int i = 1; i <= 3; i++) {
					String hotelRating = "//div[@class='ratingBubbleTable']/div[" + i
							+ "]//span[contains(@id,'bubbles')]";
					rating(hotelRating);
					String fiveStarRating = "//div[@class='ratingBubbleTable']/div[" + i
							+ "]//span[contains(@id,'bubbles')][contains(@class,'bubble_50')]";
					WebElement hotelRatingLit = wdriver.findElement(By.xpath(fiveStarRating));
					System.out.println("Fifth star selected");
					waitUntilElementIsVisible(hotelRatingLit);
					System.out.println("All 5 stars got lit up");
				}
			}
			waitUntilElementIsClickableAndClickTheElement(checkbox);
			System.out.println("Checkbox selected");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Test Case Failed");
			Assert.fail();
		}
	}

	public void rating(String xpath) {
		Actions action = new Actions(wdriver);
		action.moveToElement(
				new WebDriverWait(wdriver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath))),
				50, 0).click().build().perform();
	}
}
