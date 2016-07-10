Then /^I should see the case's "(.*?)" is "(.*?)"$/ do |case_field, case_value|
  actual_value = base_page.findById(case_field).text
  raise("NOT find value: #{case_value}") unless actual_value == case_value
end

Then /^I should not see "(.*?)"$/ do |text|
  base_page.verifyTextNotExist(text)
end

Then /^I should see the first case is a "(.*?)"$/ do |case_gender|
  actual_gender = base_page.findById("gender_name").text
  raise("NOT right gender") unless actual_gender == case_gender
end

When /I order by "Age descending age"$/ do
  base_page.clickById("order_spinner")
  base_page.clickByXpath("//android.widget.ListView[1]/android.widget.RelativeLayout[2]")
end

When /^I order by "Registration date descending order"$/ do
  base_page.clickById("order_spinner")
  base_page.clickByXpath("//android.widget.ListView[1]/android.widget.RelativeLayout[4]")
end