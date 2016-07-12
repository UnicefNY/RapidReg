Then /^I should see the case's "(.*?)" is "(.*?)"$/ do |case_field, case_value|
  if case_value == "Today's date"
    time = Time.now
    case_value = time.strftime("%b %d, %Y")
  end
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

And /^I edit the value of "(.*?)" from "(.*?)" to "(.*?)"$/ do |field, old_value, new_value|
  until base_page.ifTextExist(field) do
    case_page.scrollToNextFields
  end
  case_page.editForm(field, old_value, new_value)
end