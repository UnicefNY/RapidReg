And /^I switch to full form$/ do
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

Then /^I should see following:$/ do |table|
  table.rows_hash.each do |field, value|
    until base_page.ifTextExist(field) do
      case_page.scrollToNextFields
    end
    case_page.verifyFormValue(field,value)
  end
end

When /^I press the Back button$/ do
  sleep 5
  base_page.pressBackButton
end




