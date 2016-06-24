When /^I press "New Case" button$/ do
  case_page.createNewCase
end

And /^I scroll to "(.*?)" form$/ do |form_name|
  $current_form = case_page.getCurrentFormName
  until $current_form == form_name  do
    case_page.scrollToNextForm
    $current_form = case_page.getCurrentFormName
  end
end

When /^I fill in the following:$/ do |table|
  table.rows_hash.each do |field, value|
    if base_page.ifTextExist(field)
      case_page.fill_in(field, value)
    else
      case_page.scrollToNextFields
      case_page.fill_in(field, value)
    end
  end
end

And /^I press "SAVE" button$/ do
  case_page.saveCase
end

Then /^I should see a case with sex "(.*?)" and age "(.*?)"$/ do |sex,age|
  base_page.verifyTextExist(sex)
  base_page.verifyTextExist(age)
end






