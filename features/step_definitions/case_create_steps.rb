And /^I switch to full form$/ do
  case_page.scrollToNextFields
  case_page.scrollToNextFields
  case_page.switchToFullForm
  sleep 5
end

And /^I scroll to "(.*?)" form$/ do |form_name|
  $current_form = case_page.getCurrentFormName
  until $current_form == form_name do
    case_page.scrollToNextForm
    $current_form = case_page.getCurrentFormName
  end
end

When /^I fill in the following:$/ do |table|
  table.rows_hash.each do |field, value|
    until base_page.ifTextExist(field) do
      case_page.scrollToNextFields
    end
    case_page.fillInForm(field, value)
  end
end

When /^I click the case$/ do
  base_page.clickByXpath("//android.widget.RelativeLayout[@clickable='true']")
end


Then /^I should see a case with sex "(.*?)" and age "(.*?)"$/ do |sex, age|
  base_page.verifyTextExist(sex)
  base_page.verifyTextExist(age)
end


Then /^I should see a button named "(.*?)"$/ do |button_name|
  base_page.verifyButtonExist(button_name)
end

When /^I press the button named "(.*?)"$/ do |button_name|
  until base_page.ifTextExist(button_name) do
    case_page.scrollToNextFields
  end
  base_page.clickByXpath("//android.widget.Button[@text='#{button_name}']")
end




