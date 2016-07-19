And /^I search for:$/ do |table|
  table.rows_hash.each do |field, value|
    case_page.fillInSearchField(field, value)
  end
end

Then /^I should see following information:$/ do |table|
  table.rows_hash.each do |text|
    base_page.verifyTextExist(text)
  end
end

And /^I create case "(.*?)"$/ do |case_instance|
  base_page.clickById("fab_expand_menu_button")
  base_page.clickById("add_case")
  base_form.fillInMiniForm(test_case[case_instance])
  base_page.clickById("save")
end

And /^I clear up above "(.*?)" search conditions$/ do |num|
  num.to_i.times { base_page.clickById("clear_text") }
end

