package in.novopay.platform_ui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

	public Object[][] returnAllUniqueValuesInMap(String workbookName, String sheetName, String testType) {

		List<HashMap<String, String>> listValues = returnRowsUniqueValueBasedOnTestTypeList(workbookName, sheetName,
				testType);

		Object[][] allValues = new Object[listValues.size()][1];

		for (int i = 0; i < listValues.size(); i++) {
			allValues[i][0] = listValues.get(i);
		}
		return allValues;
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

	// Save Or Get the Price from ini file
	public String saveOrGetPrice(String company, String type, String price) {
		Ini ini;
		try {
			ini = new Ini(new File("./data.ini"));
			if (company.equalsIgnoreCase("Amazon")) {
				if (type.equalsIgnoreCase("SavePrice")) {
					ini.put("ProductData", "AmazonPrice", price);
					ini.store();
					return ini.get("ProductData", "AmazonPrice");
				} else if (type.equalsIgnoreCase("GetPrice")) {
					return ini.get("ProductData", "AmazonPrice");
				}
			} else if (company.equalsIgnoreCase("Flipkart")) {
				if (type.equalsIgnoreCase("SavePrice")) {
					ini.put("ProductData", "FlipkartPrice", price);
					ini.store();
					return ini.get("ProductData", "FlipkartPrice");
				} else if (type.equalsIgnoreCase("GetPrice")) {
					return ini.get("ProductData", "FlipkartPrice");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Remove rupee symbol and comma from the string
	public String replaceSymbols(String value) {
		String editedElement = value.replaceAll("???", "").replaceAll(",", "").trim();
		return editedElement;
	}

	@Override
	protected void isLoaded() throws Error {

	}

	@Override
	protected void load() {

	}
}