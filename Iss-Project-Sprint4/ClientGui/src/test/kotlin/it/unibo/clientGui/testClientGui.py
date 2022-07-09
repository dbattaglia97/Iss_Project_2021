from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Per creare una sessione con Chrome
driver = webdriver.Chrome()
driver.implicitly_wait(30)
driver.maximize_window()

# Per aprire un’applicazione web
driver.get("http://localhost:8081/")


#Button parking request
l=driver.find_element_by_xpath("/html/body/div[2]/form[1]/center/div/button");
l.click();

driver.implicitly_wait(30)

#Wait until slotnum appears
WebDriverWait(driver, 10).until(EC.text_to_be_present_in_element((By.ID, "infoDisplay"), 'SLOTNUM:'))


#Carenter request
l=driver.find_element_by_xpath("/html/body/div[2]/form[2]/center/div/button");
l.click();

driver.implicitly_wait(30)



#Wait until slotnum appears
WebDriverWait(driver, 10).until(EC.text_to_be_present_in_element((By.ID, "infoDisplay"), 'TOKEN:'))

token= driver.find_element(By.ID, "infoDisplay").text


driver.implicitly_wait(4000)


#Pickup request
element_enter.findElement(By.xpath("/html/body/div[2]/form[3]/input")).sendKeys(token);

WebDriverWait(driver, 10).until(EC.text_to_be_present_in_element((By.ID, "adv"), 'Token sended'))



# Per chiudere la finestra del browser
driver.quit()
