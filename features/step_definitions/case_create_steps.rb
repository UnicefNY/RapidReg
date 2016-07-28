And /^I switch to full form$/ do
  base_page.clickByXpath("//android.widget.Button[@text='Show more details']")
end

And /^I scroll to "(.*?)" form$/ do |form_name|
  $current_form = case_page.getCurrentFormName
  puts $current_form
  until $current_form == form_name do
    case_page.scrollToNextForm
    $current_form = case_page.getCurrentFormName
    puts $current_form
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
  base_page.findByXpath("//android.widget.Button[@text='#{button_name}']")
end

When /^I press the button named "(.*?)"$/ do |button_name|
  base_page.clickByXpath("//android.widget.Button[@text='#{button_name}']")
end




