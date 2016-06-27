And /^I switch to full form$/ do
  case_page.switchToFullForm
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
    if base_page.ifTextExist(field)
      case_page.fillInForm(field, value)
    else
      case_page.scrollToNextFields
      case_page.fillInForm(field, value)
    end
  end
end

When /^I click the case$/ do
  base_page.clickByXpath("//android.support.v7.widget.RecyclerView/android.widget.RelativeLayout[@index=0]")
end

Then /^I should see values on the page for the following:$/ do |table|
  table.rows_hash.each do |field,value|
    base_page.verifyTextExist(value)
  end
end


Then /^I should see a case with sex "(.*?)" and age "(.*?)"$/ do |sex, age|
  base_page.verifyTextExist(sex)
  base_page.verifyTextExist(age)
end



=begin
type: android.widget.ViewSwitcher
text:
index: 0
=end


