package in.novopay.platform_ui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.testng.Reporter;
import org.testng.SkipException;

@SuppressWarnings("rawtypes")
public class JavaUtils extends LoadableComponent {
	public static HashMap<String, String> configProperties = new HashMap<String, String>();

	/* Read the properties file and returns a 'Value' for a particular 'Key' */
	public HashMap<String, String> readConfigProperties() {
		String sectionName = null;
		Set<Entry<String, String>> dataSet;
		Ini ini;
		try {
			ini = new Ini(new File("./config.ini"));

			Ini.Section section = ini.get("Common");
			dataSet = section.entrySet();

			sectionName = section.get("configName");
			section = ini.get(sectionName);

			dataSet.addAll(section.entrySet());
			for (Map.Entry<String, String> set : dataSet) {
				configProperties.put(set.getKey().toString(), set.getValue().toString());
			}
			return configProperties;
		} catch (InvalidFileFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String checkExecutionStatus(String workbook, String sheetName, String testCaseID) {

		HashMap<String, String> testRow = readExcelData(workbook, sheetName, testCaseID);

		/*
		 * Checks the execution status of the current testCaseID which is set in the
		 * Excel - TestData sheet if marked 'Yes' testCase would execute , else testCase
		 * would skip
		 */
		if (testRow.get("Execution Status").toLowerCase().equalsIgnoreCase("no")) {
			throw new SkipException(
					"Skipping the test flow with ID " + testCaseID + " as it marked 'NO' in the Execution Excel Sheet");
		}

		Reporter.log("\nExecuting the " + testRow.get("Test Description") + " : " + testCaseID, true);
		return testCaseID;
	}

	/* Returns the values in column1 of the TestData in an ArrayList */
	public ArrayList<String> returnRowsUniqueValueBasedOnClassName(String sheetName, Class<?> className) {

		String[] clsParts = className.getName().split("\\.");
		String clsName = clsParts[(clsParts.length) - 1];
		// String[] allValues = null;
		ArrayList<String> allValues = new ArrayList<String>();
		try {
			FileInputStream file = new FileInputStream("./test-data/SLIUITestData.xlsx");
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetName);
			Iterator<Row> it = sheet.rowIterator();

			while (it.hasNext()) {

				Row record = it.next();
				String cellValue = record.getCell(1).toString() + "".trim();
				if (cellValue.equalsIgnoreCase(clsName)) {
					allValues.add(record.getCell(0).toString() + "".trim());
				}
			}
			return allValues;
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}
	}

	/*
	 * return List of HashMap with data read from excel sheet
	 */
	public List<HashMap<String, String>> returnRowsUniqueValueBasedOnTestTypeList(String workbookName, String sheetName,
			String testType) {

		HashMap<String, String> dataMap = new HashMap<String, String>();
		List<HashMap<String, String>> allValues = new ArrayList<HashMap<String, String>>();
		try {
			FileInputStream file = new FileInputStream(configProperties.get(workbookName));
			if (file != null) {
				System.out.println();
			}
			System.out.println(configProperties.get(workbookName));
			String key, value;
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetName);
			boolean flag = false;
			Iterator<Row> it = sheet.rowIterator();
			int i = 0;
			Row headers = it.next();
			while (it.hasNext()) {

				Row record = it.next();

				if ((record.getCell(3).toString().trim() + "").equalsIgnoreCase("yes")) {
					if (testType.equalsIgnoreCase("no-check")) {
						flag = true;
					} else if ((record.getCell(1).toString().trim() + "").equalsIgnoreCase(testType)) {
						flag = true;
					}

				}
				if (flag == true) {
					for (i = 0; i < headers.getLastCellNum(); i++) {
						record.getCell(i);
						if ((null != record.getCell(i)) && (record.getCell(i).getCellType() == CellType.NUMERIC)) {
							if (DateUtil.isCellDateFormatted(record.getCell(i))) {

								DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

								value = dateFormat.format(record.getCell(i).getDateCellValue()).trim();

							} else {
								record.getCell(i).toString();

								value = record.getCell(i).toString().trim();
							}
							key = headers.getCell(i).toString().trim();

						} else {

							key = (headers.getCell(i) + "".toString()).trim() + "";
							value = (null != record.getCell(i)) ? (record.getCell(i) + "".toString()).trim() + "" : "";
						}
						dataMap.put(key, value);
						// System.out.println(key+" : "+value);
					}
					allValues.add(dataMap);
					dataMap = new HashMap<String, String>();

				}
				flag = false;
			}

			return allValues;

		} catch (NullPointerException e) {
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}

	}

	//

	public HashMap<Integer, String[]> returnRowsUniqueValueBasedOnClassNameList(String sheetName, Class<?> className) {

		String[] clsParts = className.getName().split("\\.");
		String clsName = clsParts[(clsParts.length) - 1];
		// String[] allValues = null;

		HashMap<Integer, String[]> allValues = new HashMap<Integer, String[]>();
		try {
			FileInputStream file = new FileInputStream("./test-data/TestData.xlsx");
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetName);
			Iterator<Row> it = sheet.rowIterator();
			int i = 0;
			while (it.hasNext()) {

				Row record = it.next();
				String cellValue = record.getCell(1).toString() + "";
				if (cellValue.equalsIgnoreCase(clsName)) {
					allValues.put(i, new String[] { record.getCell(0).toString(), record.getCell(5).toString(),
							record.getCell(6).toString(), record.getCell(7).toString() });
					i++;
				}
			}
			return allValues;
		} catch (NullPointerException e) {
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}
	}
	//

	/*
	 * Returns the ArrayList to Two-Dimensional Object array for dataProvider
	 * Iteration
	 */
	public Object[][] returnAllUniqueValues(String sheetName, Class<?> className) {

		ArrayList<String> listValues = returnRowsUniqueValueBasedOnClassName(sheetName, className);

		Object[][] allValues = new Object[listValues.size()][1];
		for (int i = 0; i < listValues.size(); i++) {
			allValues[i][0] = listValues.get(i);
		}
		return allValues;
	}

	public Object[][] returnAllUniqueValuesInArray(String sheetName, Class<?> className) {

		HashMap<Integer, String[]> listValues = returnRowsUniqueValueBasedOnClassNameList(sheetName, className);

		Object[][] allValues = new Object[listValues.size()][];

		for (int i = 0; i < listValues.size(); i++) {
			allValues[i] = new Object[listValues.get(i).length];
			allValues[i] = listValues.get(i);
		}

		return allValues;
	}

	public Object[][] returnAllUniqueValuesInMap(String workbookName, String sheetName, String testType) {

		List<HashMap<String, String>> listValues = returnRowsUniqueValueBasedOnTestTypeList(workbookName, sheetName,
				testType);

		Object[][] allValues = new Object[listValues.size()][1];

		for (int i = 0; i < listValues.size(); i++) {
			allValues[i][0] = listValues.get(i);
		}
		return allValues;
	}

	/*
	 * Puts all the excels rows from startRowValue to endRowValue and returns
	 * Two-Dimensional Object array for dataProvider Iteration
	 */
	public Object[][] returnRowsUniqueValueInArray(String sheetName, String startRowValue, String endRowValue) {

		Object[][] values = new String[3][1];
		try {
			FileInputStream file = new FileInputStream("./test-data/TestData.xlsx");
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetName);
			Iterator<Row> it = sheet.rowIterator();

			while (it.hasNext()) {

				Row record = it.next();
				String cellValue = record.getCell(0).toString();
				if (cellValue.equalsIgnoreCase(startRowValue)) {
					int j = 0;

					while (!(record.getCell(0).toString().equalsIgnoreCase(endRowValue))) {
						values[j][0] = record.getCell(0).toString();
						j++;
						record = it.next();
					}
					values[j][0] = record.getCell(0).toString();
					break;
				}
				break;
			}
		} catch (NullPointerException e) {
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}

		return values;
	}

	public HashMap<String, String> readExcelData(String workbook, String sheetname, String uniqueValue) {
		try {
			String key, value;
			FileInputStream file = new FileInputStream(configProperties.get(workbook));
			HashMap<String, String> dataMap = new HashMap<String, String>();
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetname);
			Iterator<Row> it = sheet.rowIterator();

			Row headers = it.next();
			while (it.hasNext()) {
				Row record = it.next();
				String cellValue = record.getCell(0).toString().trim();
				if (cellValue.equalsIgnoreCase(uniqueValue)) {
					for (int i = 0; i < headers.getLastCellNum(); i++) {
						record.getCell(i);
						if ((null != record.getCell(i)) && (record.getCell(i).getCellType() == CellType.NUMERIC)) {
							if (DateUtil.isCellDateFormatted(record.getCell(i))) {
								DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								value = dateFormat.format(record.getCell(i).getDateCellValue()).trim();
							} else {
								String cellText = record.getCell(i).toString();
								if (cellText.endsWith(".0")) {
									value = cellText.substring(0, cellText.length() - 2).trim();
								} else {
									value = cellText.trim();
								}
							}
							key = headers.getCell(i).toString().trim();
						} else {
							key = (headers.getCell(i) + "".toString()).trim() + "";
							value = (null != record.getCell(i)) ? (record.getCell(i) + "".toString()).trim() + "" : "";
						}
						dataMap.put(key, value);
					}
					break;
				}
			}
			return dataMap;
		} catch (NullPointerException e) {
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}
	}

	public String getTodaysDate(String format) {
		Format formatter = new SimpleDateFormat(format);
		String todaysDate = formatter.format(new Date());
		return todaysDate;
	}

	/*
	 * Writes the API Execution details by creating new sheet for every run to Excel
	 * Report File, Iterates through the cells for a particular testcaseID and
	 * populates the data
	 */
	public void writeExecutionStatusToExcel(String[] APIExecutionDetails) throws InvalidFormatException, IOException {

		try {
			int rowToUpdate = 0;
			File file = new File(configProperties.get("testReport"));
			if (!(file.exists())) {
				file.createNewFile();
				Workbook workbook = new HSSFWorkbook();
				Sheet worksheet = workbook.createSheet(configProperties.get("reportSheetName"));
				Row headers = worksheet.createRow(0);

				headers.createCell(0).setCellValue("SERVER BUILD NUMBER");
				headers.createCell(1).setCellValue("CLIENT BUILD NUMBER");
				headers.createCell(2).setCellValue("FLOW NAME");
				headers.createCell(3).setCellValue("TCID");
				headers.createCell(4).setCellValue("TEST DESCRIPTION");
				headers.createCell(5).setCellValue("RESULT");
				headers.createCell(6).setCellValue("(WARNING) REASON OF FAILURE");
				headers.createCell(7).setCellValue("TEST START TIME");
				headers.createCell(8).setCellValue("TEST END TIME");
				FileOutputStream fileOut = new FileOutputStream(file);
				workbook.write(fileOut);
				workbook.close();
				fileOut.close();
			}
			FileInputStream fileIn = new FileInputStream(file);
			Workbook workbook = WorkbookFactory.create(fileIn);
			Sheet worksheet = workbook.getSheet(configProperties.get("reportSheetName"));
			rowToUpdate = worksheet.getLastRowNum() + 1;
			int i;
			Row record = worksheet.createRow(rowToUpdate);
			Cell cell = null;
			for (i = 0; i < APIExecutionDetails.length; i++) {
				cell = record.createCell(i);
				cell.setCellValue(APIExecutionDetails[i]);
			}

			FileOutputStream fileOut = new FileOutputStream(new File(configProperties.get("testReport")));
			workbook.write(fileOut);
			workbook.close();
			fileOut.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Returns the test case execution status based on its execution status code
	 */
	public String getExecutionResultStatus(int statusCode) {

		String testStatus = null;
		if (statusCode == 1) {
			testStatus = "PASS";
		} else if (statusCode == 2) {
			testStatus = "FAIL";
		} else if (statusCode == 3) {
			testStatus = "SKIP";
		}

		return testStatus;
	}

	/*
	 * Returns the test case execution time
	 */
	public String getTestExcutionTime(long millisec) {
		String dateFormat = "dd-MM-yyyy hh:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millisec);
		String executionTime = simpleDateFormat.format(calendar.getTime());
		return executionTime;
	}

	@Override
	protected void isLoaded() throws Error {

	}

	@Override
	protected void load() {

	}
}